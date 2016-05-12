var WebSocketServer = require('ws').Server;
var config = require('./../config.js');
var express = require('express');
var http = require('http');

var app = null;
var server = null;
var manager = null;

function init(mng) {
    console.log("Starting manager server ...");

    manager = mng;
    
    initApp();
    initServer();
};

function initServer() {
    server = http.createServer(function(req, res) { app(req,res); });
    server.setMaxListeners(0);

    server.on('error', function(err) {
    });

    server.listen(config.httpServer.port, config.httpServer.host, function() {
        process.title = "CoreASM manager";
    });
};

function initApp() {
    app = express();

    // ****************** BRAPPERS ******************

    app.get("/brappers", function(req, res) {
        var allWrappers = manager.getBrappers();
        res.send(allWrappers);
    });

    app.put("/brappers", 
            express.json(), 
            function(req, res) {
                var result = manager.registerWrapper(req.body);
                if(result.success) {
                    res.send(200, result.msg);
                } else {
                    res.send(409, result.msg);
                }
            },
            function(req, res) {
                console.log("Unable to register new wrapper.");
                res.status(500).json({ error : "Unable to register new wrapper."});
            }
    );

    app.get("/brappers/:id", function(req, res) {
        var id = req.params.id;
        var result = manager.getBrapper(id);
        if(result == undefined) {
            res.status(404).json({ error : "Brapper with id '"+id+"' has not been registered."});
        } 
        else {
            res.status(200).send(result);
        }
    });

    app.delete("/brappers/:id", function(req, res) {
        var id = req.params.id;
        if(manager.delWrapper(id)) {
            res.send(200, "Wrapper with id '"+id+"' delted successfully.\n");
        } else {
            res.json("Unable to delete wrapper with id '"+id+"'\n.", 400);
        }
    });

    // ****************** ASIM ******************
 
    app.get("/asims", function(req, res) {
        var allAgents = manager.getASIMs();
        res.send(allAgents);
    });

    app.put("/asims", 
             express.json(), 
             function(req, res) {
                 var result = manager.createASIM(req.body);
                 if(result.success) {
                     res.send(200, result.msg);
                 } else {
                     res.send(400, result.msg);
                 }
             }, 
             function(req, res) {
                 console.log("Unable to create new agent.");
                 res.send(404, "Unable to create new agent.");
             }
            );
    
    app.get("/asims/:asimname", 
             express.json(), 
             function(req, res) {
                 // TODO get the status info of an ASIM
                 res.send(200, "ASIM deleted.");
             }, 
             function(req, res) {
                 console.log("Unable to create new agent.");
                 res.send(404, "Unable to create new agent.");
             }
            );

    app.put("/asims/:asimname", 
             express.json(), 
             function(req, res) {
                 // TODO change the status of the ASIM
                 res.send(200, "ASIM deleted.");
             }, 
             function(req, res) {
                 console.log("Unable to create new agent.");
                 res.send(404, "Unable to create new agent.");
             }
            );

    app.delete("/asims/:asimname", 
             express.json(), 
             function(req, res) {
                 manager.delASIM(req);
                 res.send(200, "ASIM deleted.");
             }, 
             function(req, res) {
                 console.log("Unable to create new agent.");
                 res.send(404, "Unable to create new agent.");
             }
            );

    app.put("/message",
            express.json(),
            function(req, res) {
                var result = manager.recvMsg(req.body);

                if(!result.success) {
                    res.send(400, result.msg);
                } else {
                    res.send(200);
                }
            },
            function(error, req, res, next) {
                console.log("Error: Invalid message request. "+error);
                res.send(400, "Invalid message request. Check format!\n");
            }
            );
    
    app.put("/update",
             express.json(),
             function(req, res) {
                 var result = manager.recvUpdate(req.body);

                 if(!result.success) {
                     res.send(400, result.msg);
                 } else {
                     res.send(200);
                 }
             },
             function(error, req, res, next) {
                 console.log("Error: Invalid update request. "+error);
                 res.send(400, "Invalid update request. Check format!\n");
             }
            );
};

var serverAPI = module.exports = {
    init : init
};
