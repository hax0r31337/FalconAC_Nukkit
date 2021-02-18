package me.liuli.falcon.utils;

import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import com.google.gson.GsonBuilder;
import me.liuli.falcon.FalconAC;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class OtherUtil {
    public static String y2j(File file) {
        Config yamlConfig = new Config(file, Config.YAML);
        ConfigSection section = yamlConfig.getRootSection();
        return new GsonBuilder().create().toJson(section);
    }

    public static void writeFile(String path, String text) {
        try {
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8));
            writer.write(text);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getTextFromInputStream(InputStream inputStream) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, length);
        }

        byteArrayOutputStream.close();
        inputStream.close();

        return byteArrayOutputStream.toString("UTF-8");
    }

    public static String readFile(File file) {
        try {
            return new String(Files.readAllBytes(file.toPath()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String t2s(long time) {
        Long timeCache = time;
        int D, H, M;
        D = (int) Math.floor(timeCache / 86400);
        timeCache -= D * 86400L;
        H = (int) Math.floor(timeCache / 3600);
        timeCache -= H * 3600L;
        M = (int) Math.floor(timeCache / 60);
        timeCache -= M * 60L;
        StringBuilder result = new StringBuilder();
        if (D != 0) {
            result.append(D);
            result.append("d ");
        }
        if (H != 0) {
            result.append(H);
            result.append("h ");
        }
        if (M != 0) {
            result.append(M);
            result.append("m ");
        }
        result.append(timeCache);
        result.append("s");
        return result.toString();
    }

    public static long getTime() {
        return (System.currentTimeMillis() / 1000);
    }

    public static String getTextFromResource(String resourceName) {
        InputStream inputStream = FalconAC.class.getClassLoader().getResourceAsStream(resourceName);
        try {
            return getTextFromInputStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static float angle(Vector3 from, Vector3 other) {
        double dot = from.dot(other) / (from.length() * other.length());
        return (float) Math.acos(dot);
    }

    public static void downloadFile(String urlStr, String filePath, String fileName) throws IOException {
        FalconAC.plugin.getLogger().info("DOWNLOADING " + fileName + " FROM URL: " + urlStr);

        long startTime = System.currentTimeMillis();
        File jar = new File(filePath, fileName);
        if (jar.exists()) {
            return;
        }
        File tmp = new File(jar.getPath() + ".tmp");
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(3 * 1000);
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.83 Safari/537.36");
        InputStream is = conn.getInputStream();
        int totalSize = conn.getContentLength(), nowSize = 0, lastSize = -1;
        FileOutputStream os = new FileOutputStream(tmp);
        byte[] buf = new byte[4096];
        int size = 0;
        while ((size = is.read(buf)) != -1) {
            os.write(buf, 0, size);
            nowSize += size;
            int progcess = 100 * nowSize / totalSize;
            if (progcess % 5 == 0 && progcess != lastSize) {
                FalconAC.plugin.getLogger().info("DOWNLOADING " + fileName + " PROCESS:" + (100 * nowSize / totalSize) + "%");
                lastSize = progcess;
            }
        }
        is.close();
        os.flush();
        os.close();
        if (jar.exists())
            jar.delete();
        tmp.renameTo(jar);
        FalconAC.plugin.getLogger().info("DOWNLOAD " + fileName + " COMPLETE(" + ((System.currentTimeMillis() - startTime) / 1000) + "s)");
    }
}
