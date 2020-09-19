package com.sample.agent;

import javassist.*;
import org.apache.http.client.methods.HttpGet;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * @author aster
 */
public class SimpleClassTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        String classRefStr = "org/apache/http/impl/client/CloseableHttpClient";
        if (className.endsWith(classRefStr)) {
            ClassPool classPool = ClassPool.getDefault();
            CtClass clazz = null;
            try {
                clazz = classPool.get("org.apache.http.impl.client.CloseableHttpClient");

                CtConstructor[] cs = clazz.getConstructors();
                for (CtConstructor constructor : cs) {
                    // 在构造函数结束的位置插入如下的内容
                    constructor.insertAfter("System.out.println(\"HttpClient-agent-insert-code\");");
                }
                byte[] byteCode = clazz.toBytecode();
                clazz.detach();
                return byteCode;
            } catch (NotFoundException | CannotCompileException | IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
