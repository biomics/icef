var ASIMState = require("./ASIMState");
var ASIMCreationError = require("./ASIMCreationError");

var http = require("http");
var uuid = require("node-uuid");

var ASIM = (function() {        

    var cls = function(spec) {
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
    };

    cls.prototype = {
        control : function(command) {
            console.log("ASIM: command = "+command);
            var cmd = command.toLowerCase();

            switch(command) {
                case "start" : return this.run(); break;
                case "pause" : return this.pause(); break;
                case "resume" : return this.resume(); break;
                default : console.log("Don't know what to do!");
            }

            return { success : false, msg : "Unknown command '"+command+"'.\n" };
        },
        
        pause : function() {
            if(this.status == ASIMState.ERROR)
                return { success : false, msg : "Cannot pause ASIM '"+this.name+"' as it is in error state\n" };

            if(this.status != ASIMState.RUNNING)
                return { success : false, msg : "Cannot pause ASIM '"+this.name+"' as it is not running.\n" };

            var request = { command : "pause" };

            var data = JSON.stringify(request);

            var options = {
                host: this.brapper.host,
                port: this.brapper.port,
                path: '/asims/'+this.simulation+'/'+this.name,
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Content-Length': Buffer.byteLength(data)
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
            
            request.write(data);
            request.end();

            return { success : true, msg : "Pausing of ASIM '"+this.name+"' successfully triggered\n" };
        },

        resume : function() {
            if(this.status == ASIMState.ERROR)
                return { success : false, msg : "Cannot resume ASIM '"+this.name+"' as it is in error state\n" };

            if(this.status != ASIMState.PAUSED)
                return { success : false, msg : "Cannot resume ASIM '"+this.name+"' as it is not paused.\n" };

            var request = { command : "resume" };

            var data = JSON.stringify(request);

            var options = {
                host: this.brapper.host,
                port: this.brapper.port,
                path: '/asims/'+this.simulation+'/'+this.name,
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Content-Length': Buffer.byteLength(data)
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
            
            request.write(data);
            request.end();

            return { success : true, msg : "Resuming of ASIM '"+this.name+"' successfully triggered\n" };
        },

        setRegisteredLocations : function(reg) {
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

            var request = { command : "start" };

            var data = JSON.stringify(request);

            var options = {
                host: this.brapper.host,
                port: this.brapper.port,
                path: '/asims/'+this.simulation+'/'+this.name,
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Content-Length': Buffer.byteLength(data)
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
                    console.log("resData: "+resData);
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
            
            request.write(data);
            request.end();

            return { success : true, msg : "Starting of ASIM '"+this.name+"' successfully triggered\n" };
        },

        load : function() {
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

            var request = http.request(options, function(res) {
                var resData = "";
                
                res.setEncoding('utf8');
                
                res.on('data', function(chunk) {
                    if(chunk)
                        resData += chunk;
                });
                
                res.on('end', function(chunk) {
                    if(chunk)
                        resData += chunk;
                    
                    var result = null;
                    try {
                        result = JSON.parse(resData);
                    } 
                    catch(e) {
                        console.log("ERROR DURING CREATION: "+e);
                        console.log("DATA: "+resData);
                        self.setError(resData);
                        self.brapper = null;
                        self.unload();
                        return;
                    }
                    if(result.error != undefined && result.error != null && result.error != "") {
                        console.log("ERROR DURING CREATION: "+result.error);
                        self.setError(result.error);
                        self.brapper = null;
                        self.unload();
                    } else {
                        if(self.start)
                            self.status = ASIMState.RUNNING;
                        else
                            self.status = ASIMState.LOADED;
                    }
                });
            });
            
            request.on('error', function(e) {
                console.log("Problem: ", e);
            });
            
            request.write(data);
            request.end();
            
            console.log("this.registeredLocations: ", this.registeredLocations);
            for(var reg in this.registeredLocations)
                console.log("REG: "+reg);

            for(var trg in this.registeredLocations) {
                var newRegRequest = { target : trg, registrations : this.registeredLocations[trg] };
                console.log("REGISTRATION REQUEST: " + JSON.stringify(newRegRequest));
                this.brapper.register4Updates(this.simulation, newRegRequest);
            }
        },

        recvMsg : function(msg) {
            if(this.status == ASIMState.RUNNING) {
                return this.brapper.recvMsg(msg);
            } else
                return { success : false, msg : "Message for ASIM '"+this.getName()+"' ignored. It is not running" };
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
                path: '/updates/'+this.simulation+'/asim/'+name,
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
                path: '/updates/'+this.simulation+'/asim/'+name,
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

        register4Updates : function(simId, reg) {
            this.brapper.register4Updates(simId, reg);
        },

        destroy : function(simId, name) {
            return this.brapper.destroyASIM(simId, name);
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
