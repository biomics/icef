var WebSocketServer = require('ws').Server;
var express = require('express');
var http = require('http');

var sys = require('sys');
var spawn = require('child_process').spawn;
var exec = require('child_process').exec;

var app = null;
var server = null;
var manager = null;
var config = null;

var connections = [];

function init(_config, mng) {
    config = _config;
    manager = mng;

    initApp();
    initServer();
    startScheduler();

    manager.socket.start();

    console.log("Manager up and running at http://"+config.httpServer.host+":"+config.httpServer.port);
};

function initServer() {
    server = http.createServer(function(req, res) { app(req,res); });
    server.setMaxListeners(0);

    server.on('connection', function(socket) {
        socket.setNoDelay(true);
    });

    server.on('error', function(err) {
    });

    server.listen(config.httpServer.port, config.httpServer.host, function() {
        process.title = "CoreASM manager";
    });
};

function startScheduler() {
    var args = [];

    args.push("-jar");
    args.push(config.scheduler.jar);
    args.push("-u");
    args.push("-s");
    if(config.scheduler.host) {
        args.push("-h");
        args.push(config.scheduler.host);
    }
    if(config.scheduler.port) {
        args.push("-p");
        args.push(config.scheduler.port);
    }
    if(config.httpServer.host) {
        args.push("-m");
        args.push(config.httpServer.host);
    }
    if(config.httpServer.port) {
        args.push("-mp");
        args.push(config.httpServer.port);
    }

    var scheduler = spawn("java", args);

    scheduler.stdout.on('data', function(data) {
        console.log("[Scheduler ASIM]: "+data);
    });

    scheduler.stderr.on('data', function(data) {
        console.log("[Scheduler ASIM]: Error: "+data);
    });

    scheduler.on('error', function(e) {
        console.log("[Scheduler ASIM]: Error: "+e);
    });

    scheduler.on('exit', function(code) {
        if(code != 0)
            console.log("[Scheduler ASIM]: Error: "+code);
    });

    manager.registerSchedulerBrapper(config.scheduler);
}

function initApp() {
    app = express();

    app.use(function(req,res,next){
        req.connection.setNoDelay(true);
        next();
    });

    // ****************** SIMULATIONS ****************** 

    app.get("/simulations", function(req, res) {
        var simus = manager.getSimulations();
        res.send(simus);
    });

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

    // TODO: SOME DELETION

    // ****************** BRAPPERS ******************

    app.get("/brappers", function(req, res) {
        var allWrappers = manager.getASIMBrappers();
        res.send(allWrappers);
    });

    app.put("/brappers", 
            express.json(), 
            function(req, res) {
                var result = manager.registerASIMBrapper(req.body);
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
        var result = manager.getASIMBrapper(id);
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
        var allASIMs = manager.getASIMs();
        var simpleASIMs = [];

        for(var i in allASIMs)
            simpleASIMs.push(allASIMs[i].simplify());
        res.send(simpleASIMs);
    });

    app.put("/asims", 
             express.json(), 
             function(req, res) {
                 var result = manager.createASIM(req.body);
                 if(result.success) {
                     if(result.asim == undefined)
                         res.send(204);
                     else {
                         var response = {};
                         response.name = result.asim.name;
                         response.simulation = result.asim.simulation;
                         response.success = true;
                         response.error = "";
                         res.send(201, response);
                     }
                 } else {
                     res.send(409, result);
                 }
             }, 
             function(req, res) {
                 res.send(500, "Unable to create new agent.");
             }
            );

    app.get("/asims/:simulation", 
             express.json(), 
             function(req, res) {
                 var simulation = req.params.simulation;
                 
                 var allASIMs = manager.getASIMs(simulation);
                 var result = [];
                 
                 for(var i in allASIMs)
                     result.push(allASIMs[i].simplify());

                 if(result == undefined)
                     res.send(404);
                 else
                     res.send(200, result);
             }, 
             function(req, res) {
                 res.send(500, "Unable to retrieve ASIM");
             }
            );
    
    app.get("/asims/:simulation/:name", 
             express.json(), 
             function(req, res) {
                 var simulation = req.params.simulation;
                 var name = req.params.name;
                 var result = manager.getASIM(simulation, name);
                 if(result == undefined)
                     res.send(404);
                 else
                     res.send(200, result);
             }, 
             function(req, res) {
                 res.send(500, "Unable to retrieve ASIM");
             }
            );

    app.put("/asims/:simulation/:name", 
             express.json(), 
             function(req, res) {
                 var simulation = req.params.simulation;
                 var name = req.params.name;

                 var result = manager.controlASIM(simulation, name, req.body.command);
                 if(!result.success)
                     res.send(404);
                 else
                     res.send(200, result.msg);
             }, 
             function(req, res) {
                 res.send(500, "Unable to retrieve ASIM");
             }
            );

    app.delete("/asims/:simulation/:name", 
             express.json(), 
             function(req, res) {
                 var simulation = req.params.simulation;
                 var name = req.params.name;

                 console.log("Request deletion of ASIM: "+name);

                 if(manager.delASIM(simulation, name)) 
                     res.send(200);
                 else
                     res.send(404);
             }, 
             function(req, res) {
                 res.send(500, "Unable to delte ASIM.");
             }
            );

    // ****************** Message ******************

    app.put("/message/:simulation",
            express.json(),
            function(req, res) {
                var simulation = req.params.simulation;
         
                var result = manager.recvMsg(simulation, req.body);

                if(!result.success) 
                    console.log("ERROR: "+result.msg);

                res.set("Connection", "close");
                res.send(204);
                res.end();
            },
            function(error, req, res, next) {
                res.set("Connection", "close");
                console.log("Error: Invalid message request. "+error);
                res.send(400, "Invalid message request. Check format!\n");
                res.end();
            }
            );
    
    // ****************** Schedulers ******************

    app.get("/schedulers", function(req, res) {
        res.send(501);
    });

    app.put("/schedulers", 
             express.json(), 
             function(req, res) {
                 var result = manager.createASIM(req.body);
                 if(result.success) {
                     if(result.asim == undefined)
                         res.send(204);
                     else {
                         var response = {};
                         response.name = result.asim.name;
                         response.simulation = result.asim.simulation;
                         response.success = true;
                         response.error = "";
                         res.send(201, response);
                     }
                 } else {
                     res.send(409, result);
                 }
             }, 
             function(req, res) {
                 res.send(500, "Unable to create new scheduler ASIM.");
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

    app.put("/schedulers/:simulation/:name", 
             express.json(), 
             function(req, res) {
                 var simulation = req.params.simulation;
                 var name = req.params.name;
                 
                 var result = manager.controlScheduler(simulation, name, req.body.command);

                 if(!result.success)
                     res.send(404);
                 else
                     res.send(200, result.msg);
             }, 
             function(req, res) {
                 res.send(500, "Unable to control scheduler.");
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

    app.put("/updates/:simulation",
             express.json(),
             function(req, res) {
                var simulation = req.params.simulation;
                 var result = manager.recvUpdate(simulation, req.body);
                 if(!result.success) {
                     console.log("ERROR: "+result.msg);
                     res.send(400, result.msg);
                 } else {
                     res.send(200);
                 }
             },
             function(error, req, res, next) {
                 console.log("Error: Invalid update request. "+error);
                 res.send(500, "Invalid update request. Check format!\n");
             }
            );

    app.put("/updates/:simulation/register",
             express.json(),
             function(req, res) {
                 var simulation = req.params.simulation;
                
                 console.log("Try to register the following locations in simulation: "+simulation);
                 console.log("Target for updates: "+req.body.target);
                 for(var r in req.body.registrations) {
                     console.log("\tloc> "+req.body.registrations[r].location+"; asim> "+req.body.registrations[r].asim);
                 }
                 console.log("req.body: "+JSON.stringify(req.body));

                 var result = manager.register4Updates(simulation, req.body);

                 console.log("RESULT: ",result);
                 
                 if(result.success) 
                     res.send(200);
                 else {
                     res.send(403, result.msg);
                 }
                         
                 res.end();
                 
             },
             function(error, req, res, next) {
                 res.set("Connection", "close");
                 res.send(500, "Unable to register locations: "+error);
                 res.end();
             }
            );
};

var serverAPI = module.exports = {
    init : init
};
