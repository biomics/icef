/*      
 * casima.js v1.0
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

var Manager = require("./casima/Manager");

var server = require("./server/server");
var Socket = require("./server/Socket");
var config = require("./config");

var socket = new Socket();
var manager = new Manager(socket);

server.init(config, manager);
