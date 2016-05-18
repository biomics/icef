var ASIMCreationError = (function() {
    var cls = function(message) {
        this.message = (message || "");
    };

    cls.prototype = new Error();

    return cls;
})();

module.exports = ASIMCreationError;
