package org.coreasm.biomics;

import java.util.HashMap;

public class EngineManager {

    private static final HashMap<String, CoreASMContainer> engines = 
        new HashMap<>();

    public static void createEngine(AgentCreationRequest req) {
        System.out.println("Create a new engine");
        System.out.println("AgentName: "+req.name);
        System.out.println("Program: "+req.program);

        CoreASMContainer casm = new CoreASMContainer(req.name, req.program);
        engines.put(req.name, casm);
    }

    public static boolean startEngine(String name) {
        System.out.println("Create an existing engine");
        System.out.println("AgentName: "+name);

        if(engines.containsKey(name)) {
            CoreASMContainer casm = engines.get(name);
            casm.exec();
            return true;
        } else {
            return false;
        }
    }

    public static boolean receiveMsg(MessageRequest req) {
        System.out.println("Engine Manager receives message");
        System.out.println("Receiver: "+req.receiver);
        System.out.println("Sender: "+req.sender);
        System.out.println("Body: "+req.body);

        return true;
    }
    
    public static void stopAll() {
        
    }

    public static void reset() {
        engines.clear();
    }
}
