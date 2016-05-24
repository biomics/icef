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

        this.asimBrapperMap = {};

        // the scheduler brapper
        this.schedulerBrapperMap = {};

        this.simMap = {};
    };

    cls.prototype = {
        getScheduler : function() {
            return this.scheduler;
        },

        setScheduler : function() {
        },

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

        registerSchedulerBrapper : function(descr) {
            if(!descr || descr.host == undefined || descr.port == undefined) {
                return { success : false, msg : "Invalid brapper description. Unable to register scheduler brapper.\n" };
            }

            var newBrapper = new Brapper(descr.host, descr.port, "scheduler");
            var id = newBrapper.getId();

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

        getASIMBrapper : function(id) {
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
            var brapper = this.getASIMBrapper();
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
            newASIM.load(descr.start);

            if(descr.start)
                simulation.report2Scheduler(newASIM.getName(), "start");
            
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
            console.log("Manager: delASIM 1 ");
            var sim = this.simMap[simulation];
            if(sim) {
                console.log("Manager: delASIM 2");
                return sim.delASIM(name);
            } else
                return false;
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

        recvMsg : function(simulation, msg) {
            var sim = this.simMap[simulation];
            
            if(sim == undefined || sim == null)
                return { success : false, msg : "Simulation for message does not exist. Ignore." };
            
            return sim.recvMsg(msg);
        },

        // TODO also send the updates to some internal 
        // data structure in case these are updates belonging 
        // to the UI
        recvUpdate : function(simulation, update) {
            var sim = this.simMap[simulation];
            
            if(sim == undefined || sim == null) {
                console.log("Simulation '"+simulation+"' for updates does not exist. Ignore.");
                return { success : false, msg : "Simulation for updates does not exist. Ignore." };
            }

            return sim.recvUpdate(update);
        },

        register4Updates : function(simulation, registration) {
            var sim = this.simMap[simulation];

            if(sim == undefined || sim == null)
                return { success : false, msg : "Simulation for updates does not exist. Ignore." };

            // register locations inside the simulation 
            // such that the locations in new asims are 
            // automatically registered
            sim.registerLocations(registration);

            var success = true;
            var msg = "";
            for(var b in this.asimBrapperMap) {
                var result = this.asimBrapperMap[b].register4Updates(simulation, registration);
                success = success && result.success;
            }

            if(success)
                return { success : true, msg : "Registration successful" };
            else
                return { success : false, msg : "Unable to register all locations in brapper '"+this.id+"'" };
        },

        sendUpdate : function(update) {
        }
    };

    return cls;
})();

module.exports = Manager;
