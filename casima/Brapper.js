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

        recvMsg : function(msg, callback) {
            // console.log("BRAPPER.JS Sending message to brapper at "+this.host+":"+this.port+"\n");
            
            var data = JSON.stringify(msg);

            var options = {
                host: this.host,
                port: this.port,
                path: '/message/'+msg.simulation,
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Content-Length': Buffer.byteLength(data)
                }
            };

            var request = http.request(options, function(res) {

                // console.log("BRAPPER.JS res.statusCode == "+res.statusCode+" (host "+options.host+":"+options.port+")");

                res.setEncoding('utf8');

                res.on('data', function(chunk) {
                    // console.log("BRAPPER.JS SOME DATA ARRIVING");
                })

                res.on('end', function(chunk) {
                    // console.log("BRAPPER.JS REQUEST FINISHED!!");
                    // callback({ success : true, msg : "Message forwarded\n" });
                });
            });

            request.on('error', function(e) {

                // console.log("BRAPPER.JS: AN ERROR OCCURRED");
                console.log("[Manager]: Brapper: ERROR: "+e);
                // callback({ success : false, msg : "Something went wrong\n" });
            });

            // request.write(data);
            request.end(data);
        }
    };

    return cls;
})();

module.exports = Brapper;
