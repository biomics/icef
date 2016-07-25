/*
 * ASIMCreationResponse.java v1.0
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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;

import org.coreasm.engine.CoreASMError;

public class ASIMCreationResponse {

    public String msg;
    public ASIMDetails asim;
    
    String getMsg() {
    	return msg;
    }
    
    void setMsg(String m) {
    	msg = m;
    }
    
    ASIMDetails getAsim() {
    	return asim;
    }
    
    void setAsim(ASIMDetails a) {
    	asim = a;
    }

    public ASIMCreationResponse() {
	asim = new ASIMDetails();
    }
    
    public class ASIMDetails {
    
    	public String name;
    	public String simulation;
    	public String status;
    	public boolean success;
    	public String error;
	public String brapper;
	
    	String getName() {
    		return name;
    	}
    	
    	void setName(String n) {
    		name = n;
    	}
    	
    	String getSimulation() {
    		return simulation;
    	}
    	
    	void setSimulation(String s) {
    		simulation = s;
    	}
    	
    	String getStatus() {
    		return status;
    	}
    	
    	void setStatus(String s) {
    		status = s;
    	}
    	
    	boolean getSuccess() {
    		return success;
    	}
    	
    	void setSuccess(boolean s) {
    		success = s;
    	}
    	
    	String getError() {
    		return error;
    	}
    	
    	void setError(String e) {
    		error = e;
    	}

	String getBrapper() {
	    return brapper;
	}

	void setBrapper(String b) {
	    brapper = b;
	}
    };
}
