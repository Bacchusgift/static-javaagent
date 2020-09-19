package com.sample.agent;

import java.lang.instrument.Instrumentation;

/**
 * @author aster
 * 启动agent
 */
public class AgentDemo {
    private static Instrumentation instrumentation;

    /**
     * -javaagent
     * */
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("premain invoke:");
        System.out.println(agentArgs);

        instrumentation = inst;
        SimpleClassTransformer transformer = new SimpleClassTransformer();
        inst.addTransformer(transformer);
    }

    public static void premain(String agentArgs) {
        System.out.println("premain invoke without inst:");
        System.out.println(agentArgs);
    }
}
