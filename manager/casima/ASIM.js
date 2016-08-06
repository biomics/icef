/*
 * ASIM.js v1.0
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

var ASIMState = require("./ASIMState");
var ASIMCreationError = require("./ASIMCreationError");

var http = require("http");
var uuid = require("node-uuid");

var ASIM = (function() {        

    var cls = function(spec, scheduler) {
        if(spec.name == undefined || spec.name == null || spec.name == "") 
            this.name = "ASIM"+uuid.v4().replace(/-/g, "");
        else
            this.name = spec.name;

        if(spec.signature == undefined || spec.signature == null)
            this.signature = null;
        else
            this.signature = spec.signature;

        if(spec.program == undefined || spec.program == null)
            throw new ASIMCreationError("Erroneous ASIM specification. Missing program.");
        else
            this.program = spec.program;

        if(spec.init == undefined || spec.init == null)
            throw new ASIMCreationError("Erroneous ASIM specification. No init rule specified.");
        else
            this.init = spec.init;

        if(spec.policy == undefined || spec.policy == null) 
            throw new ASIMCreationError("Erroneous ASIM specification. No scheduling policy specified.");
        else
            this.policy = spec.policy;

        if(spec.start != undefined && spec.start != null)
            this.start = spec.start;

        if(spec.simualtion != undefined || spec.simulation != null)
            this.simulation = spec.simulation;
        else 
            this.simulation = null;

        this.registeredLocations = null;
        this.brapper = null;
        this.status = ASIMState.EMPTY;

        if(scheduler == undefined || scheduler == null)
            this.isScheduler = false;
        else
            this.isScheduler = scheduler;
    };

    cls.prototype = {
        control : function(command) {
            var cmd = command.toLowerCase();

            switch(command) {
            case "start" : return this.run(); break;
            case "pause" : return this.pause(); break;
            case "resume" : return this.resume(); break;
	    case "stop" : return this.stop(); break;
            default :
            }

            return { success : false, msg : "Unknown command '"+command+"'.\n" };
        },

        pause : function() {
            if(this.status == ASIMState.ERROR)
                return { success : false, msg : "Cannot pause ASIM '"+this.name+"' as it is in error state\n" };

            if(this.status != ASIMState.RUNNING)
                return { success : false, msg : "Cannot pause ASIM '"+this.name+"' as it is not running.\n" };

            var options = {
                host: this.brapper.host,
                port: this.brapper.port,
                path: '/asims/'+this.simulation+'/'+this.name+'/pause',
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Content-Length': 0
                }
            };

            var self = this;

            var request = http.request(options, function(res) {
                var resData = "";

                res.setEncoding('utf8');

                res.on('data', function(chunk) { if(chunk) resData += chunk; });

                res.on('end', function(chunk) {
                    if(chunk)
                        resData += chunk;

                    var result = JSON.parse(resData);
                    if(result.error != undefined && result.error != null && result.error != "") {
                        self.setError(result.error);
                        self.brapper = null;
                        self.unload();
                    } else 
                        self.status = ASIMState.PAUSED;
                });
            });

            request.on('error', function(e) {
                return { success : false, msg : "Unable to pause ASIM '"+this.name+"'\n" };
            });

            request.end();

            return { success : true, msg : "Pausing of ASIM '"+this.name+"' successfully triggered\n" };
        },

        resume : function() {
            if(this.status == ASIMState.ERROR)
                return { success : false, msg : "Cannot resume ASIM '"+this.name+"' as it is in error state\n" };

            if(this.status != ASIMState.PAUSED)
                return { success : false, msg : "Cannot resume ASIM '"+this.name+"' as it is not paused.\n" };

            var options = {
                host: this.brapper.host,
                port: this.brapper.port,
                path: '/asims/'+this.simulation+'/'+this.name+'/resume',
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Content-Length': 0
                }
            };
	    
            var self = this;
            var request = http.request(options, function(res) {
                var resData = "";

                res.setEncoding('utf8');

                res.on('data', function(chunk) { if(chunk) resData += chunk; });

                res.on('end', function(chunk) {
                    if(chunk)
                        resData += chunk;

                    var result = JSON.parse(resData);
                    if(result.error != undefined && result.error != null && result.error != "") {
                        self.setError(result.error);
                        self.brapper = null;
                        self.unload();
                    } else 
                        self.status = ASIMState.RUNNING;
                });
            });

            request.on('error', function(e) {
                return { success : false, msg : "Unable to resume ASIM '"+this.name+"'\n" };
            });

            request.end();

            return { success : true, msg : "Resuming of ASIM '"+this.name+"' successfully triggered\n" };
        },

	stop : function() {
            if(this.status == ASIMState.ERROR)
                return { success : false, msg : "Cannot stop ASIM '"+this.name+"' as it is in error state\n" };

            var options = {
                host: this.brapper.host,
                port: this.brapper.port,
                path: '/asims/'+this.simulation+'/'+this.name+'/stop',
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Content-Length': 0
                }
            };

            var self = this;

            var request = http.request(options, function(res) {
                var resData = "";

                res.setEncoding('utf8');

                res.on('data', function(chunk) { if(chunk) resData += chunk; });

                res.on('end', function(chunk) {
                    if(chunk)
                        resData += chunk;

                    var result = JSON.parse(resData);
                    if(result.error != undefined && result.error != null && result.error != "") {
                        self.setError(result.error);
                        self.brapper = null;
                        self.unload();
                    } else 
                        self.status = ASIMState.RUNNING;
                });
            });

            request.on('error', function(e) {
                return { success : false, msg : "Unable to stop ASIM '"+this.name+"'\n" };
            });

            request.end();

            return { success : true, msg : "Stopping of ASIM '"+this.name+"' successfully triggered\n" };
        },

        setRegisteredLocations : function(reg) {
            if(reg == undefined || reg == null)
                this.registeredLocations = null;
            else
                this.registeredLocations = reg;
        },

        registerLocations : function() {
            /* var localReg = {};
            for(var location in this.registeredLocations) {
                var targets =  this.registeredLocations[location].targets;
                for(var t in targets) {
                    var newReg = { target : targets[t], registrations : [] };
                    for(var a in this.registeredLocations[location].asims) {
                        var asim =  this.registeredLocations[location].asims[a];
                        newReg.registrations.push({ location : location, asim : asim });
                    }

                    if(newReg.registrations.size == 0) {
                        newReg.registrations.push({ location : location });
                    }
                }
            }*/
        },

        run : function() {
            if(this.status == ASIMState.ERROR)
                return { success : true, msg : "Cannot start ASIM '"+this.name+"' as it is in error state\n" };

            var options = {
                host: this.brapper.host,
                port: this.brapper.port,
                path: '/asims/'+this.simulation+'/'+this.name+'/start',
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Content-Length': 0
                }
            };

            var self = this;

            var request = http.request(options, function(res) {
                var resData = "";

                res.setEncoding('utf8');

                res.on('data', function(chunk) { if(chunk) resData += chunk; });

                res.on('end', function(chunk) {
                    if(chunk)
                        resData += chunk;
                    var result = JSON.parse(resData);
                    if(result.error != undefined && result.error != null && result.error != "") {
                        self.setError(result.error);
                        self.brapper = null;
                        self.unload();
                    } else 
                        self.status = ASIMState.RUNNING;
                });
            });

            request.on('error', function(e) {
                return { success : false, msg : "Unable to start ASIM '"+this.name+"'\n" };
            });

            request.end();

            return { success : true, msg : "Starting of ASIM '"+this.name+"' successfully triggered\n" };
        },

        load : function(brapper, callback) {
            if(brapper == undefined || brapper == null) {
                callback(null, { msg : "Unable to load ASIM '"+this.name+"'. Do not know where to load it." });
                return;
            }

            this.brapper = brapper;
            this.brapper.addASIM(this);

            var request = {};
            request.simulation = this.simulation;
            request.signature = this.signature;
            request.program = this.program;
            request.init = this.init;
            request.policy = this.policy;
            request.name = this.name;
            request.start = this.start;

            var data = JSON.stringify(request);

            var options = {
                host: this.brapper.host,
                port: this.brapper.port,
                path: '/asims',
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Content-Length': Buffer.byteLength(data)
                }
            };

            var self = this;
            var req = http.request(options, function(res) {
                var resData = "";

                res.setEncoding('utf8');

                res.on('data', function(chunk) {
                    if(chunk)
                        resData += chunk;
                });

                // TODO: create error in brapper which shows that something went wrong
                res.on('end', function(chunk) {
                    if(chunk)
                        resData += chunk;

                    if(res.statusCode != 201) {
                        callback(null, { code : 400, msg: "Error in creation of ASIM '"+self.getName()+"'.", details : JSON.parse(resData)});
                    } else {
			var result = null;
                        try {
                            result = JSON.parse(resData);
			    if(self.start)
				self.status = ASIMState.RUNNING
			    else
				self.status = ASIMState.LOADED;

			    if(self.simulation != null && self.brapper != null && self.registeredLocations != null) {
				self.register4Updates(function(succ, err) {
				    if(err != null) {
					console.log("WARNING: Registration for updates not successful!");
				    }
				});
			    }
			    
                            callback({ code : 201, msg : "ASIM '" + self.getName() + "' successfully created."}, null);
                        }
                        catch(e) {
                            self.setError(resData);
                            callback(null, { code : 500, msg: "Unexpected response from brapper '"+self.brapper.id+"' during ASIM creation: "+resData });
                        }
                    }
                });
            });

            req.on('error', function(e) {
                callback(null, { code : 500, msg : e });
            });

            req.write(data);
            req.end();
        },

        recvMsg : function(msg, callback) {
            if(this.status == ASIMState.RUNNING) {
                this.brapper.recvMsg(msg, callback);
            } else {
		console.log("WARNING: Message not forwarded. ASIM not running.");
		callback(null, { code : 503, msg : "Message for ASIM '"+this.getName()+"' ignored. It is not running" });
	    }
        },

        recvUpdate : function(msg) {
            if(this.status == ASIMState.RUNNING)
                return this.brapper.recvUpdate(msg);
            else
                return { success : false, msg : "Message for ASIM '"+this.getName()+"' ignored. It is not running" };
        },

        unload : function() {
            // TODO: unload a loaded ASIM from brapper
        },

        reportNewASIM : function(name) {
            if(this.status == ASIMState.ERROR)
                return { success : true, msg : "ERROR: ASIM '"+this.name+"' is in error state\n" };

            var options = {
                host: this.brapper.host,
                port: this.brapper.port,
                path: '/updates/'+this.simulation+'/'+name,
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Content-Length': 0
                }
            };

            var self = this;
            var error = null;

            var request = http.request(options, function(res) {
                var resData = "";
                res.setEncoding('utf8');

                if(res.statusCode != 201) 
                    error = { success : false, msg : "Unable to report new ASIM '"+this.name+"'\n" };
            });

            request.on('error', function(e) {
                error = { success : false, msg : "Unable to report new ASIM '"+this.name+"'\n" };
            });

            request.write("");
            request.end();

            error = { success : true, msg : "New ASIM '"+this.name+"' successfully added.\n" };

            return error;
        },

        reportRemovedASIM : function(name) {
            var options = {
                host: this.brapper.host,
                port: this.brapper.port,
                path: '/updates/'+this.simulation+'/'+name,
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                    'Content-Length': 0
                }
            };

            var self = this;
            var error = null;

            var request = http.request(options, function(res) {
                var resData = "";
                res.setEncoding('utf8');

                if(res.statusCode != 201) 
                    error = { success : false, msg : "Unable to report removed ASIM '"+this.name+"'\n" };
            });

            request.on('error', function(e) {
                error = { success : false, msg : "Unable to report removed ASIM '"+this.name+"'\n" };
            });

            request.write("");
            request.end();

            error = { success : true, msg : "ASIM '"+this.name+"' removal reported successfully.\n" };

            return error;
        },

        register4Updates : function(callback) {
	    for(var trg in this.registeredLocations) {
		var registration = { target : trg, registrations : this.registeredLocations[trg] };
		this.brapper.register4Updates(this.simulation, registration, callback);
	    }
        },

        destroy : function() {
            return this.brapper.destroyASIM(this.simulation, this.name);
        },

        getName : function() {
            return this.name;
        }, 

        setBrapper : function(brapper) {
            this.brapper = brapper;
        },

        delBrapper : function() {
            this.brapper = null;
        },

        setSimulation : function(id) {
            this.simulation = id;
        },

        setState : function(state) {
            this.status = state;
        },

        setError : function(e) {
            this.error = e;
            this.status = ASIMState.ERROR;
        },

        simplify : function() {
            var sASIM = {};
            sASIM.name = this.name;
            sASIM.simulation = this.simulation;
            if(this.brapper != undefined && this.brapper != null)
                sASIM.brapper = this.brapper.getId();
            else
                sASIM.brapper = null;
            sASIM.status = this.status;
            if(this.error)
                sASIM.error = this.error 

            return sASIM;
        }
    };

    return cls;
})();

module.exports = ASIM;
