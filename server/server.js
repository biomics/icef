var WebSocketServer = require('ws').Server;
var express = require('express');
var http = require('http');

var sys = require('sys');
var exec = require('child_process').exec;

var app = null;
var server = null;
var manager = null;
var config = null;

function init(_config, mng) {
    console.log("Starting manager server ...");

    config = _config;
    manager = mng;
    
    initApp();
    initServer();
    startUpdater();
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

function startUpdater() {
    var cmd = "java -jar "+config.updater.path+" ";
    cmd = cmd + (config.updater.host ? " -h " + config.updater.host : "" );
    cmd = cmd + (config.updater.port ? " -p " + config.updater.port : "" );

    var updater = exec(cmd, function(error, stdout, stderr) {
        if(error != null) {
            console.log("Fatal error: Update ASIM crashed");
            console.log(error);
            process.exit(1);

            console.log("stdout: "+stdout);
            console.log("stderr: "+stderr);

            // TODO: try to restart
        }
    });
}

function initApp() {
    app = express();

    // ****************** SIMULATIONS ****************** 

    app.put("/simulations",
            express.json(),
            function(req, res) {
                var result = manager.loadSimulation(req.body);
                if(result.success) {
                    res.send(200, result);
                } else {
                    res.send(409, result);
                }
            },
            function(req, res) {
                console.log("Unable to load new simulation.");
                res.status(500).json({ error : "Unable to load new simulation."});
            }
           );

    // ****************** BRAPPERS ******************

    app.get("/brappers", function(req, res) {
        var allWrappers = manager.getBrappers();
        res.send(allWrappers);
    });

    app.put("/brappers", 
            express.json(), 
            function(req, res) {
                var result = manager.registerBrapper(req.body);
                if(result.success) {
                    res.send(200, result);
                } else {
                    res.send(409, result);
                }
            },
            function(req, res) {
                console.log("Unable to register new brapper.");
                res.status(500).json({ error : "Unable to register new brapper."});
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
                     if(result.asim == undefined)
                         res.send(204);
                     else
                         res.send(200, result);
                 } else {
                     res.send(409, result);
                 }
             }, 
             function(req, res) {
                 res.send(500, "Unable to create new agent.");
             }
            );
    
    app.get("/asims/:id", 
             express.json(), 
             function(req, res) {
                 var id = req.params.id;
                 var result = manager.getASIM(id);
                 if(result == undefined)
                     res.send(404);
                 else
                     res.send(200, result);
             }, 
             function(req, res) {
                 res.send(500, "Unable to retrieve ASIM");
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

    // ****************** Message ******************

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
    
    // ****************** Schedulers ******************

    app.get("/schedulers", function(req, res) {
        res.send(501);
    });

    app.put("/schedulers", 
             express.json(), 
             function(req, res) {
                 res.send(501);
             }, 
             function(req, res) {
                 res.send(501);
             }
            );
    
    app.get("/schedulers/:id", 
             express.json(), 
             function(req, res) {
                 res.send(501);
             }, 
             function(req, res) {
                 res.send(501);
             }
            );

    app.put("/schedulers/:id", 
             express.json(), 
             function(req, res) {
                 res.send(501);
             }, 
             function(req, res) {
                 res.send(501);
             }
            );

    app.delete("/schedulers/:id", 
             express.json(), 

             function(req, res) {
                 res.send(501);
             }, 
             function(req, res) {
                 res.send(501);
             }
            );

    // ****************** Channels ******************

    app.get("/channels", function(req, res) {
        res.send(501);
    });

    app.put("/channels", 
             express.json(), 
             function(req, res) {
                 res.send(501);
             }, 
             function(req, res) {
                 res.send(501);
             }
            );
    
    app.get("/channels/:id", 
             express.json(), 
             function(req, res) {
                 res.send(501);
             }, 
             function(req, res) {
                 res.send(501);
             }
            );

    app.put("/channels/:id", 
             express.json(), 
             function(req, res) {
                 res.send(501);
             }, 
             function(req, res) {
                 res.send(501);
             }
            );

    app.delete("/channels/:id", 
             express.json(), 

             function(req, res) {
                 res.send(501);
             }, 
             function(req, res) {
                 res.send(501);
             }
            );

    // ****************** Updates ******************    

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

    app.put("/update/register",
             express.json(),
             function(req, res) {
                 res.send(501);
             },
             function(error, req, res, next) {
                 res.send(501);
             }
            );
};

var serverAPI = module.exports = {
    init : init
};
