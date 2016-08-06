/*
 * Manager.js v1.0
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

var ASIM = require("./ASIM");
var ASIMCreationError = require("./ASIMCreationError");
var Brapper = require("./Brapper");
var Simulation = require("./Simulation");

var uuid = require("node-uuid");

var Manager = (function() {

    var cls = function(socket) {
        this.schedulerList = {};
        this.schedulerMap = {};

        this.channelList = {};
        this.channelMap = {};

        this.asimBrapperMap = {};

        // the scheduler brapper
        this.schedulerBrapperMap = {};

        this.simMap = {};

        this.socket = socket;
    };

    cls.prototype = {
        setScheduler : function() {
        },

        // retrieve the set of all simulation ids
        getSimulations : function(callback) {
            var ids = [];
            for(var id in this.simMap) 
                ids.push(id);
            callback({ data : ids}, null);
        },

        // retrieve the set of all simulation ids
        getSimulation : function(name, callback) {
            /* console.log("this.simMap: ", this.simMap);
               console.log("this.simMap[name]: ", this.simMap[name]); */

            if(this.simMap != null && this.simMap[name] != undefined && this.simMap[name] != null) {
                callback({ code : 200, data : this.simMap[name].getStatus() }, null);
            } else {
                callback(null, { code : 404, msg : "Simulation '"+name+"' does not exist." });
            }
        },

        // load a simulation secification given by spec
        loadSimulation : function(spec, callback) {
            var simulation = null;

            // if no simulation id is specified, create a new simulation
            if(spec.id == undefined || spec.id == null || this.simMap[spec.id] == undefined)
                simulation = new Simulation(this);
            else {
                simulation = this.simMap[spec.id];
            }

            var self = this;
            simulation.load(spec, function(success, error) {
                if(success != null)
                    self.simMap[simulation.getId()] = simulation;
                callback(success, error);
            });
        },

        registerSchedulerBrapper : function(descr) {
            if(!descr || descr.host == undefined || descr.port == undefined) {
                return { success : false, msg : "Invalid brapper description. Unable to register scheduler brapper.\n" };
            }

            var newBrapper = new Brapper(descr.host, descr.port, "scheduler");
            var id = newBrapper.getId();

	    console.log("[Manager] Register scheduler brapper '"+id+"' at '"+descr.host+":"+descr.port+"'");

            if(this.schedulerBrapperMap[id] != undefined) {
                return { success : false, msg : "Unable to register new scheduling brapper. Brapper already exists!\n" };
            } else {
                this.schedulerBrapperMap[id] = newBrapper;
                return { success : true, msg : "Scheduling brapper successfully registered.", id : id };
            }
        },

        registerASIMBrapper : function(descr) {
            if(!descr || descr.host == undefined || descr.port == undefined) {
                return { success : false, msg : "Invalid brapper description. Unable to register ASIM brapper.\n" };
            }

            var newBrapper = new Brapper(descr.host, descr.port);
            var id = newBrapper.getId();

            console.log("[Manager] Register brapper '"+id+"' at '"+descr.host+":"+descr.port+"'");

            if(this.asimBrapperMap[id] != undefined) {
                return { success : false, msg : "Unable to register new brapper. Brapper already exists!\n" };
            } else {
                this.asimBrapperMap[id] = newBrapper;
                return { success : true, msg : "Brapper successfully registered.", id : id };
            }
        },

        delBrapper : function(id) {
            var toDel = null;
            for(var w in this.asimBrapperMap) {
                if(this.asimBrapperMap[w].id == id) {
                    toDel = w;
                    break;
                }
            }

            if(toDel != null) {
                delete this.asimBrapperMap[w];
                return true;
            } else {
                return false;
            }
        },

        getASIMBrappers : function() {
            return this.asimBrapperMap;
        },

        getSchedulerBrappers : function() {
            return this.schedulerBrapperMap;
        },

        getASIMBrapperInfo : function(id) {
            return this.asimBrapperMap[id];
        },

        getASIMs : function(simulation) {
            var list = [];

            if(simulation == undefined || simulation == null) {
                for(var sim in this.simMap) {
                    if(this.simMap[sim])
                        list = list.concat(this.simMap[sim].getASIMs());
                }
            } else {
                if(this.simMap[simulation] == undefined)
                    return [];
                else
                    return this.simMap[simulation].getASIMs();
            }
            
            return list;
        },

        getScheduler : function(simulation, name) {
            if(!this.simMap[simulation])
                return undefined;
            else {
                var scheduler = this.simMap[simulation].getScheduler(name);
                if(scheduler)
                    return scheduler.simplify();
                else
                    return scheduler;
            }
        },

        delScheduler : function(simulation, name, callback) {
            var sim = this.simMap[simulation];
            if(!sim) {
                callback(null, { code : 404, msg : "Cannot delete scheduler. Simulation '"+simulation+"' does not exist." });
            } else {
                sim.delScheduler(name, callback);
            }
        },

        getSchedulers : function(simulation) {
            var list = [];

            if(simulation == undefined || simulation == null) {
                for(var sim in this.simMap) {
                    if(this.simMap[sim])
                        list = list.concat(this.simMap[sim].getSchedulers());
                }
            } else {
                if(this.simMap[simulation] == undefined)
                    return null;
                else
                    return this.simMap[simulation].getSchedulers();
            }
            
            return list;
        },

        getASIM : function(simulation, asimName) {
            if(!this.simMap[simulation])
                return undefined;
            else {
                var asim = this.simMap[simulation].getASIM(asimName);
                if(asim)
                    return asim.simplify();
                else
                    return asim;
            }
        },

        createASIM : function(spec, callback) {
            // check for an empty brapper
            var brapper = this.getASIMBrapper();
            if(brapper == null) {
                callback(null, { code : 503, msg : "Manager has no brappers to run ASIMs. Register or restart them." });
                return;
            }

            var simulation = null;
            // create a new simulation for this ASIM
            if(spec.simulation == undefined || spec.simulation == null) {
                simulation = new Simulation(this);
                this.simMap[simulation.getId()] = simulation;
            } else {
                if(this.simMap[spec.simulation] == undefined) {
                    simulation = new Simulation(this);
                    simulation.setId(spec.simulation);
                    this.simMap[simulation.getId()] = simulation;
                } else 
                    simulation = this.simMap[spec.simulation];                
            }

            // first create the new ASIM
            var newASIM = null;
            try{               
                newASIM = new ASIM(spec);
            }
            catch(e) {
                if(e instanceof ASIMCreationError) {
                    callback(null, { code : 400, msg : "Error while creating ASIM: " + e.toString() });
                } else 
                    callback(null, { code : 500, msg : "Unexpected error during ASIM creation. Report to developer." });
            }

            // ASIM already exists => error
            if(simulation.hasASIM(newASIM))
                callback(null, { code : 409, msg : "ASIM '"+newASIM.getName()+"' already exists in simulation '"+simulation.getId()+"'.\n" });
            else
                simulation.addASIM(newASIM);

            var self = this;
            newASIM.load(brapper, function(success, error) {
                if(success != null) {
                    if(spec.start)
                        simulation.report2Scheduler(newASIM.getName(), "start");

                    callback({ code : success.code, msg : "ASIM '"+newASIM.getName()+"' created successfully in simulation '"+simulation.getId()+"'.", asim : newASIM.simplify() }, null);
                } else {
                    callback(null, error);
                }
            });
        },

        controlASIM : function(simulation, name, cmd) {
            var sim = this.simMap[simulation];

            if(sim)
                return sim.controlASIM(name, cmd);
            else
                return false;
        },

        delASIM : function(simulation, name) {
            var sim = this.simMap[simulation];
            if(sim) {
                return sim.delASIM(name);
            } else
                return false;
        },

        createScheduler : function(spec, callback) {
            // check for an empty brapper
            var brapper = this.getSchedulerBrapper();

            if(brapper == null) {
                callback(null, { code : 503, msg : "Manager has no brappers to run Schedulers. Register or restart a scheduler brapper." });
                return;
            }

            var simulation = null;
            // create a new simulation for this Scheduler
            if(spec.simulation == undefined || spec.simulation == null) {
                simulation = new Simulation(this);
                this.simMap[simulation.getId()] = simulation;
            } else {
                if(this.simMap[spec.simulation] == undefined) {
                    simulation = new Simulation(this);
                    simulation.setId(spec.simulation);
                    this.simMap[simulation.getId()] = simulation;
                } else
                    simulation = this.simMap[spec.simulation];
            }

            // first create the new ASIM for this scheduler
            var newASIM = null;
            try{
                newASIM = new ASIM(spec);
            }
            catch(e) {
                if(e instanceof ASIMCreationError) {
                    callback(null, { code : 400, msg : "Unable to create scheduler ASIM '"+newASIM.getName()+"' in simulation '"+simulation.getId()+"'" });
                } else {
                    callback(null, { code : 503, msg : e });
                }
            }

            if(simulation.hasASIM(newASIM))
                callback(null, { code : 409, msg : "Scheduler ASIM '"+newASIM.getName()+"' already exists in simulation '"+simulation.getId()+"'." });
            else
                newASIM.setSimulation(simulation.getId());

            var self = this;
            newASIM.load(brapper, function(success, error) {
                if(success != null) {
                    simulation.addScheduler(newASIM);
                    callback({ code : success.code, msg : "Scheduler ASIM '"+newASIM.getName()+"' created successfully in simulation '"+simulation.getId()+"'.", scheduler : newASIM.simplify() }, null);
                } else {
                    callback(null, error);
                }
            });
        },

        controlScheduler : function(simulation, name, cmd) {
            var sim = this.simMap[simulation];

            if(sim)
                return sim.controlScheduler(name, cmd);
            else
                return false;
        },

        getSchedulerBrapper : function() {
            var minLoad = -1;
            var minId = -1;
            for(var wid in this.schedulerBrapperMap) {
                var wrapper = this.schedulerBrapperMap[wid];
                var wl = wrapper.getLoad();
                if(wl < minLoad || minLoad == -1) {
                    minLoad = wl;
                    minId = wid;
                }
            }

            if(minId == -1)
                return null;
            else
                return this.schedulerBrapperMap[minId];
        },

        getASIMBrapper : function() {
            var minLoad = -1;
            var minId = -1;
            for(var wid in this.asimBrapperMap) {
                var wrapper = this.asimBrapperMap[wid];
                var wl = wrapper.getLoad();
                if(wl < minLoad || minLoad == -1) {
                    minLoad = wl;
                    minId = wid;
                }
            }

            if(minId == -1)
                return null;
            else
                return this.asimBrapperMap[minId];
        },

        recvMsg : function(simulation, msg, callback) {
            var sim = this.simMap[simulation];
            
            if(sim == undefined || sim == null) {
                callback(null, { code : 404, msg : "Simulation specified in message does not exist. Ignore." });
                return;
            }
            
            sim.recvMsg(msg, callback);
        },

        // TODO also send the updates to some internal 
        // data structure in case these are updates belonging 
        // to the UI
        recvUpdate : function(simulation, update) {
            var sim = this.simMap[simulation];
            
            if(sim == undefined || sim == null) {
                // console.log("Simulation '"+simulation+"' for updates does not exist. Ignore.");
                return { success : false, msg : "Simulation for updates does not exist. Ignore." };
            }

            return sim.recvUpdate(update);
        },

        // TODO: review this method
        register4Updates : function(simulation, registration, callback) {
            var sim = this.simMap[simulation];

            if(simulation == undefined || simulation == null) {
                callback(null, { msg : "Simulation for updates does not exist. Ignore." });
                return;
            }

            var success = true;
            var msg = "";

            var numBrappers = Object.keys(this.asimBrapperMap).length;
            var brapperCount = 0;
            for(var b in this.asimBrapperMap) {
                var self = this;
                var result = this.asimBrapperMap[b].register4Updates(simulation, registration, function(result) {
                    brapperCount++;

                    success = success && result.success;

                    if(brapperCount == numBrappers) {

                        if(success)
                            callback({msg : "Registration successful" }, null);
                        else
                            callback(null, { msg : "Unable to register all locations in brapper '"+self.asimBrapperMap[b].id+"'" });
                    };
                });
            }

            if(numBrappers == 0) {
                callback({ msg : "No registrations required" }, null);
            }
        },

        sendUpdate : function(update) {
        }
    };

    return cls;
})();

module.exports = Manager;
