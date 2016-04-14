package org.coreasm.biomics;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

public class Wrapper {
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8080/";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in org.coreasm.biomics package
        final ResourceConfig rc = new ResourceConfig().packages("org.coreasm.biomics");

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    /**
     * Wrapper main method.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        
        EngineManager.reset();

        /*        CoreASMContainer casm1 = new CoreASMContainer("NEWAGENT", "CoreASM test\nuse Standard\ninit P\nrule P = { print \"Hello\" }");
        CoreASMContainer casm2 = new CoreASMContainer("NEWAGENT", "CoreASM test\nuse Standard\ninit P\nrule P = { print \"Hello2\" }");
        CoreASMContainer casm3 = new CoreASMContainer("NEWAGENT", "CoreASM test\nuse Standard\ninit P\nrule P = { print \"Hello3\" }");

        CoreASMContainer casm4 = new CoreASMContainer("NEWAGENT", "CoreASM test\nuse Standard\ninit P\nrule P = { print \"Hello3\" }");

        CoreASMContainer casm5 = new CoreASMContainer("NEWAGENT", "CoreASM test\nuse Standard\ninit P\nrule P = { print \"Hello3\" }");

        CoreASMContainer casm6 = new CoreASMContainer("NEWAGENT", "CoreASM test\nuse Standard\ninit P\nrule P = { print \"Hello3\" }");


        casm1.exec();
        casm2.exec();
        casm3.exec();
        casm4.exec();
        casm5.exec();
        casm6.exec();*/

        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
        System.in.read();
        server.stop();

        /* 
           casm1.destroy();
           casm2.destroy();
           casm3.destroy();
           casm4.destroy();
           casm5.destroy();
           casm6.destroy(); */
    }
}

