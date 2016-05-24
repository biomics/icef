var ASIM = require("./ASIM");
var http = require("http");
var uuid = require("node-uuid");

var id = 1;

var Brapper = (function() {
    var cls = function(host, port, type) {
        this.id = uuid.v4();
        this.host = host;
        this.port = port;

        this.type = type ? type : "asim";
        this.asims = {};
        this.load = 0;
    };

    cls.prototype = {
        getId : function() {
            return this.id;
        },

        getLoad : function() {
            return this.load;
        },

        setError : function(e) {
            this.error = e;
        },

        addASIM : function(asim) {
            this.asims[asim.getName()] = asim;
            this.load++;

            asim.setBrapper(this);
        },

        getASIMs : function() {
            return this.asims;
        },

        destroyASIM : function(simId, name) {
            console.log("Brapper.js: Destroy ASIM '"+name+"' in simulation '"+simId+"'");

            var asim = this.asims[name];
            if(asim == undefined)
                return false;

            var options = {
                host: this.host,
                port: this.port,
                path: "/asims/"+simId+"/"+name,
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                    'Content-Length': 0
                }
            };

            var request = http.request(options, function(res) {
                if(res.statusCode)
                    console.log("Response: "+res.statusCode);
                res.setEncoding('utf8');
            });

            request.setNoDelay(true);

            request.on('error', function(e) {
                console.log("[Manager]: Brapper: Error: Problem destroying ASIM: ", e);
            });

            request.end();

            console.log("RETURN TRUE");

            return true;
        },

        recvUpdate : function(msg)  {
            var data = JSON.stringify(msg);

            var options = {
                host: this.host,
                port: this.port,
                path: '/updates/'+msg.simulation,
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Content-Length': Buffer.byteLength(data)
                }
            };

            var request = http.request(options, function(res) {
                res.setEncoding('utf8');
                res.on('data', function(chunk) {
                    // console.log("Response: "+chunk);
                });
                res.on('end', function(chunk) {
                    // console.log("Response: "+chunk);
                });
            });

            request.on('error', function(e) {
                console.log("[Manager]: Brapper: Error: Problem: ", e);
            });

            request.write(data);
            request.end();

            return { success : true, msg : "Update forwarded\n" };
        },

        recvMsg : function(msg) {
            // console.log("BRAPPER.JS Sending message to brapper at "+this.host+":"+this.port+"\n");
            
            var data = JSON.stringify(msg);

            var options = {
                host: this.host,
                port: this.port,
                path: '/message/'+msg.simulation,
                method: 'PUT',
                agent : false,
                headers: {
                    'Content-Type': 'application/json',
                    'Content-Length': Buffer.byteLength(data),
                    'Connection' : 'Close',
                }
            };

            var request = http.request(options, function(res) {

                res.setEncoding('utf8');

                res.on('data', function(chunk) {
                    // console.log("BRAPPER.JS SOME DATA ARRIVING");
                })

                res.on('end', function(chunk) {
                    return { success : true, msg : "Message forwarded\n" };
                });
            });

            request.setNoDelay(true);

            request.on('error', function(e) {
                // console.log("BRAPPER.JS: AN ERROR OCCURRED");
                console.log("[Manager]: Brapper: ERROR: "+e);
                // callback({ success : false, msg : "Something went wrong\n" });
            });

            request.write(data);
            request.end();

            return { success : true, msg : "Message forwarded" };
        },

        register4Updates : function(simId, reg) {
            var data = JSON.stringify(reg);

            var options = {
                host: this.host,
                port: this.port,
                path: '/updates/'+simId+"/register",
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Content-Length': Buffer.byteLength(data),
                }
            };

            var request = http.request(options, function(res) {

                res.setEncoding('utf8');

                res.on('data', function(chunk) {
                    // console.log("BRAPPER.JS SOME DATA ARRIVING");
                })

                res.on('end', function(chunk) {
                    return { success : true, msg : "Message forwarded\n" };
                });
            });

            request.setNoDelay(true);

            request.on('error', function(e) {
                // console.log("BRAPPER.JS: AN ERROR OCCURRED");
                console.log("[Manager]: Brapper: ERROR: "+e);
                // callback({ success : false, msg : "Something went wrong\n" });
            });

            request.write(data);
            request.end();

            return { success : true, msg : "Message forwarded" };
        }
    };

    return cls;
})();

module.exports = Brapper;
