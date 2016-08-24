/*
 * Simulation.js v1.0
 *
 * This file contains source code developed by the European
 * FP7 research project BIOMICS (Grant no. 318202)
 * Copyright (C) 2016 Daniel Schreckling
 *
 * Licensed under the Academic Free License version 3.0
 *   http://www.opensource.org/licenses/afl-3.0.php
 *
 *
 */

var uuid = require("node-uuid");
var async = require("async");
var q = require("q");

var ASIM = require("./ASIM");
var ASIMState = require("./ASIMState");
var SimulationState = require("./SimulationState");
var ASIMCreationError = require("./ASIMCreationError");

var Simulation = (function() {

    var cls = function(manager) {
        this.manager = manager;

        this.status = SimulationState.EMPTY;

        this.asimList = {};
        this.channelList = {};
        this.schedulerList = {};
        this.id = uuid.v4();

        this.registeredLocations = {};

        this.locationUpdates = {};
    };

    // TODO REMOVE AND UNLOAD ALL ASIMS BEFORE RESETTING OR DELETION

    cls.prototype = {
        reset : function() {
            this.asimList = {};
            this.schedulerList = {};
            this.channelList = {};
        },

        getUpdates : function() {
            return this.locationUpdates;
        },

        getId : function() {
            return this.id;
        },

        setId : function(id) {
            this.id = id;
        },

        hasASIM : function(asim) {
            if(this.asimList[asim.getName()] == undefined)
                return false;
            else
                return true;
        },

        getASIM : function(name) {
            return this.asimList[name];
        },

        getASIMs : function() {
            var list = [];
            for(var n in this.asimList)
                list.push(this.asimList[n]);

            return list;
        },

        getStatus : function() {
            var descr = {};
            descr.id = this.id;
            descr.status = this.status;

            descr.schedulers = [];
            var schedulers = this.getSchedulers();
            for(var id in schedulers) {
                descr.schedulers.push(schedulers[id].name);
            }

            descr.asims = [];
            var asims = this.getASIMs();
            for(var id in asims) {
                descr.asims.push(asims[id].name);
            }

            return descr;
        },

        getScheduler : function(name) {
            return this.schedulerList[name];
        },

        getSchedulers : function() {
            var list = [];
            for(var n in this.schedulerList)
                list.push(this.schedulerList[n]);

            return list;
        },

        controlASIM : function(name, command) {
            var asim = this.asimList[name];
            if(asim == undefined) {
                return { success : false, msg : "Simulation "+this.id+" does not host ASIM '"+name+"'" };
            } else {
                this.report2Scheduler(name, command);
                return asim.control(command)
            }
        },

        report2Scheduler : function(name, command) {
            console.log("Simulation.report2Scheduler('"+name+"', '"+command+"')");
            for(var strScheduler in this.schedulerList) {
                var scheduler = this.schedulerList[strScheduler];
                switch(command) {
                case "start" :
                case "resume" : scheduler.reportNewASIM(name); break;
                case "pause" : 
                case "stop" : scheduler.reportRemovedASIM(name); break;
                default : console.log("Unknown command");
                }
            }
        },

        controlScheduler : function(name, command) {
            // console.log("Simulation.controlScheduler('"+name+"', '"+command+"')");

            var scheduler = this.schedulerList[name];
            if(scheduler == undefined) {
                return { success : false, msg : "Simulation "+this.id+" does not host scheduler ASIM '"+name+"'" };
            } else
                return scheduler.control(command)
        },

        // add a scheduler ASIM to this simulation
        addScheduler : function(scheduler) {
            if(this.schedulerList[scheduler.getName()] != undefined)
                return false;

            scheduler.setSimulation(this.id);
            this.schedulerList[scheduler.getName()] = scheduler;
            return true;
        },

        delScheduler : function(name, callback) {
            var scheduler = this.schedulerList[name];
            if(scheduler == undefined) {
                callback(null, { code : 404, msg : "Scheduler '"+name+"' does not exist in simulation '"+this.id+"'." });
                return;
            }

            scheduler.setSimulation(undefined);
            delete this.schedulerList[name];

            // TODO: Forward delete instruction to brapper to actually delete the ASIM

            callback({ code : 201, msg : "Scheduler '"+name+"' successfully deleted." }, null);
        },

        // add an ASIM to this simulation 
        addASIM : function(asim) {
            if(this.asimList[asim.getName()] != undefined)
                return false;

            asim.setSimulation(this.id);
            asim.setRegisteredLocations(this.registeredLocations);

            this.asimList[asim.getName()] = asim;

            this.manager.socket.addASIM(asim);

            return true;
        },

        // TODO: make async
        delASIM : function(asimName) {
            var address = asimName.split("@");
            if(address.length == 2)
                asimName = address[1];
            
            var asim = this.asimList[asimName];
            if(asim == undefined) {
                return false;
            }

            for(var scheduler in this.schedulerList) {
                this.schedulerList[scheduler].reportRemovedASIM(asimName);
            }

            if(this.locationUpdates[asimName] != undefined)
                delete this.locationUpdates[asimName]; 
            
            delete this.asimList[asimName];
            delete this.locationUpdates[asimName];
            
            this.manager.socket.delASIM(asimName);
            
            asim.destroy();

            return true;
        },

        start : function() {
            var success = true;
            var errorMsg = "";

            for(var asimName in asimList) {
                var asim = asimList[asimName];
                if(asim.status != ASIMState.ERROR)
                    success = success && asim.start();
                else {
                    success = false;
                    errorMsg += "ASIM '"+asim.getName()+"' is in error state.\n"
                }
            }

            // TODO: Role back and stop all ASIM that were started!

            if(success)
                return { success : true, msg : "Simulation started successfully" };
            else
                return { success : false, msg : "Error starting simulation: " + errorMsg };
        },

        loadSchedulers : function(spec, self, done) {
            var problems = [];
            var created = [];
            var loads = [];

            spec.schedulers.forEach(function(current, index, array) {
                var scheduler = null;
                var deferred = q.defer();
                loads.push(deferred.promise);

                try {
                    scheduler = new ASIM(current, true);
                }

                catch(e) {
                    if(e instanceof ASIMCreationError)
                        deferred.reject({ msg : "Unable to create scheduler ASIM '"+scheduler.getName()+"' in simulation '"+self.id+"'."});
                    else {
                        deferred.reject({ msg : "Unable to create scheduler ASIM: "+e } );
                        return;
                    }
                }

                if(self.addScheduler(scheduler)) {
                    var brapper = self.manager.getSchedulerBrapper();

                    // TODO role back!
                    if(brapper == null) {
                        deferred.reject({ msg : "Unable to load specification. Manager cannot find scheduler brappers."});
                        return;
                    } else {
                        scheduler.load(brapper, function(success, error) {
                            if(success != null)
                                deferred.resolve(scheduler.simplify());
                            else
                                deferred.reject(error);
                        });
                    }
                } else {
                    deferred.reject({ msg : "Scheduler ASIM '"+scheduler.getName()+"' already exists in this simulation."});
                }
            });

            q.allSettled(loads)
                .then(function(result) {
                    result.forEach(function(res) {
                        if(res.state === "fulfilled") {
                            created.push(res.value);
                        } else {
                            problems.push(res.reason);
                        }
                    });

                    if(problems.length == 0)
                        done(null, created);
                    else
                        done(problems, null);
                })
                .done();
        },

        loadASIMs : function(spec, self, done) {
            var problems = [];
            var created = [];
            var loads = [];

            var numSchedulers = spec.schedulers.length;
            var numASIMs = spec.asims.length;

            spec.asims.forEach(function(curASIM, index, array) {
                var asim = null;
                var deferred = q.defer();
                loads.push(deferred.promise);

                try {
                    asim = new ASIM(curASIM);

                    if(self.addASIM(asim)) {
                        var asimBrapper = self.manager.getASIMBrapper();

                        if(asimBrapper == null) {
                            deferred.reject({ msg : "Unable to load specification. Manager cannot allocate required ASIM brappers."});
                        } else {

                            asim.load(asimBrapper, function(success, error) {
                                if(error == null)
                                    deferred.resolve(asim.simplify());
                                else
                                    deferred.reject(error);
                            });

                            // TODO: explicitly start afterwards!
                            // if(asim.start)
                            //    this.report2Scheduler(asim.getName(), "start");
                        } 
                    } else {
                        deferred.reject({ msg : "ASIM '"+asim.getName()+"' already exists in this simulation."});
                    }
                }
                catch(e) {
                    if(e instanceof ASIMCreationError)
                        deferred.reject({ msg : "Some ASIM could not be created. Check your specification. "+e});
                    else
                        throw(e);
                }
            });

            q.allSettled(loads)
                .then(function(result) {
                    result.forEach(function(res) {
                        console.log("ASIM: ", res);
                        if(res.state === "fulfilled") {
                            created.push(res.value);
                        } else {
                            problems.push(res.reason);
                        }
                    });

                    if(problems.length == 0)
                        done(null, created);
                    else
                        done(problems, null);
                })
                .done();
        },

        load : function(spec, callback) {
            var created = {};
            var problems = [];

            if(spec == undefined || spec == null) {
                callback(null, { code : 400, msg : "Invalid simulation specification. Unable to load specification." });
                return;
            }

            if(spec.asims == undefined) {
                callback(null, { code : 400, msg : "Invalid simulation specification. Specification does not specify any ASIMs." });
                return;
            }

            if(!(spec.asims instanceof Array)) {
                callback(null, { code : 400, msg : "Invalid simulation specification. Schedulers must be defined in an Array." });
                return;
            }

            if(spec.schedulers != undefined && !(spec.schedulers instanceof Array)) {
                callback(null, { code : 400, msg : "Invalid simulation specification. Schedulers must be defined in an Array." });
                return;
            }

            if(spec.channels != undefined && !(spec.channels instanceof Array)) {
                callback(null, { code : 400, msg : "Invalid simulation specification. Channles must be defined in an Array." });
                return;
            }

            if(spec.id != undefined && spec.id != null)
                this.id = spec.id;

            var self = this;

            var numSchedulers = spec.schedulers.length;
            var numASIMs = spec.asims.length;

            // TODO make asynchronous!
            if(spec.updates != undefined && spec.updates != null && spec.updates instanceof Array) {
                for(var u in spec.updates) {
                    self.registerLocations(spec.updates[u]);
                }
            }

            async.parallel({ schedulers : this.loadSchedulers.bind(null, spec, self),
                             asims : this.loadASIMs.bind(null, spec, self) }, function(err, result) {
                                 if(err != null) {

                                     self.status = SimulationState.ERROR;

                                     var report = { msg : "Simulation was not created due to the following errors." };
                                     report.errors = {};
                                     var id = 1;
                                     console.log("XERR: ",err);
                                     err.forEach(function(e) {
                                         report.errors[id] = e.msg;
                                         if(e.details != undefined)
                                             report.errors[id] += " " + e.details.asim.error;
                                         id++;
                                     });

                                     console.log("Stop all created ASIMS: ", result);

                                     for(var a in self.asimList)
                                         self.delASIM(a)

                                     for(var a in self.schedulerList)
                                         self.delScheduler(a, function(succ, err) {
                                             if(err != null) {
                                                 console.log("WARNING: Unable to free all resources after failed simulation upload");
                                             }
                                         });

                                     callback({ code : 400, data : report });
                                 } else {
                                     var createdASIMs = {};

                                     result['schedulers'].forEach(function(asim) {
                                         createdASIMs[asim.name] = asim;
                                     });

                                     result['asims'].forEach(function(asim) {
                                         createdASIMs[asim.name] = asim;

                                         result['schedulers'].forEach(function(scheduler) {
                                             console.log("getScheduler: ", scheduler);
                                             self.getScheduler(scheduler.name).reportNewASIM(asim.name);
                                         });
                                     });

                                     self.status = SimulationState.LOADED;

                                     callback({ code : 201, data : createdASIMs });
                                 }
                             });
        },
        
        recvUpdate : function(update) {
            if(update == undefined || update == null)
                return { success : false, msg : "Error: Invalid update\n" };

            if(update.type != "update") {
                return { success : false, msg : "Cannot process update with payload type '"+update.type+"'.\n" };
            }

            // update from which agent?
            if(update.fromAgent == undefined || update.fromAgent == null) {
                return { success : false, msg : "Update message does not specify the agent for which the update should take place\n" };
            }

            if(this.asimList[update.fromAgent] == undefined)
                return { success : false, msg : "Old update from ASIM that does not exist anymore.\n" };

            if(update.body == undefined || update.body == null) {
                return { success : false, msg : "Update set is not specified in received update!\n" };
            }

            /* if(this.updateASIM == undefined || this.updateASIM == null) {
               return { success : false, msg : "FATAL: Manager does not run an updateASIM!\n" };
               }*/

            // TODO: Ugly - generalize
            if(update.toAgent == "@UI@") {
                var updates = JSON.parse(update.body).updates;
                this.updateLocation(update.fromAgent, JSON.parse(JSON.stringify(updates)));
                return { success : true, msg : "" };
            }

            var address = update.toAgent.split("@");
            if(address.length != 2) {
                return { success : false, msg : "Invalid address format\n" };
            }

            var asim = this.schedulerList[address[1]];
            if(asim != undefined) {
                var updates = JSON.parse(update.body).updates;

                this.updateLocation(update.fromAgent, JSON.parse(JSON.stringify(updates)));
                
                return asim.recvUpdate(update);
            } else {
                return { success : false, msg : "Unable to forward update. Scheduler at '"+update.toAgent+"' does not exist.\n" };
            } 

            return { success : true, msg : "" };
        },

        updateLocation : function(name, updates) {
            if(this.locationUpdates[name] == undefined)
                this.locationUpdates[name] = {};

            for(var location in updates) {   
                var update = {};

                var value = undefined;

                if(updates[location].value.BooleanElement != undefined)
                    value = updates[location].value.BooleanElement.value;
                
                if(updates[location].value.NumberElement != undefined)
                    value = updates[location].value.NumberElement.value;

                if(updates[location].value.EnumerationElement != undefined)
                    value = updates[location].value.EnumerationElement.name;

                (this.locationUpdates[name])[updates[location].location.name] = value;
            }

            this.manager.socket.putUpdates(this.locationUpdates);
        },

        recvMsg : function(msg, callback) {
            if(msg == undefined || msg == null)
                callback(null, { code : 400, msg : "Error: Invalid message\n" });

            if(msg.type != "msg") {
                callback(null, { code : 400, msg : "Cannot forward messages without type 'msg'.\n" });
            }

            if(msg.toAgent == undefined || msg.toAgent == null)
                callback(null, { code : 400, msg : "Message specifies no or invalid target.\n" });

            // IS A SENDER ALWAYS REQUIRED?
            if(msg.fromAgent == undefined || msg.fromAgent == null)
                callback(null, { code : 400, msg : "Message specifies no or invalid source.\n" });

            var address = msg.toAgent.split("@");
            if(address.length != 2)
                callback(null, { code : 400, msg : "Invalid address format\n" });

            var asim = this.asimList[address[1]];
            if(asim != undefined) {
                // console.log("Forward message from ASIM '"+msg.fromAgent+"' to ASIM '"+msg.toAgent+"'");
                asim.recvMsg(msg, callback);
            } else {
                var scheduler = this.schedulerList[address[1]];
                if(scheduler != undefined) {
                    // console.log("Forward message from ASIM '"+msg.fromAgent+"' to scheduler ASIM '"+msg.toAgent+"'");
                    scheduler.recvMsg(msg, callback);
                } else
                    callback(null, { code : 404, msg : "Unable to forward message. Target does not exist." });
            }
        },

        /* getASIM : function(fullAddress) {
           var address = fullAddress.split("@");
           if(address.length != 2)
           return null;
           return address[1];
           },*/

        registerLocations : function(reg) {
            if(reg.target == undefined || reg.target == null) {
                return { success : false, msg : "Registration does not specify a target for updates" };
            }

            if(this.registeredLocations[reg.target] == undefined)
                this.registeredLocations[reg.target] = [];

            for(var l in reg.registrations) {
                this.registeredLocations[reg.target].push(reg.registrations[l]);
            }

            return true;
        }
    };

    return cls;

})();

module.exports = Simulation;
