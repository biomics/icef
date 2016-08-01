/*      
 * config.js v1.0
 *
 * This file contains source code developed by the European 
 * FP7 research project BIOMICS (Grant no. 318202)
 * Copyright (C) 2016 Daniel Schreckling
 *
 * Licensed under the Academic Free License version 3.0
 *   http://www.opensource.org/licenses/afl-3.0.php
 *
 *
 */

var Config = {
    socket : {
        port : 3000
    },
    httpServer : {
        port : 9090,
        host : 'localhost',
        path : '/'
    },

    scheduler : {
        port : 9091,
        host : 'localhost',
        jar : '../coreASM/org.coreasm.biomics.wrapper/target/brapper.jar'
    }
};

module.exports = Config;
