package me.liuli.falcon.utils;

import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class OtherUtils {
    public static String y2j(File file){
        Config yamlConfig = new Config(file,Config.YAML);
        ConfigSection section = yamlConfig.getRootSection();
        return new GsonBuilder().create().toJson(section);
    }
    public static void injectClass(File file) {
        try {
            URLClassLoader autoload = (URLClassLoader) ClassLoader.getSystemClassLoader();
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(autoload, file.toURI().toURL());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void writeFile(String path,String text) {
        try {
            Writer writer=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8));
            writer.write(text);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String readFile(String fileName) {
        File file = new File(fileName);
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
            return new String(filecontent, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String t2s(long time){
        Long timeCache=time;
        int D,H,M;
        D=(int)Math.floor(timeCache/86400);
        timeCache-=D*86400L;
        H=(int)Math.floor(timeCache/3600);
        timeCache-=H*3600L;
        M=(int)Math.floor(timeCache/60);
        timeCache-=M*60L;
        StringBuilder result=new StringBuilder();
        if(D!=0){
            result.append(D);
            result.append("d ");
        }
        if(H!=0){
            result.append(H);
            result.append("h ");
        }
        if(M!=0){
            result.append(M);
            result.append("m ");
        }
        result.append(timeCache);
        result.append("s");
        return result.toString();
    }
    public static long getTime(){
        return (new Date().getTime()/1000);
    }
    public static void readJar(String fileName,String JarDir,String path){
        try {
            JarFile jarFile = new JarFile(JarDir);
            JarEntry entry = jarFile.getJarEntry(fileName);
            InputStream input = jarFile.getInputStream(entry);
            java.nio.file.Files.copy(input, new File(path).toPath(), StandardCopyOption.REPLACE_EXISTING);
            input.close();
            jarFile.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
