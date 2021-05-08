package com.fanli.weixin.utils;

import com.fanli.weixin.domain.GetMsg;
import com.thoughtworks.xstream.XStream;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessgaeUtils {

        /*将xml格式转化为map*/
        public static Map<String,String> xmlToMap(HttpServletRequest request) throws Exception{
            Map<String,String> map=new HashMap<>();

            SAXReader reader = new SAXReader();
            InputStream inputStream = request.getInputStream();
            System.out.println("--------> " + request);
            Document doc = reader.read(inputStream);
            Element root = doc.getRootElement();//得到根节点
            List<Element> list=root.elements();//根节点下的所有的节点
            for(Element e:list){
                map.put(e.getName(),e.getText());
            }

            inputStream.close();
            return  map;
        }
    public static String objectToXml(GetMsg message){
        XStream xStream=new XStream();
        xStream.alias("xml", message.getClass());
        return xStream.toXML(message);
    }
    }
