var ASIM = require("./ASIM");
var Brapper = require("./Brapper");
var Scheduler = require("./Scheduler");

var uuid = require("node-uuid");

var Manager = (function() {

    var cls = function() {
        this.schedulerList = {};
        this.schedulerMap = {};

        this.channelList = {};
        this.channelMap = {};

        this.asimMap = {};
        this.asimList = {};

        this.brapperMap = {};
    };

    cls.prototype = {
        resetSimulation : function() {
            this.asimList = {};
            this.schedulerList = {};
            this.channelList = {};
        },

        loadSimulation : function(spec) {
            this.resetSimulation();

            if(!spec || spec.asims == undefined) {
                return { success : false, msg : "Invalid simulation specification. Unable to load specification.\n" };
            }

            if(spec.channels != undefined && !(spec.schedulers instanceof Array))
                return { success : false, msg : "Invalid simulation specification. 'schedulers' must be an array.\n" };

            if(!(spec.asims instanceof Array))
                return { success : false, msg : "Invalid simulation specification. 'asims' must be an array.\n" };

            if(spec.channels != undefined && !(spec.channels instanceof Array))
                return { success : false, msg : "Invalid simulation specification. 'channels' must be an array if specified.\n" };

            var self = this;
            try {
                spec.schedulers.forEach(function(current, index, array) {
                    var scheduler = new Scheduler(
                        current.name,
                        current.signature,
                        current.init,
                        current.program,
                        current.policy);

                    if(self.schedulerList[scheduler.getName()] != undefined) {
                        self.asimList = {};
                        self.schedulerList = {};

                        var e = new Error();
                        e.msg = "Invalid simulation specification. Two scheduler carry same name.";
                        e.sender = "Scheduler";
                        throw e;
                    }

                    self.schedulerList[scheduler.getName()] = scheduler;
                });
            } 
            catch(e) {
                if(e.sender != "Scheduler") throw e;
                return { success : false, msg : e };
            }

            try {
                spec.asims.forEach(function(current, index, array) {
                    var asim = new ASIM(
                        current.name,
                        current.signature,
                        current.init,
                        current.program,
                        current.policy);
                    if(self.asimList[asim.getName()]) {
                        self.asimList = {};
                        self.schedulerList = {};

                        var e = new Error();
                        e.msg = "Invalid simulation specification. Two asims carry same name.";
                        e.sender = "ASIM";
                        throw e;
                    }
                    self.asimList[asim.getName()] = asim;
                });
            } 
            catch(e) {
                if(e.sender != "ASIM") throw e;
                return { success : false, msg : e };
            }
            
            // create ASIM for the specifications
            try {
                for(var name in this.asimList) {
                    if(!this.assignBrapper(this.asimList[name])) {
                        var e = new Error();
                        e.msg = "Unable to assign ASIM '"+name+"' to a brapper.\n";
                        e.sender = "Assignment";
                        throw e;
                    }
                }
            }
            catch(e) {
                if(e.sender != "Assignment") throw e;
                return { success : false, msg : e.msg };
            }
            
            return { success : true, msg : "Simulation loaded successfully.\n", id : uuid.v4() };
        },

        registerBrapper : function(descr) {
            if(!descr || descr.host == undefined || descr.port == undefined) {
                return { success : false, msg : "Invalid brapper description. Unable to register wrapper.\n" };
            }

            var newBrapper = new Brapper(descr.host, descr.port);
            var key = newBrapper.getKey();

            if(this.brapperMap[key] != undefined) {
                return { success : false, msg : "Unable to register new wrapper. Brapper already exists!\n" };
            } else {
                this.brapperMap[key] = newBrapper;
                return { success : true, msg : "Brapper successfully registered.", key : key };
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

        getASIMs : function() {
            return this.asimList;
        },

        getASIM : function(id) {
            return this.asimList[id];
        },

        createASIM : function(descr) {
            console.log("Manager: Create new ASIM");

            if(!descr || descr.program == undefined || descr.init == undefined || descr.policy == undefined ) {
                return { success : false, msg : "Invalid creation request. Missing or invalid ASIM description.\n" };
            } else {
                var newASIM = new ASIM(descr.name, descr.signature, descr.init, descr.program, descr.policy);

                if(this.asimMap[newASIM.getName()] == undefined && this.asimList[newASIM.getName()] == undefined) {
                    if(this.assignBrapper(newASIM)) 
                        return { success : true, msg : "ASIM successfully created", asim : newASIM };
                    else
                        return { success : false, msg : "Unable to find hosting brapper for ASIM "+newASIM.getName()+".\n" };
                } else {
                    // ASIM already exists => error
                    return { success : false, msg : "ASIM already exists.\n" };
                }
            }
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

        assignBrapper : function(asim) {
            var hostingBrapper = this.getHostingBrapper();

            if(hostingBrapper == null) {
                return false;
            }
            
            var self = this;
            // TODO check success by implementing a callback
            hostingBrapper.createASIM(asim, function(e) {                
                if(e != null && e != undefined && e.name != undefined) {
                    if(self.asimMap != undefined && self.asimMap != null)
                        delete self.asimMap[e.name];

                    if(self.asimList != undefined && self.asimList != null) {
                        console.log("e: "+e);
                        if(e.error && e.error != "") {
                            self.asimList[e.name].setError(e.error);
                            self.asimList[e.name].setBrapper(null);
                        }
                    }
                }
            });

            this.asimMap[asim.getName()] = hostingBrapper;
            asim.setBrapper(hostingBrapper.id);
            
            return true;
        },

        delAgent : function(descr) {
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

            if(this.asimMap[msg.toAgent] != undefined) {
                console.log("Forward message from agent '"+msg.fromAgent+"' to agent '"+msg.toAgent+"'");

                console.log("Brapper hosting this agent: ",this.asimMap[msg.toAgent]);

                this.asimMap[msg.toAgent].recvMsg(msg);
                
                return { success : true };
            } else {
                return { success : false, msg : "Unable to forward message. Target does not exist.\n" };
            }
        },

        recvUpdate : function(update) {
            console.log("Manager: Receive an update.");

            if(update == undefined || update == null)
                return { success : false, msg : "Error: Invalid update\n" };

            if(msg.type != "update") {
                return { success : false, msg : "Cannot process update with payload type 'update'.\n" };
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
            
        }, 

        sendUpdate : function(update) {
        }
    };

    return cls;
})();

module.exports = Manager;
