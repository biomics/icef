var Agent = (function() {
    var cls = function(_name, _program, _wrapper) {
        this.name = _name;
        this.program = _program;
        this.wrapper = _wrapper;
    };

    return cls;
})();

module.exports = Agent;
