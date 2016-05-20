var Manager = require("./casima/Manager");
// var Channeler = require("./casima/Channeler");
// var Updater = require("./casima/Updater");

var server = require("./server/server");
var config = require("./config");

var manager = new Manager();
server.init(config, manager);
