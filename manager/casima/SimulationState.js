/*      
 * SimulationState.js v1.0
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


var SimulationState = {
    EMPTY: "empty",
    IDLE : "idle",
    LOADED : "loaded",
    INIT : "initialized",
    RUNNING : "running",
    PAUSED : "paused",
    STOPED : "stopped",
    ERROR : "error"
}

module.exports = SimulationState;
