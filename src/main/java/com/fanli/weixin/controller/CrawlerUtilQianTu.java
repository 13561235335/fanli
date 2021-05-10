package com.fanli.weixin.controller;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CrawlerUtilQianTu {

    public static void main(String[] a){
        try {

            URL url=new URL("https://m.tb.cn/h.4rHil7S?sm=d3de56");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();// 打开连接


            connection.connect();// 连接会话
            BufferedReader reader=new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));

            String line;
            while((line=reader.readLine())!=null){
                System.out.println(line);
            }
            reader.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
