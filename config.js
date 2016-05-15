var Config = {
    httpServer : {
        port : 9090,
        host : 'localhost',
        path : '/'
    },

    updater : {
        port : 9091,
        host : 'localhost',
        path : '../coreASM/org.coreasm.biomics.wrapper/target/brapper.jar'
    },

    scheduler : {
        port : 9091,
        host : 'localhost'
    }
};

module.exports = Config;
