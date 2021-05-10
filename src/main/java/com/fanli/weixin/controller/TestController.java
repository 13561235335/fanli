package com.fanli.weixin.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fanli.weixin.domain.GetMsg;
import com.fanli.weixin.utils.MessgaeUtils;
import com.fanli.weixin.utils.Page;
import com.fanli.weixin.utils.RequestAndResponseTool;
import com.fanli.weixin.utils.UrlUtils;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.TbkDgMaterialOptionalRequest;
import com.taobao.api.response.TbkDgMaterialOptionalResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class TestController {

    /**
     * 微信认证接口
     * @param echostr
     * @return
     */
    @GetMapping("/hello")
    public String hello(@RequestParam("echostr") String echostr) {
        return echostr;
    }

    /**
     * 用户发送消息 接收回复接口
     * @param request
     * @param response
     * @throws Exception
     */
    @PostMapping("/hello")
    public void hello(HttpServletRequest request, HttpServletResponse response) throws Exception {

        response.setCharacterEncoding("utf-8");
        PrintWriter out = null;
        //将微信请求xml转为map格式，获取所需的参数


        Map<String, String> map = MessgaeUtils.xmlToMap(request);


        //从集合中，获取XML各个节点的内容

        String ToUserName = map.get("ToUserName");

        String FromUserName = map.get("FromUserName");

        String CreateTime = map.get("CreateTime");

        String MsgType = map.get("MsgType");

        String Content = map.get("Content");

        String MsgId = map.get("MsgId");
        GetMsg message = new GetMsg();
        //这里只处理文本消息
        if (MsgType.equalsIgnoreCase("text")) {

            TaobaoClient client = new DefaultTaobaoClient("http://gw.api.taobao.com/router/rest", "32618323", "9d86885c1e6442a2e664a83f97679336");
            TbkDgMaterialOptionalRequest req = new TbkDgMaterialOptionalRequest();

            req.setQ(Content);
            req.setAdzoneId(111297300240l);

            TbkDgMaterialOptionalResponse rsp = client.execute(req);
            message.setFromUserName(ToUserName);
            message.setToUserName(FromUserName);
            message.setContent(rsp.getBody());
            message.setMsgId(MsgId);
            message.setMsgType("text");
            message.setCreateTime(new Date().getTime());

        }
        try {
            out = response.getWriter();
            out.write(MessgaeUtils.objectToXml(message));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        out.close();

    }

    public static void main(String[] args)  throws Exception {

        // 从淘口令中取出url
        String urlByStr = findUrlByStr("1.0信bIOIX3DtfSy， https://m.tb.cn/h.4rHil7S?sm=d3de56  南极人袜子男短袜夏季透气防臭吸汗男士船袜春秋薄款低帮运动袜潮");
        // 判断url是否为空，为空就是标题
        if(!"".equals(urlByStr)){
            // 拆分出商品标题
            String[] split = "9.0，8COzXeTc1b1信 https://m.tb.cn/h.4r58bZT?sm=881eca  【3盒30片】LEADERS丽得姿氨基酸深层补水保湿韩国进口面膜女正品".split("  ");
            System.out.println("----------->商品标题" + split[1]);
            TaobaoClient client = new DefaultTaobaoClient("http://gw.api.taobao.com/router/rest", "32618323", "9d86885c1e6442a2e664a83f97679336");
            TbkDgMaterialOptionalRequest req = new TbkDgMaterialOptionalRequest();

            req.setQ(split[1]);
            req.setAdzoneId(111297300240l);
            TbkDgMaterialOptionalResponse rsp = client.execute(req);

            // 获取商品id
            String id = getId(urlByStr);

        }else {

        }

    }

    /**
     * 根据字符串取其中的url
     * @param data
     * @return
     */
    public static String findUrlByStr(String data){
        Pattern pattern = Pattern.compile("https?://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]");
        Matcher matcher = pattern.matcher(data);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }

    /**
     * 根据淘口令的中的url获取商品id
     * @param url
     * @return
     */
    public static String getId(String url){
        Page page = RequestAndResponseTool.sendRequstAndGetResponse(url);
        Document doc = Jsoup.parse(page.getHtml());
        // 获取script的对象 ----> 此处存在问题 script顺序改变则取不到对用的值
        Element script = doc.getElementsByTag("script").get(1);
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
                    // 获取变量中的url
                    String idUrl = findUrlByStr(variable);
                    // 通过url获取id
                    return UrlUtils.getParam(idUrl, "id");
                }
            }
        }
        return "";
    }

}
