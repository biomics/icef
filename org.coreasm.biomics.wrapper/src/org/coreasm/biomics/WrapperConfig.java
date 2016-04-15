package org.coreasm.biomics;

import org.kohsuke.args4j.Option;

public class WrapperConfig {
    @Option(name="-h", metaVar="<host>", usage="Provide the IP or the hostname to which the wrapper should be bound (default localhost).")
    public String host = "localhost";
    
    @Option(name="-p", metaVar="<port>", usage="Sets the port to which the wrapper should listen to (default 8080).")
    public int port = 8080;

    @Option(name="-m", forbids={ "-r", "-rp" }, metaVar="<host>", usage="Sets the IP or the hostname which the wrapper should use as a wrapper manager.")
    public String managerHost = null;

    @Option(name="-mp", forbids={ "-r", "-rp" }, depends={"-m"}, metaVar="<port>", usage="Sets the port on which the wrapper manager listens (default 8080).\nThis option is only valid in combination with -m.")
    public int managerPort = 8080;

    @Option(name="-r", forbids={ "-m", "-mp" }, metaVar="<host>", usage="Wrapper only mode operates without manager, i.e. all messages are sent to the remote wrapper specified in this option.")
   public String remoteHost = null;

    @Option(name="-rp", depends={"-r"}, forbids={ "-m", "-mp" }, metaVar="<port>", usage="Specifies the port of the remote wrapper.")
    public int remotePort = 8080;

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
