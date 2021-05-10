package com.fanli.weixin.utils;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestAndResponseTool {


    public static Page  sendRequstAndGetResponse(String url) {
        Page page = null;
        // 1.生成 HttpClinet 对象并设置参数
        HttpClient httpClient = new HttpClient();
        // 设置 HTTP 连接超时 5s
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
        // 2.生成 GetMethod 对象并设置参数
        GetMethod getMethod = new GetMethod(url);
        //zz设置请求头
        getMethod.setRequestHeader("user-agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
        //zz设置编码格式Content-Encoding →gzip
        //getMethod.setRequestHeader("Content-Encoding","GBKs");
        // 设置 get 请求超时 5s
        getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 5000);
        // 设置请求重试处理
        getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
        // 3.执行 HTTP GET 请求
        try {
            int statusCode = httpClient.executeMethod(getMethod);
        // 判断访问的状态码
            if (statusCode != HttpStatus.SC_OK) {
                System.err.println("Method failed: " + getMethod.getStatusLine());
            }
        // 4.处理 HTTP 响应内容
            byte[] responseBody = getMethod.getResponseBody();// 读取为字节 数组
            String contentType = getMethod.getResponseHeader("Content-Type").getValue(); // 得到当前返回类型
            page = new Page(responseBody,url,contentType); //封装成为页面
        } catch (HttpException e) {
        // 发生致命的异常，可能是协议不对或者返回的内容有问题
            System.out.println("Please check your provided http address!");
            e.printStackTrace();
        } catch (IOException e) {
        // 发生网络异常
            e.printStackTrace();
        } finally {
        // 释放连接
            getMethod.releaseConnection();
        }
        return page;
    }

    public static void main(String[] args) throws Exception {
        Page page = RequestAndResponseTool.sendRequstAndGetResponse("https://m.tb.cn/h.4r58bZT?sm=881eca");
        Document doc = Jsoup.parse(page.getHtml());
        Element script = doc.getElementsByTag("script").get(1);
        System.out.println(script);
        System.out.println(script.getElementsByTag("url"));
        /*用來封裝要保存的参数*/
        Map<String, Object> map = new HashMap<String, Object>();


            /*取得JS变量数组*/
            String[] data = script.data().toString().split("var");

            /*取得单个JS变量*/
            for(String variable : data){

                /*过滤variable为空的数据*/
                if(variable.contains("=")){

                    /*取到满足条件的JS变量*/
                    if(variable.contains("url")){

                        String[]  kvp = variable.split("=");

                        /*取得JS变量存入map*/
                        if(!map.containsKey(kvp[0].trim()))
                            map.put(kvp[0].trim(), findUrlByStr(variable));
                    }
                }
            }
        System.out.println(map.get("url"));
    }

    /*设置网页抓取响应时间*/
    private static final int TIMEOUT = 10000;

    public static Map<String, Object> getSerieExtDetail(int serieId) throws Exception{

        /*车系参数配置页面*/
        String serieInfo = "https://m.tb.cn/h.4r58bZT?sm=881eca";

        /*用來封裝要保存的参数*/
        Map<String, Object> map = new HashMap<String, Object>();

        /*取得车系参数配置页面文档*/
        Document document = Jsoup.connect(serieInfo).timeout(TIMEOUT).get();

        /*取得script下面的JS变量*/
        Elements e = document.getElementsByTag("script").eq(6);

        /*循环遍历script下面的JS变量*/
        for (Element element : e) {

            /*取得JS变量数组*/
            String[] data = element.data().toString().split("var");

            /*取得单个JS变量*/
            for(String variable : data){

                /*过滤variable为空的数据*/
                if(variable.contains("=")){

                    /*取到满足条件的JS变量*/
                    if(variable.contains("option") || variable.contains("config")
                            || variable.contains("color") || variable.contains("innerColor")){

                        String[]  kvp = variable.split("=");

                        /*取得JS变量存入map*/
                        if(!map.containsKey(kvp[0].trim()))
                            map.put(kvp[0].trim(), kvp[1].trim().substring(0, kvp[1].trim().length()-1).toString());
                    }
                }
            }
        }
        return map;
    }

    public static String findUrlByStr(String data){
        Pattern pattern = Pattern.compile("https?://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]");
        Matcher matcher = pattern.matcher(data);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }
}
