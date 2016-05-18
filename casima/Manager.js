var ASIM = require("./ASIM");
var ASIMCreationError = require("./ASIMCreationError");
var Brapper = require("./Brapper");
var Scheduler = require("./Scheduler");
var Simulation = require("./Simulation");

var uuid = require("node-uuid");

var Manager = (function() {

    var cls = function() {
        this.schedulerList = {};
        this.schedulerMap = {};

        this.channelList = {};
        this.channelMap = {};

        this.brapperMap = {};

        this.simMap = {};
    };

    cls.prototype = {
        getSimulations : function() {
            var ids = [];
            for(var id in this.simMap) 
                ids.push(id);
            return ids;
        },

        loadSimulation : function(spec) {
            var newSimulation = new Simulation(this);
            var result = newSimulation.load(spec);

            if(result.success)
                this.simMap[newSimulation.getId()] = newSimulation;

            return result;
        },

        registerBrapper : function(descr) {
            if(!descr || descr.host == undefined || descr.port == undefined) {
                return { success : false, msg : "Invalid brapper description. Unable to register brapper.\n" };
            }

            var newBrapper = new Brapper(descr.host, descr.port);
            var id = newBrapper.getId();

            if(this.brapperMap[id] != undefined) {
                return { success : false, msg : "Unable to register new wrapper. Brapper already exists!\n" };
            } else {
                this.brapperMap[id] = newBrapper;
                return { success : true, msg : "Brapper successfully registered.", id : id };
            }
        },

        delBrapper : function(id) {
            var toDel = null;
            for(var w in this.brapperMap) {
                if(this.brapperMap[w].id == id) {
                    toDel = w;
                    break;
                }
            }

            if(toDel != null) {
                delete this.brapperMap[w];
                return true;
            } else {
                return false;
            }
        },

        getBrappers : function() {
            return this.brapperMap;
        },

        getBrapper : function(id) {
            return this.brapperMap[id];
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

        createASIM : function(descr) {
            // check for an empty brapper
            var brapper = this.getHostingBrapper();
            if(brapper == null) 
                return { success : false, msg : "Manager has no brappers to run ASIMs. Register or restart them." };

            var simulation = null;
            // create a new simulation for this ASIM
            if(descr.simulation == undefined || descr.simulation == null) {
                simulation = new Simulation(this);
                this.simMap[simulation.getId()] = simulation;
            } else {
                if(this.simMap[descr.simulation] == undefined) {
                    simulation = new Simulation(this);
                    simulation.setId(descr.simulation);
                    this.simMap[simulation.getId()] = simulation;
                } else 
                    simulation = this.simMap[descr.simulation];                
            }

            // first create the new ASIM
            var newASIM = null;
            try{               
                newASIM = new ASIM(descr);
            }
            catch(e) {
                if(e instanceof ASIMCreationError) {
                    return { success : false, error : e.toString() };
                } else 
                    throw e;
            }

            // ASIM already exists => error
            if(simulation.hasASIM(newASIM))
                return { success : false, msg : "ASIM '"+newASIM.getName()+"' already exists in simulation '"+simulation.getId()+"'.\n" };
            else
                simulation.addASIM(newASIM);
            
            brapper.addASIM(newASIM);

            var self = this;
            newASIM.load();
            
            return { success : true, msg : "ASIM '"+newASIM.getName()+"' created successfully in simulation '"+simulation.getId()+"'.\n", asim : newASIM.simplify() };
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
            if(sim)
                return sim.delASIM(name);
            else
                return false;
        },

        getHostingBrapper : function() {
            var minLoad = -1;
            var minId = -1;
            for(var wid in this.brapperMap) {
                var wrapper = this.brapperMap[wid];
                var wl = wrapper.getLoad();
                if(wl < minLoad || minLoad == -1) {
                    minLoad = wl;
                    minId = wid;
                }
            }

            if(minId == -1)
                return null;
            else
                return this.brapperMap[minId];
        },

        recvMsg : function(simulation, msg) {
            var sim = this.simMap[simulation];
            
            if(sim == undefined || sim == null)
                return { success : false, msg : "Simulation for message does not exist. Ignore." };
            
            return sim.recvMsg(msg);
        },

        recvUpdate : function(update) {
            // console.log("Manager: Receive: ", update);

            if(update == undefined || update == null)
                return { success : false, msg : "Error: Invalid update\n" };

            if(update.type != "update") {
                return { success : false, msg : "Cannot process update with payload type '"+update.type+"'.\n" };
            }

            if(update.simulation == undefined || update.simulation == null) {
               return { success : false, msg : "Cannot process update without simulation specification.\n" };
            }

            var sim = this.simMap[update.simulation];
            if(sim == undefined || sim == null) {
                return { success : false, msg : "Error: Update for unknown simulation '" + update.simulation + "'.\n" };
            }

            // update from which agent?
            if(update.fromAgent == undefined || update.fromAgent == null) {
                return { success : false, msg : "Update message does not specify the agent for which the update should take place\n" };
            }

            if(update.body == undefined || update.body == null) {
                return { success : false, msg : "Update set is not specified in received update!\n" };
            }

            if(this.updateASIM == undefined || this.updateASIM == null) {
                return { success : false, msg : "FATAL: Manager does not run an updateASIM!\n" };
            }
            
            console.log("Ignore update for now");

            return { success: true, msg : "Ignored" };
        }, 

        sendUpdate : function(update) {
        }
    };

    return cls;
})();

module.exports = Manager;
