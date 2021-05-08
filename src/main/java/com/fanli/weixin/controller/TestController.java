package com.fanli.weixin.controller;

import com.fanli.weixin.domain.GetMsg;
import com.fanli.weixin.utils.MessgaeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;

@RestController
public class TestController {

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


            message.setFromUserName(ToUserName);
            message.setToUserName(FromUserName);
            message.setContent("您发送的消息是text文本消息" + FromUserName + "很帅");
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
}
