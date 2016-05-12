var ASIM = require("./ASIM");
var Brapper = require("./Brapper");

var Manager = (function() {

    var cls = function() {
        this.agentMap = {};
        this.availableBrappers = {};
    };

    cls.prototype = {
        registerBrapper : function(descr) {
            if(!descr || descr.host == undefined || descr.port == undefined) {
                return { success : false, msg : "Invalid wrapper description. Unable to register wrapper.\n" };
            }

            var newBrapper = new Brapper(descr.host, descr.port);
            var key = newBrapper.getKey();

            if(this.availableBrappers[key] != undefined) {
                return { success : false, msg : "Unable to register new wrapper. Brapper already exists!\n" };
            } else {
                this.availableBrappers[key] = newBrapper;
                return { success : true, msg : "Brapper registered.\n" };   
            }
        },

        delBrapper : function(id) {
            var toDel = null;
            for(var w in this.availableBrappers) {
                if(this.availableBrappers[w].id == id) {
                    toDel = w;
                    break;
                }
            }

            if(toDel != null) {
                delete this.availableBrappers[w];
                return true;
            } else {
                return false;
            }
        },

        getBrappers : function() {
            return this.availableBrappers;
        },

        getBrapper : function(id) {
            return this.availableBrappers[id];
        },

        getASIMs : function() {
            return this.agentMap;
        },

        createASIM : function(descr) {
            console.log("Manager: Create new agent");

            if(!descr || descr.name == undefined || descr.program == undefined) {
                return { success : false, msg : "Invalid creation request. Missing or invalid agent description.\n" };
            } else {
                // create a new agent
                if(this.agentMap[descr.name] == undefined) {
                    var hostingBrapper = this.getHostingBrapper();
                    if(hostingBrapper == null) {
                        console.log("Selected Brapper: ", hostingBrapper);

                        return { success : false, msg : "Unable to determine wrapper which could host this agent.\n" };
                    }

                    hostingBrapper.createAgent(descr);
                    this.agentMap[descr.name] = hostingBrapper;

                    return { success : true, msg : "Agent created successfully.\n" };
                } else {
                // agent already exists => error
                    return { success : false, msg : "Agent already exists.\n" };
                }
            }
        },

        getHostingBrapper : function() {
            var minLoad = -1;
            var minId = -1;
            for(var wid in this.availableBrappers) {
                var wrapper = this.availableBrappers[wid];
                var wl = wrapper.getLoad();
                if(wl < minLoad || minLoad == -1) {
                    minLoad = wl;
                    minId = wid;
                }
            }

            if(minId == -1)
                return null;
            else
                return this.availableBrappers[minId];
        },

        delAgent : function(descr) {
        },

        recvMsg : function(msg) {

            if(msg == undefined || msg == null)
                return { success : false, msg : "Invalid message\n" };

            if(msg.type != "agent") {
                return { success : false, msg : "Cannot forward messages without type 'agent'.\n" };
            }

            if(msg.toAgent == undefined || msg.toAgent == null) {
                return { success : false, msg : "Message specifies no or invalid target.\n" };
            }

            if(msg.fromAgent == undefined || msg.fromAgent == null) {
                return { success : false, msg : "Message specifies no or invalid source.\n" };
            }

            if(this.agentMap[msg.toAgent] != undefined) {
                console.log("Forward message from agent '"+msg.fromAgent+"' to agent '"+msg.toAgent+"'");

                console.log("Brapper hosting this agent: ",this.agentMap[msg.toAgent]);

                this.agentMap[msg.toAgent].recvMsg(msg);
                
                return { success : true };
            } else {
                return { success : false, msg : "Unable to forward message. Target does not exist.\n" };
            }
        },

        recvUpdate : function(update) {
            console.log("Manager: Receive an update.");

            // update from which agent?
            if(update.agent == undefined || update.agent == null) {
                return { success : false, msg : "Update message does not specify the agent for which the update should take place\n" };
            }

            if(update.set == undefined || update.set == null) {
                return { success : false, msg : "Update set is not specified!\n" };
            }
        }, 

        sendUpdate : function(update) {
        }
    };

    return cls;
})();

module.exports = Manager;
