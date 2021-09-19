package com.geekbang.jvm;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

public class XClassLoader extends ClassLoader{
    public static void main(String[] args) throws Exception{
        ClassLoader classLoader = new XClassLoader();
        Class<?> aClass = classLoader.loadClass("Hello");
        for (Method m : aClass.getDeclaredMethods()) {
            System.out.println(aClass.getSimpleName() + "." + m.getName());
        }
        Object instance = aClass.getDeclaredConstructor().newInstance();
        Method method = aClass.getMethod("hello");
        method.invoke(instance);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String resourcePath = name.replace(".","/");
        final String suffix = ".xlass";
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(resourcePath+suffix);
        try {
            int length = inputStream.available();
            byte[] byteArray = new byte[length];
            inputStream.read(byteArray);
            //转换
            byte[] classBytes = decode(byteArray);
            //通知底层定义这个类
            return defineClass(name,classBytes,0,classBytes.length);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ClassNotFoundException();
        }finally {
            close(inputStream);
        }
    }

    //解码
    private static byte[] decode(byte[] byteArray){
        byte[] targetArray = new byte[byteArray.length];
        for (int i = 0; i < byteArray.length; i++) {
            targetArray[i] = (byte) (255 - byteArray[i]);
        }
        return targetArray;
    }

    private static void close(Closeable res){
        if(null != res){
            try {
                res.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
