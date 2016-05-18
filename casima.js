var Manager = require("./casima/Manager");
var Scheduler = require("./casima/Scheduler");
//var Channeler = require("./casima/Channeler");
var Updater = require("./casima/Updater");

var server = require("./server/server");
var config = require("./config");

var updater = new Updater();
//var channeler = new Channeler();

var manager = new Manager();

server.init(config, manager);
