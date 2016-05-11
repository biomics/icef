var ASIM = require("./ASIM");
var http = require("http");

var id = 1;

var Brapper = (function() {
    var cls = function(host, port) {
        this.id = id++;
        this.host = host;
        this.port = port;

        this.agents = {};
        this.load = 0;
    };

    cls.prototype = {
        getKey : function() {
            return this.host + "_" + this.port;
        },

        getLoad : function() {
            return this.load;
        },

        createASIM : function(descr) {
            this.agents[descr.name] = new ASIM(descr.name, descr.program);
            this.load++;
        },

        recvMsg : function(msg) {
            console.log("Sending message to wrapper at "+this.host+":"+this.port+"\n");

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
