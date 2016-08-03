/*
 * Wrapper.java v1.0
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

package org.coreasim.biomics;

import java.util.Collection;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http.KeepAlive;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.CmdLineException;
import java.io.IOException;
import java.net.URI;
import java.net.BindException;

public class Wrapper {
    protected WrapperConfig config = null;
    protected String commUrl = null;
    private HttpServer server = null;

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public void startServer() {
        // TODO: Disable logging

        // search for resources and components in org.coreasim.biomics
        // final ResourceConfig rc = new ResourceConfig().packages("org.coreasim.biomics.wrapper");
        ResourceConfig rc = new ResourceConfig().packages("org.coreasim.biomics");
        

        // create and start a new instance of grizzly http server
        server = GrizzlyHttpServerFactory.createHttpServer(URI.create("http://"+config.getHost()+":"+config.getPort()+"/"), rc, false);

        Collection<NetworkListener> listeners = server.getListeners();
        for(NetworkListener l : listeners) {
            KeepAlive ka = l.getKeepAlive();
            l.getKeepAlive().setMaxRequestsCount(0);
        }

	try {
	    server.start();
	    System.out.println("Brapper started successfully at http://"+config.getHost()+":"+config.getPort()+"/");
	} catch (BindException be) {
	    System.err.println("Unable to bind to port "+config.getPort()+"!");
	} catch (IOException ioe) {
	    System.err.println("IO Exception on brapper startup.");
	}
    }

    public void stopServer() {
        server.stop();
    }

    public Wrapper(String[] args) {
        config = new WrapperConfig();
        CmdLineParser parser = new CmdLineParser(config);
        try {
            parser.parseArgument(args);
        } catch(CmdLineException e) {
            System.err.println("Error in command line arguments");
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            System.exit(1);
        }
        
        // wrapper to wrapper communicaiton
        if(config.remoteHost != null) {
            commUrl = "http://" + config.remoteHost + ":" + config.remotePort;
            System.out.println("Wrapper is in W2W mode!");
        }

        // wrapper to manager communication
        if(config.managerHost != null) {
            commUrl = "http://" + config.managerHost + ":" + config.managerPort;
            System.out.println("Wrapper is in W2M mode!");
        }

        // TODO: try to send messages to yourself
        if(commUrl == null) {
            commUrl = "http://" + config.host + ":" + config.port;
            System.out.println("Wrapper is in local mode!");
        }
        
        EngineManager.reset(this);
    }

    public String getCommUrl() {
        return commUrl;
    }

    public WrapperConfig getConfig() {
        return config;
    }

    /**
     * Wrapper main method.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        Wrapper wrapper = new Wrapper(args);
        wrapper.startServer();
        System.in.read();
        wrapper.stopServer();
    }
}

