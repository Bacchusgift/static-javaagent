package com.sample.agent.startup;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class StartupProfilerAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        Logger logger = Logger.getLogger("start");
        try {
            FileHandler fh = new FileHandler("~/logs/log.data");
            fh.setFormatter(new Formatter() {
                @Override
                public String format(LogRecord record) {
                    SimpleDateFormat logTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    return String.format("%s [] %s - %s\n",
                        logTime.format(new Date()), record.getLevel(), record.getMessage()
                    );
                }
            });
            logger.addHandler(fh);
            logger.warning("logger init.");
        } catch (Exception e) {
            e.printStackTrace();
        }
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
