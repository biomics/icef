package org.coreasm.biomics;

import org.coreasm.engine.CoreASMError;

public class ASIMControlResponse {
    public String name;
    public String simulation;
    public boolean success;
    public String error;

    public ASIMControlResponse(String n, String s, String e) {
        name = n;
        simulation = s;
        success = e == null;
        error = e;
    }
}
