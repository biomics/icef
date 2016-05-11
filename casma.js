var server = require("./server");
var Manager = require("./Manager");

var manager = new Manager();

server.init(manager);
