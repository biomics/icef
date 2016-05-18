package org.coreasm.biomics;

import org.coreasm.engine.CoreASMError;

public class ASIMCreationResponse {
    public String name;
    public String simulation;
    public boolean success;
    public String error;

    public ASIMCreationResponse(String n, String s, CoreASMError e) {
        name = n;
        simulation = s;
        success = e == null;
        if(e == null)
            error = "";
        else
            error = e.toString();
    }
}
