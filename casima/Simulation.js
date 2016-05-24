var uuid = require("node-uuid");

var Scheduler = require("./Scheduler");
var ASIM = require("./ASIM");
var ASIMState = require("./ASIMState");

var Simulation = (function() {

    var cls = function(manager) {
        this.manager = manager;

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

        addASIM : function(asim) {
            if(this.asimList[asim.getName()] != undefined)
                return false;
            
            asim.setSimulation(this.id);
            asim.setRegisteredLocations(this.registeredLocations);

            this.asimList[asim.getName()] = asim;

            // TODO DO SOMETHING SIMILAR FOR UPDATES, ACCUMULATE THEM AND THEN DISPLAY
            // this.manager.socket.addASIM(asim);
            
            return true;
        },

        delASIM : function(name) {
            console.log("- SIMULATION: delASIM: "+name);
            if(this.asimList[name]) {
                console.log("ASIM "+name+" is in simulation");
                if(this.asimList[name].destroy(this.id, name)) {
                    console.log("Destruction successful");
                    for(var scheduler in this.schedulerList) {
                        console.log("Inform scheduler "+this.schedulerList[scheduler]);
                        this.schedulerList[scheduler].reportRemovedASIM(name);
                    }
                    delete this.asimList[name];
                    return true;
                } else {
                    console.log("Simulation cannot destroy ASIM");
                    return false;
                }
                
                var result = this.asimList[name].destroy(this.id, name);
                delete this.asimList[name];
                return result;
            } else {
                console.log("ASIM is not in this simulation");
                return false;
            }
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
            // console.log("Simulation.report2Scheduler('"+name+"', '"+command+"')");
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

        addScheduler : function(scheduler) {
            if(this.schedulerList[scheduler.getName()] != undefined)
                return false;
            
            scheduler.setSimulation(this.id);
            this.schedulerList[scheduler.getName()] = scheduler;

            // console.log("Simulation: Scheduler '"+scheduler.getName()+"' added to simulation '" + this.id + "'");
            
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
        
        load : function(spec) {
            this.reset();

            if(spec.id != undefined && spec.id != null)
                this.id = spec.id;

            if(!spec || spec.asims == undefined) {
                var e = new Error();
                e.src = "Simulation";
                e.msg = "Invalid simulation specification. Unable to load specification.\n";
            }

            if(spec.schedulers != undefined && !(spec.schedulers instanceof Array))
                return { success : false, msg : "Invalid simulation specification. 'schedulers' must be an array.\n" };

            if(!(spec.asims instanceof Array))
                return { success : false, msg : "Invalid simulation specification. 'asims' must be an array.\n" };

            if(spec.channels != undefined && !(spec.channels instanceof Array))
                return { success : false, msg : "Invalid simulation specification. 'channels' must be an array if specified.\n" };

            var self = this;
            try {
                spec.schedulers.forEach(function(current, index, array) {
                    var scheduler = new ASIM(current);
                    scheduler.setSimulation(self.id);

                    if(!self.addScheduler(scheduler)) {
                        self.asimList = {};
                        self.schedulerList = {};

                        var e = new Error();
                        e.msg = "Invalid simulation specification. Two scheduler carry same name.";
                        e.sender = "Scheduler";
                        throw e;
                    }
                });
            } 
            catch(e) {
                if(e.sender != "Scheduler") throw e;
                return { success : false, msg : e };
            }

            try {
                spec.asims.forEach(function(current, index, array) {
                    var asim = new ASIM(current);
                    asim.setSimulation(self.id);

                    if(!self.addASIM(asim)) {
                        self.asimList = {};
                        self.schedulerList = {};

                        var e = new Error();
                        e.msg = "Invalid simulation specification. Two asims carry same name.";
                        e.src = "ASIM";
                        throw e;
                    }
                });
            } 
            catch(e) {
                if(e.sender != "ASIM") throw e;
                return { success : false, msg : e };
            }

            // create scheduler ASIM for the specifications
            try {
                for(var name in this.schedulerList) {
                    var brapper = this.manager.getSchedulerBrapper();
                    if(brapper == null) 
                        return { success : false, msg : "Manager has no scheduler brappers to load simulation. Register or restart the brappers first." };

                    var scheduler = this.schedulerList[name];
                    brapper.addASIM(scheduler);
                    scheduler.load();
                }
            }
            catch(e) {
                if(e.sender != "Assignment") throw e;
                return { success : false, msg : e.msg };
            }
            
            // create ASIM for the specifications
            try {
                for(var name in this.asimList) {
                    var brapper = this.manager.getASIMBrapper();
                    if(brapper == null) 
                        return { success : false, msg : "Manager has no brappers to load simulation. Register or restart the brappers first." };

                    var asim = this.asimList[name];
                    brapper.addASIM(asim);
                    asim.load();
                }
            }
            catch(e) {
                if(e.sender != "Assignment") throw e;
                return { success : false, msg : e.msg };
            }
            
            return { success : true, msg : "Simulation loaded successfully.\n", id : this.id };
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

            if(update.body == undefined || update.body == null) {
                return { success : false, msg : "Update set is not specified in received update!\n" };
            }

            /* if(this.updateASIM == undefined || this.updateASIM == null) {
                return { success : false, msg : "FATAL: Manager does not run an updateASIM!\n" };
            }*/

            // console.log("TOAGENT: "+update.toAgent);
            var address = update.toAgent.split("@");
            if(address.length != 2) {
                // console.log("Target '"+update.toAgent+"' has invalid address format\n");
                return { success : false, msg : "Invalid address format\n" };
            }

            var asim = this.schedulerList[address[1]];
            if(asim != undefined) {
                // console.log("Forwarding update from ASIM '"+update.fromAgent+"' to scheduler ASIM '"+update.toAgent+"'");
                var updates = JSON.parse(update.body).updates;

                this.updateLocation(update.fromAgent, JSON.parse(JSON.stringify(updates)));

                return asim.recvUpdate(update);
            } else {
                return { success : false, msg : "Unable to forward update. Scheduler at '"+update.toAgent+"' does not exist.\n" };
            } 

            return true;
        },

        updateLocation : function(name, updates) {
            for(var location in updates) {   
                var update = {};
                if(this.locationUpdates[name] == undefined)
                    this.locationUpdates[name] = {};
                
                var value = undefined;
                if(updates[location].value.NumberElement != undefined)
                    value = updates[location].value.NumberElement.value;

                (this.locationUpdates[name])[updates[location].location.name] = value;
            }

            this.manager.socket.putUpdates(this.locationUpdates);
        },

        recvMsg : function(msg) {
            if(msg == undefined || msg == null)
                return { success : false, msg : "Error: Invalid message\n" };

            if(msg.type != "msg") {
                return { success : false, msg : "Cannot forward messages without type 'msg'.\n" };
            }

            if(msg.toAgent == undefined || msg.toAgent == null) {
                return { success : false, msg : "Message specifies no or invalid target.\n" };
            }

            if(msg.fromAgent == undefined || msg.fromAgent == null) {
                return { success : false, msg : "Message specifies no or invalid source.\n" };
            }

            var address = msg.toAgent.split("@");
            if(address.length != 2) {
                // console.log("Target '"+msg.toAgent+"' has invalid address format\n");
                return { success : false, msg : "Invalid address format\n" };
            }

            var asim = this.asimList[address[1]];
            if(asim != undefined) {
                // console.log("Forward message from ASIM '"+msg.fromAgent+"' to ASIM '"+msg.toAgent+"'");
                return asim.recvMsg(msg);
            } else {
                var scheduler = this.schedulerList[address[1]];
                if(scheduler != undefined) {
                    // console.log("Forward message from ASIM '"+msg.fromAgent+"' to Scheduler '"+msg.toAgent+"'");
                    return scheduler.recvMsg(msg);
                } else
                    return { success : false, msg : "Unable to forward message. Target does not exist.\n" };
            }
        },

        getASIM : function(fullAddress) {
            var address = fullAddress.split("@");
            if(address.length != 2)
                return null;
            return address[1];
        },

        registerLocations : function(reg) {
            if(reg.target == undefined || reg.target == null) {
                return { success : false, msg : "Registration does not specify a target for updates" };
            }

            if(this.registeredLocations[reg.target] == undefined)
                this.registeredLocations[reg.target] = [];
            
            for(var l in reg.registrations) {
                this.registeredLocations[reg.target].push(reg.registrations[l]);
            }

            /* console.log("Registrations: ");
             for(var l in this.registeredLocations) {
             console.log("REG: "+l+": "+JSON.stringify(this.registeredLocations[l]));
             }
             */

            return true;
        }
    };

    return cls;
    
})();

module.exports = Simulation;
