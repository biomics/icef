var server = require("./server/server");
var Manager = require("./casima/Manager");

var manager = new Manager();

server.init(manager);
