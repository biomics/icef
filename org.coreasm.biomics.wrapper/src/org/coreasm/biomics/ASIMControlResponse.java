/*
 * ASIMControlResponse.java v1.0
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

package org.coreasm.biomics;

import org.coreasm.engine.CoreASMError;

public class ASIMControlResponse {
    public String name;
    public String simulation;
    public String state;
    public boolean success;
    public String error;

    public ASIMControlResponse(String n, String s, String st, String e) {
        name = n;
        simulation = s;
        state = st;
        success = e == null;
        error = e;
    }
}
