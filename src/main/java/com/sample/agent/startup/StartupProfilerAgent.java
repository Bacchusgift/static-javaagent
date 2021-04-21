package com.sample.agent.startup;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class StartupProfilerAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        inst.addTransformer(new DefaultTransformer(), true);
    }

    public static class DefaultTransformer implements ClassFileTransformer {
        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

            try {
                if (className != null && className.endsWith("ApplicationContextAwreProcessor")) {
                    CtClass ctClass = ClassPool.getDefault().makeClass(new ByteArrayInputStream(classfileBuffer));

                    CtMethod beforeMethod = ctClass.getDeclaredMethod("postProcessBeforeInitialization");
                    beforeMethod.insertBefore("com.sample.agent.startup.SpringBeanInitializationProfiler.beforeInitialize(this.applicationContext, beanName);");

                    CtMethod afterMethod = ctClass.getDeclaredMethod("postProcessAfterInitialization");
                    afterMethod.insertAfter("com.sample.agent.startup.SpringBeanInitializationProfiler.afterInitialize(this.applicationContext, beanName);");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return classfileBuffer;
        }
    }
}
