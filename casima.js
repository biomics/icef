var Manager = require("./casima/Manager");

var server = require("./server/server");
var Socket = require("./server/Socket");
var config = require("./config");

var socket = new Socket();
var manager = new Manager(socket);

server.init(config, manager);
