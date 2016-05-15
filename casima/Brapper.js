var ASIM = require("./ASIM");
var http = require("http");
var uuid = require("node-uuid");

var id = 1;

var Brapper = (function() {
    var cls = function(host, port) {
        this.id = uuid.v4();
        this.host = host;
        this.port = port;

        this.asims = {};
        this.load = 0;
    };

    cls.prototype = {
        getKey : function() {
            return this.id;
        },

        getLoad : function() {
            return this.load;
        },

        setError : function(e) {
            this.error = e;
        },

        createASIM : function(asim , callback) {
            this.asims[asim.getName()] = asim;
            this.load++;

            var request = {};
            request.program = asim.program;
            request.init = asim.init;
            request.policy = asim.policy;
            request.name = asim.name;

            data = JSON.stringify(request);

            var options = {
                host: this.host,
                port: this.port,
                path: '/asims',
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Content-Length': Buffer.byteLength(data)
                }
            };

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

                    callback(JSON.parse(resData));
                });
            });
            
            request.on('error', function(e) {
                console.log("Problem: ", e);          
            });
            
            request.write(data);
            request.end();
        },

        getASIMs : function() {
            return this.asims;
        },

        recvMsg : function(msg) {
            console.log("Sending message to brapper at "+this.host+":"+this.port+"\n");

            var data = JSON.stringify(msg);

            var options = {
                host: this.host,
                port: this.port,
                path: '/message',
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Content-Length': Buffer.byteLength(data)
                }
            };

            var request = http.request(options, function(res) {
                res.setEncoding('utf8');
                res.on('data', function(chunk) {
                    console.log("Response: "+chunk);
                });
                res.on('end', function(chunk) {
                    console.log("Response: "+chunk);
                });
            });

            request.on('error', function(e) {
                console.log("Problem: ", e);
            });

            request.write(data);
            request.end();

            return { success : true, msg : "Message forwarded\n" };
        }
    };

    return cls;
})();

module.exports = Brapper;
