var ASIM = require("./ASIM");
var ASIMState = require("./ASIMState");
var Brapper = require("./Brapper");

var uuid = require("node-uuid");

var Scheduler = (function() {

    var cls = function(spec) {
        if(spec.name == undefined || spec.name == null) 
            this.name = uuid.v4();
        else
            this.name = spec.name;

        if(spec.signature == undefined || spec.signature == null) {
            var e = new Error();
            e.msg = "Erroneous scheduler specification. Missing signature.";
            e.sender = "Scheduler";
            throw e;
        } else
            this.signature = spec.signature;

        if(spec.program == undefined || spec.program == null) {
            var e = new Error();
            e.msg = "Erroneous scheduler specification. Missing program.";
            e.sender = "Scheduler";
            throw e;
        } else
            this.program = spec.program;

        if(spec.init == undefined || spec.init == null) {
            var e = new Error();
            e.msg = "Erroneous scheduler specification. No init rule specified."
            e.sender = "Scheduler";
            throw e;
        } else
            this.init = spec.init;

        if(spec.policy == undefined || spec.policy == null) {
            var e = new Error();
            e.msg = "Erroneous scheduler specification. No scheduling policy specified.";
            e.sender = "Scheduler";
            throw e;
        } else
            this.policy = spec.policy;

        if(spec.simualtion != undefined || spec.simulation != null) {
            this.simulation = spec.simulation;
        } else 
            this.simulation = null;

        this.brapper = null;
        this.status = ASIMState.IDLE;
    };

    cls.prototype = {
        getName : function() {
            return this.name;
        },

        setSimulation : function(id) {
            this.simulation = id;
        }
    };

    return cls;
})();

module.exports = Scheduler;
