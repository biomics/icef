/*
 * Brapper.js v1.0
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
            this.asims[asim.getName()] = asim.getName();
            this.load++;

            asim.setBrapper(this);
        },

        getASIMs : function() {
            return Object.keys(this.asims);
        },

	// TODO turn into async call
        destroyASIM : function(simId, name) {
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
                res.setEncoding('utf8');
            });

            request.setNoDelay(true);

            request.on('error', function(e) {
                console.log("[Manager]: Brapper: Error: Problem destroying ASIM: ", e);
            });

            request.end();

	    delete this.asims[name];
	    this.load--;

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
                console.log("[Manager]: Brapper: Error: Unable to send update to '"+this.host+":"+this.port+"'.Problem: ", e);
            });

            request.write(data);
            request.end();

            return { success : true, msg : "Update forwarded\n" };
        },

        recvMsg : function(msg, callback) {            
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
                    callback({ code : 200, msg : "Message delivered\n" }, null);
                });
            });

            request.setNoDelay(true);

            request.on('error', function(e) {
                callback(null, { code : 400, msg : "Something went wrong in brapper.\n" });
            });

            request.write(data);
            request.end();

            return { success : true, msg : "Message forwarded" };
        },

        register4Updates : function(simId, reg, callback) {
            var data = JSON.stringify(reg);

            console.log("[Manager]: Send registrations to '"+this.host+":"+this.port+"'");

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

            var self = this;
            var request = http.request(options, function(res) {

                res.setEncoding('utf8');

                res.on('data', function(chunk) {
                    // nothing to do
                });

                res.on('end', function(chunk) {
                    console.log("[Manager]: Registration ended for '"+self.host+":"+self.port+"'");
                    callback({ success : true, msg : "Registration successful\n" });
                });
            });

            request.on('error', function(e) {
                console.log("[Manager]: Brapper: ERROR: "+e);
                callback({ success : false, msg : "Something went wrong\n" });
            });

            request.write(data);
            request.end();
        }
    };

    return cls;
})();

module.exports = Brapper;
