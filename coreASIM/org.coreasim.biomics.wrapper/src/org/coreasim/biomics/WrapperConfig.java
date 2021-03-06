/*
 * WrapperConfig.java v1.0
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

import org.kohsuke.args4j.Option;

public class WrapperConfig {
    @Option(name="--help", usage="Shows this usage information")
    public Boolean showUsage = false;
    
    @Option(name="-h", metaVar="<host>", usage="Provide the IP or the hostname to which the wrapper should be bound.")
    public String host = "localhost";
    
    @Option(name="-p", metaVar="<port>", usage="Sets the port to which the wrapper should listen to.")
    public int port = 8080;

    @Option(name="-m", forbids={ "-r", "-rp" }, metaVar="<host>", usage="Sets the IP or the hostname which the wrapper should use as a wrapper manager.")
    public String managerHost = null;

    @Option(name="-mp", forbids={ "-r", "-rp" }, depends={"-m"}, metaVar="<port>", usage="Sets the port on which the wrapper manager listens. \nThis option is only valid in combination with -m.")
    public int managerPort = 8080;

    @Option(name="-r", forbids={ "-m", "-mp" }, metaVar="<host>", usage="Wrapper only mode operates without manager, i.e. all messages are sent to the remote wrapper specified in this option.")
   public String remoteHost = null;

    @Option(name="-rp", depends={"-r"}, forbids={ "-m", "-mp" }, metaVar="<port>", usage="Specifies the port of the remote brapper.")
    public int remotePort = 8080;

    @Option(name="-u", forbids={ "-r", "-rp" }, usage="Enables update accumulation inside the brapper.")
    public boolean accUpdatesMode = false;

    @Option(name="-s", depends={ "-u" }, forbids={ "-r", "-rp" }, usage="Allows the execution of several scheduling ASIMs.")
    public boolean schedulingMode = false;

    @Option(name="-c", depends={ "-u" }, forbids={ "-r", "-rp" }, usage="Allows the execution of channel ASIMs.")
    public boolean channelMode = false;

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getManager() {
        return managerHost;
    }

    public int getManagerPort() {
        return managerPort;
    }

    public String getRemoteWrapper() {
        return remoteHost;
    }

    public int getRemoteWrapperPort() {
        return remotePort;
    }
}
