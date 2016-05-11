var Agent = require("./agent.js");
var Wrapper = require("./wrapper.js");

var Manager = (function() {

    var cls = function() {
        this.agentMap = {};
        this.availableWrappers = {};
    };

    cls.prototype = {
        registerWrapper : function(descr) {
            if(!descr || descr.host == undefined || descr.port == undefined) {
                return { success : false, msg : "Invalid wrapper description. Unable to register wrapper.\n" };
            }

            var newWrapper = new Wrapper(descr.host, descr.port);
            var key = newWrapper.getKey();

            if(this.availableWrappers[key] != undefined) {
                return { success : false, msg : "Unable to register new wrapper. Wrapper already exists!\n" };
            } else {
                this.availableWrappers[key] = newWrapper;
                return { success : true, msg : "Wrapper registered.\n" };   
            }
        },

        delWrapper : function(id) {
            var toDel = null;
            for(var w in this.availableWrappers) {
                if(this.availableWrappers[w].id == id) {
                    toDel = w;
                    break;
                }
            }

            if(toDel != null) {
                delete this.availableWrappers[w];
                return true;
            } else {
                return false;
            }
        },

        getWrappers : function() {
            return this.availableWrappers;
        },

        getAgents : function() {
            return this.agentMap;
        },

        createAgent : function(descr) {
            console.log("Manager: Create new agent");

            if(!descr || descr.name == undefined || descr.program == undefined) {
                return { success : false, msg : "Invalid creation request. Missing or invalid agent description.\n" };
            } else {
                // create a new agent
                if(this.agentMap[descr.name] == undefined) {
                    var hostingWrapper = this.getHostingWrapper();
                    if(hostingWrapper == null) {
                        console.log("Selected Wrapper: ", hostingWrapper);

                        return { success : false, msg : "Unable to determine wrapper which could host this agent.\n" };
                    }

                    hostingWrapper.createAgent(descr);
                    this.agentMap[descr.name] = hostingWrapper;

                    return { success : true, msg : "Agent created successfully.\n" };
                } else {
                // agent already exists => error
                    return { success : false, msg : "Agent already exists.\n" };
                }
            }
        },

        getHostingWrapper : function() {
            var minLoad = -1;
            var minId = -1;
            for(var wid in this.availableWrappers) {
                var wrapper = this.availableWrappers[wid];
                var wl = wrapper.getLoad();
                if(wl < minLoad || minLoad == -1) {
                    minLoad = wl;
                    minId = wid;
                }
            }

            if(minId == -1)
                return null;
            else
                return this.availableWrappers[minId];
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

                console.log("Wrapper hosting this agent: ",this.agentMap[msg.toAgent]);

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
