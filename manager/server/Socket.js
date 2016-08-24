/*      
 * Socket.js v1.0
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

var WebSocketServer = require('ws').Server;

var rateA = 1;
var rateB = 1;

var Socket = (function() {
    var rateUnit = 60000;

    var cls = function() {
        this.manager = null;
        this.ASIMupdates = {};
        

        this.server = null;
        this.socket = null;
        this.connections = [];
        this.registry = {};
        this.allCells = [];
        this.req = 0;

        this.updates = [];

        this.divisionThreshold = 5;
        this.specializeThreshold = 10;
        this.maxHopLength = 20;
        
        this.substances = {
            divide : this.divisionThreshold,
            specialize : 0
        };
    };

    cls.prototype.start = function(config, manager) {
        this.manager = manager;

        var self = this;

        setTimeout(function() { self.send2Clients(); }, 500);

        this.socket = new WebSocketServer({port :3000 });

        this.socket.on('connection', function connection(ws) {
            console.log("Open connection ... ");
            
            self.connections.push(ws);

            ws.on('message', function incoming(message) {
                var msg = JSON.parse(message);
                console.log("- Receive message: '"+msg.cmd+"'");
                switch(msg.cmd) {
                case "request" : 
                    console.log("Simulate a request ... ");
                    self.sendManualRequest();
                    break;
                case "run" :
                    console.log("Start running simulation");
                    self.req = 0;

                    var msg = {};
                    msg.cmd = "reset";
                    self.updateClients(msg);
                                       
                    for(var t in self.registry) {
                        self.scheduleCell(t);
                    }

                    self.sendNewRequest();

                    break;
                case "state" : 
                    console.log("Sync with client ... ");
                    self.initClient(ws);
                    break;
                case "quantity" : 
                    console.log("************ QUANTITY ************");
                    if(msg.type) {
                        self.getCellQuantity(this, msg.type);
                    }
                    
                    break;
                }
            });

            ws.on('close', function(code, reason) {
                for(var c in self.connections) {
                    var connection = self.connections[c];
                    
                    if(connection == ws) {
                        self.connections.splice(c, 1);
                        break;
                    }
                }
            });
        });  
    };

    cls.prototype.getCellQuantity = function(connection, type) {
        var quantity = 0;

        this.allCells.forEach(function(cur, i, arr) { if(cur.cell.type == type) quantity++; });

        // console.log("Number of elements ("+type+"): "+quantity);

        connection.send(JSON.stringify({ cmd : "quantity", type : type, value : quantity}));
    };

    cls.prototype.injectCell = function(c) {
        c.reactor = this;
        
        /* var msg = {};
        msg.cmd = "state";
        msg.cells = [ { id : c.id,
                        type : c.type,
                        left : c.lNeighbour ? c.lNeighbour.id : null,  
                        right : c.rNeighbour ? c.rNeighbour.id : null } ];

        this.updateClients(msg);*/

        this.allCells.push({ id : c.id, cell : c });
        c.live();
    };

    cls.prototype.registerCell = function(cell, rate) {
        this.registry[cell.type] = { cell : cell, rate : rate };
    };

    cls.prototype.scheduleCell = function(type) {
        console.log("Schedule cell of type: "+type);

        var cell = this.registry[type].cell;
        var rate = this.registry[type].rate;
        var quantity = 0;
        var time = rateUnit;

        if(rate > rateUnit) {
            quantity = Math.round(rate / rateUnit);
            time = 1;
        } else {
            quantity = 1;
            time = rateUnit / rate;
        }

        var self = this;
        for(var i = 0; i < quantity; i++) {
            setTimeout(function() { 
                self.scheduleCell(type); 
            }, time);
        }

        var newCell = new Cell(cell.type, cell.msgHandler);
        this.eventHandler("add", newCell);
    };
    
    // chose a random cell to serve request
    cls.prototype.choose = function() {
        var chosenCell = null;

        if(this.allCells.length) {
            var chosen = Math.floor(Math.random() * this.allCells.length);
            /* console.log("Ring size: " + this.allCells.length);
            console.log("Cell to serve request: "+chosen); */
            
            for(var connection in this.connections) {
                this.connections[connection].send(JSON.stringify(this.allCells.length));
            }
            
            chosenCell = this.allCells[chosen].cell;
            // console.log("chosen cell: "+chosenCell);
        }

        return chosenCell;
    };

    cls.prototype.send2Clients = function() {
        var self = this;

        this.getUpdates();

        var oldQueue = this.updates;

        if(oldQueue.length && this.connections.length) {
            for(var conn in this.connections) {
                var ws = this.connections[conn];
                ws.send(JSON.stringify(oldQueue));
            }
        }

        this.updates = [];

        setTimeout(function() { self.send2Clients(); }, 250);
    };

    cls.prototype.updateClients = function(msg) {
        this.updates.push(msg);
    };

    cls.prototype.initClient = function(connection) {
        var msg = {};
        msg.cmd = "state";
        msg.cells = [];
        for(var cid in this.allCells) {
            msg.cells.push({ id : this.allCells[cid].id,
                             type : this.allCells[cid].type,
                             left : (this.allCells[cid].cell.lNeighbour ? this.allCells[cid].cell.lNeighbour.id : null),
                             right : (this.allCells[cid].cell.rNeighbour ? this.allCells[cid].cell.rNeighbour.id : null) });
        }
        console.log("initClient with ",msg);
        
        connection.send(JSON.stringify(msg));
    };
    
    cls.prototype.eventHandler = function(event, cell) {
        var msg = {};
        switch(event) {
        case "serve" : {
            msg.cmd = "serve";
            msg.id = cell.id;
            msg.type = cell.type;
            this.updateClients(msg);
            break;
        }
        case "finish" : {
            msg.cmd = "finish";
            msg.id = cell.id;
            msg.type = cell.type;
            this.updateClients(msg);
            break;
        }
        case "add" : {
            this.injectCell(cell);

            msg.cmd = "add";
            msg.id = cell.id;
            msg.type = cell.type;
            msg.left = cell.lNeighbour.id;
            msg.right = cell.rNeighbour.id;

            this.updateClients(msg);
            break;
        }
        case "split" : {
            this.injectCell(cell);
            
            msg.cmd = "split";
            msg.id = cell.id;
            msg.type = cell.type;
            msg.left = cell.lNeighbour.id;
            msg.right = cell.rNeighbour.id;
            this.updateClients(msg);
            
            /* console.log("Split cell: ",cell);
            console.log("current ring: ");
            for(var c in this.allCells) {
                var cell = this.allCells[c];
                
                console.log(c+": "+cell.cell);
            }*/
            
            break;
        }
        case "die" : {
            for(var id in this.allCells) {
                if(this.allCells[id].id == cell.id) {
                    this.allCells.splice(id, 1);
                    console.log("FOUND MATCH");
                    break;
                }
            }
            
            msg.cmd = "die";
            msg.id = cell.id;
            msg.left = cell.lNeighbour.id;
            msg.right = cell.rNeighbour.id;
            this.updateClients(msg);
            
            break;
        }
        }
    };

    cls.prototype.sendManualRequest = function() { 
        this.req++;
        
        var msg = {};
        var cell = this.choose();
        
        if(cell) 
            cell.request("Request "+this.req);
    };

    cls.prototype.sendNewRequest = function() {
        var str = "Request "+this.req;
        this.req++;
        
        var msg = {};
        var cell = this.choose();

        if(cell) {
            console.log("Cell to send request to: "+ cell);
        
            cell.request(str);
            
            var newRequest = 200; // + Math.sin(this.req/20) * 400;
            console.log("NEW REQUEST: "+newRequest);
            // if(req < 1)
            var self = this;
            setTimeout(function() { self.sendNewRequest(); }, newRequest);
        }
    };

    cls.prototype.addASIM = function(asim) {
        var msg = {};
        msg.cmd = "add";
        msg.id = asim.name;
        msg.type = "A";

        this.updateClients(msg);
    };

    cls.prototype.delASIM = function(asim) {
        var msg = {};
        msg.cmd = "del";
        msg.id = asim;
        msg.type = "A";

        this.updateClients(msg);
    };
    
    cls.prototype.putUpdates = function(updates) {
        for(var asim in updates) {
            for(var location in updates[asim]) {
                if(this.ASIMupdates[asim] == undefined)
                    this.ASIMupdates[asim] = {};
                (this.ASIMupdates[asim])[location] = (updates[asim])[location];
            }
        }
    };

    cls.prototype.getUpdates = function(updates) {
        var msg = {};
        msg.cmd = "updates";
        msg.updates = this.ASIMupdates;

        this.updateClients(msg);

        this.ASIMupdates = {};
    };

    return cls;
})();

module.exports = Socket;
