/*      
 * server.js v1.0
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

var express = require('express');
var http = require('http');

var spawn = require('child_process').spawn;

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
        process.stdout.write("[Scheduler ASIM]: "+data);
    });

    scheduler.stderr.on('data', function(data) {
        process.stdout.write("[Scheduler ASIM]: Error: "+data);
    });

    scheduler.on('error', function(e) {
        // console.log("[Scheduler ASIM]: Error: "+e);
    });

    scheduler.on('exit', function(code) {
        if(code != 0)
            process.stdout.write("[Scheduler ASIM]: Error: "+code);
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

    // send an array of simulation identifiers
    app.get("/simulations", function(req, res) {
        manager.getSimulations(function(success, error) {
            if(!error)
                res.status(200).json({ simulations : success.data } );
            else
                res.send(500);
        });
    });

    app.get("/simulations/:name", function(req, res) {
        var name = req.params.name;
        manager.getSimulation(name, function(success, error) {
            if(success != null && success != undefined) {
                res.status(success.code).json(success.data);
            }
            else {
                res.status(error.code).json({ msg : error.msg });
            }
        });
    });

    app.put("/simulations/:name", function(req, res) {
        var name = req.params.name;
        manager.getSimulation(name, function(success, error) {
            if(success != null && success != undefined) {
                res.status(success.code).json(success.data);
            }
            else {
                res.status(error.code).json({ msg : error.msg });
            }
        });
    });

    // load a simulation by putting a specification
    app.put("/simulations",
            express.json(),
            function(req, res) {
                var result = manager.loadSimulation(req.body, function(success, error) {
                    if(success != null) {
                        res.status(success.code).json(success.data);
                    } else {
                        var msg = {};
                        msg.msg = error.msg;
                        msg.error = error.data;

                        res.status(error.code).json(msg);
                    }
                });
            },
            function(req, res) {
                res.status(500).json({ error : "Unable to load new simulation."});
            }
           );

    // ****************** BRAPPERS ******************

    app.get("/brappers", function(req, res) {
        var allASIMBrappers  = manager.getASIMBrappers();
        var allSchedulerBrappers = manager.getSchedulerBrappers();

        var allBrappers = [];
        for(var k in allASIMBrappers) {
            allBrappers.push(allASIMBrappers[k]);
        }
        for(var k in allSchedulerBrappers) {
            allBrappers.push(allSchedulerBrappers[k]);
        }
        res.send(allBrappers);
    });

    app.put("/brappers", 
            express.json(), 
            function(req, res) {
                // TODO: rewrite method with callback
                var result = manager.registerASIMBrapper(req.body);
                if(result.success) {
                    res.send(200, { msg : result.msg, id : result.id });
                } else {
                    res.send(409, { msg : result.msg });
                }
            },
            function(req, res) {
                res.status(500).json({ error : "Unable to register new brapper."});
            }
    );

    app.get("/brappers/:id", function(req, res) {
        var id = req.params.id;
        var result = manager.getASIMBrapperInfo(id);
        if(result == undefined) {
            res.status(404).json({ msg : "Brapper with id '"+id+"' does not exist."});
        } else {
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

        res.send({ asims : simpleASIMs});
    });

    app.put("/asims", 
             express.json(), 
             function(req, res) {
                 manager.createASIM(req.body, function(success, error) {
                     if(success != null) {
                         res.send(success.code, { msg : success.msg, asim : success.asim});
                     } else {
                         res.send(error.code, { error: error.msg, details : error.details });
                     }
                 });
             }, 
             function(req, res) {
                 res.send(500, "Unable to create new ASIM.");
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

                 if(manager.delASIM(simulation, name))
                     res.send(200);
                 else {
                     console.log("Unable to delete ASIM: "+name);
                     res.send(404);
                 }
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

                manager.recvMsg(simulation, req.body, function(success, error) {
                    if(error != null) {
                        res.send(error.code, { error : error.msg });
                    } else {
                        res.send(success.code, { msg : success.msg });
                    }
                });
            },
            function(error, req, res, next) {
                console.log("Error: Invalid message request. "+error);
                res.send(500, { error : "Manager has a problem processing this message! Check whether message is a valid JSON document.\n" } );
                res.end();
            });
    
    // ****************** Schedulers ******************

    app.get("/schedulers", function(req, res) {
        var allSchedulers = manager.getSchedulers();
        var simpleASIMs = [];

        for(var i in allSchedulers)
            simpleASIMs.push(allSchedulers[i].simplify());

        res.send({ schedulers : simpleASIMs });
    });

    app.get("/schedulers/:simulation", 
            express.json(), 
            function(req, res) {
                var simulation = req.params.simulation;
                
                var allASIMs = manager.getSchedulers(simulation);

                if(allASIMs == null || allASIMs == undefined)
                    res.send(404, { error : "Simulation or schedulers not found." });
                
                var result = [];
                
                for(var i in allASIMs)
                    result.push(allASIMs[i].simplify());
                
                res.send(200, { schedulers : result });
            }, 
            function(req, res) {
                res.send(500, { error : "Internal manager error!" });
            }
           );
    
    app.get("/schedulers/:simulation/:name", 
            express.json(), 
            function(req, res) {
                var simulation = req.params.simulation;
                var name = req.params.name;
                var result = manager.getScheduler(simulation, name);
                if(result == undefined)
                    res.send(404, { error : "Scheduler identified by '"+name+"' does not exist." });
                else
                    res.send(200, result);
            }, 
            function(req, res) {
                res.send(500, "Internal Manager error. Unable to retrieve scheduler.");
            }
           );

    app.put("/schedulers", 
             express.json(), 
             function(req, res) {
                 var result = manager.createScheduler(req.body, function(success, error) {
                     if(success != null) {
                         res.send(success.code, { msg : success.msg, scheduler : success.scheduler});
                     } else {
                         res.send(error.code, { error: error.msg, details : error.details });
                     }
                 });
             }, 
             function(req, res) {
                 res.send(500, "Unable to create new scheduler.");
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
                 res.send(500, "Internal manager error. Unable to control scheduler.");
             }
            );

    app.delete("/schedulers/:simulation/:id", 
             express.json(), 

             function(req, res) {
                 var simulation = req.params.simulation;
                 var name = req.params.id;
                 manager.delScheduler(simulation, name, function(success, error) {
                     if(error != null)
                         res.send(error.code, { error : error.msg });
                     else
                         res.send(success.code, { msg : success.msg});
                 });
             }, 
             function(req, res) {
                 res.send(500, "Internal manager error. Unable to delete scheduler.");
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
                
                 /* console.log("Try to register the following locations in simulation: "+simulation);
                    console.log("Target for updates: "+req.body.target);
                    for(var r in req.body.registrations) {
                    console.log("\tloc> "+req.body.registrations[r].location+"; asim> "+req.body.registrations[r].asim);
                    }
                    console.log("req.body: "+JSON.stringify(req.body));
                 */

                 var result = manager.register4Updates(simulation, req.body, function(success, error) {
                     if(success != null) {
                         res.writeHead(201, { 'Content-Type' : 'text/plain' });
                         res.end(success.msg);
                     }
                     else {
                         res.writeHead(403, { 'Content-Type' : 'text/plain' });
                         res.end(error.msg);
                     }
                 });
                 
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
