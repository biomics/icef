var ASIMState = require("./ASIMState");
var uuid = require("node-uuid");

var ASIM = (function() {        

    var cls = function(_name, _signature, _init, _program, _policy) {
        if(_name == undefined || _name == null) 
            this.name = uuid.v4();
        else
            this.name = _name;

        if(_signature == undefined || _signature == null) {
            var e = new Error("Erroneous ASIM specification. Missing signature.");
            e.sender = "ASIM";
            throw e;
        } else
            this.signature = _signature;


        if(_program == undefined || _program == null) {
            var e = new Error("Erroneous ASIM specification. Missing program.");
            e.sender = "ASIM";
            throw e;
        } else
            this.program = _program;

        if(_init == undefined || _init == null) {
            var e = new Error("Erroneous ASIM specification. No init rule specified.");
            e.sender = "ASIM";
            throw e;
        } else
            this.init = _init;

        if(_policy == undefined || _policy == null) {
            var e = new Error("Erroneous ASIM specification. No scheduling policy specified.");
            e.sender = "ASIM";
            throw e;
        } else
            this.policy = _policy;

        this.brapper = null;
        this.status = ASIMState.IDLE;
    };

    cls.prototype = {
        getName : function() {
            return this.name;
        }, 

        setBrapper : function(id) {
            this.brapper = id;
        },

        setError : function(e) {
            this.error = e;
            this.status = ASIMState.ERROR;
        }
    };

    return cls;
})();

module.exports = ASIM;
