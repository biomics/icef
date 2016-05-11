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

    app.get("/brappers", function(req, res) {
        var allWrappers = manager.getWrappers();
        res.send(allWrappers);
    });

    app.put("/brappers/register", 
            express.json(), 
            function(req, res) {
                var result = manager.registerWrapper(req.body);
                if(result.success) {
                    res.send(200, result.msg);
                } else {
                    res.send(400, result.msg);
                }
            },
            function(req, res) {
                console.log("Unable to register new wrapper.");
                res.send(404, "Unable to register new wrapper.");
            }
    );

    app.get("/brappers/delete/:id", function(req, res) {
        var id = req.params.id;
        if(manager.delWrapper(id)) {
            res.send(200, "Wrapper with id '"+id+"' delted successfully.\n");
        } else {
            res.send(400, "Unable to delete wrapper with id '"+id+"'\n.");
        }
    });
 
    app.get("/asims", function(req, res) {
        var allAgents = manager.getAgents();
        res.send(allAgents);
    });

    app.put("/asims/create", 
             express.json(), 
             function(req, res) {
                 var result = manager.createAgent(req.body);
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

    app.put("/asims/delete/:agentname", 
             express.json(), 
             function(req, res) {
                 manager.delAgent(req);
                 res.send(200, "Agent deleted.");
             }, 
             function(req, res) {
                 console.log("Unable to create new agent.");
                 res.send(404, "Unable to create new agent.");
             }
            );

    app.post("/message",
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
    
    app.post("/update",
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
