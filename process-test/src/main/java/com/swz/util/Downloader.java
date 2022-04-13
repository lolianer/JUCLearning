package com.swz.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author shen_wzhong
 * @create 2022-04-13 8:45
 */
public class Downloader {
    public static List<String> download() {
        try {
            URLConnection conn = new URL("https://www.baidu.com/").openConnection();
            List<String> lines = new ArrayList<>();
            //try-with-resources机制
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            }
            return lines;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
