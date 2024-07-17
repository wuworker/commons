package com.wxl.commons.util;

import org.dom4j.Element;

import java.util.Map;

/**
 * Create by wuxingle on 2024/07/17
 * json xml转换工具
 */
public class JsonXmlConverters {

    /**
     * json转xml
     */
    public static String jsonToXmlString(String jsonStr) {
        JsonXmlConverter converter = new JsonXmlConverter();
        converter.setCreateRootAuto(false);
        return converter.jsonToXmlString(jsonStr);
    }

    public static String jsonToXmlString(Object json) {
        JsonXmlConverter converter = new JsonXmlConverter();
        converter.setCreateRootAuto(false);
        return converter.jsonToXmlString(json);
    }

    public static String jsonToXmlString(String jsonStr, String defaultRoot) {
        JsonXmlConverter converter = new JsonXmlConverter();
        converter.setCreateRootAuto(true);
        converter.setDefaultRoot(defaultRoot);
        return converter.jsonToXmlString(jsonStr);
    }

    public static String jsonToXmlString(Object json, String defaultRoot) {
        JsonXmlConverter converter = new JsonXmlConverter();
        converter.setCreateRootAuto(true);
        converter.setDefaultRoot(defaultRoot);
        return converter.jsonToXmlString(json);
    }

    public static Element jsonToXml(String jsonStr, String defaultRoot) {
        JsonXmlConverter converter = new JsonXmlConverter();
        converter.setDefaultRoot(defaultRoot);
        return converter.jsonToXml(jsonStr);
    }

    public static Element jsonToXml(Object json, String defaultRoot) {
        JsonXmlConverter converter = new JsonXmlConverter();
        converter.setDefaultRoot(defaultRoot);
        return converter.jsonToXml(json);
    }

    /**
     * xml转json
     *
     * @see JsonXmlConverter
     */

    public static String xmlToJsonString(String xml) {
        JsonXmlConverter converter = new JsonXmlConverter();
        return converter.xmlToJsonString(xml);
    }

    public static String xmlToJsonString(Element xml) {
        JsonXmlConverter converter = new JsonXmlConverter();
        return converter.xmlToJsonString(xml);
    }

    public static String xmlToJsonString(String xml, boolean containAttr) {
        JsonXmlConverter converter = new JsonXmlConverter();
        converter.setContainAttr(containAttr);
        return converter.xmlToJsonString(xml);
    }

    public static String xmlToJsonString(Element xml, boolean containAttr) {
        JsonXmlConverter converter = new JsonXmlConverter();
        converter.setContainAttr(containAttr);
        return converter.xmlToJsonString(xml);
    }

    public static Map<String, Object> xmlToJson(String xml) {
        JsonXmlConverter converter = new JsonXmlConverter();
        return converter.xmlToJson(xml);
    }

    public static Map<String, Object> xmlToJson(Element xml) {
        JsonXmlConverter converter = new JsonXmlConverter();
        return converter.xmlToJson(xml);
    }

    public static Map<String, Object> xmlToJson(String xml, boolean containAttr) {
        JsonXmlConverter converter = new JsonXmlConverter();
        converter.setContainAttr(containAttr);
        return converter.xmlToJson(xml);
    }

    public static Map<String, Object> xmlToJson(Element xml, boolean containAttr) {
        JsonXmlConverter converter = new JsonXmlConverter();
        converter.setContainAttr(containAttr);
        return converter.xmlToJson(xml);
    }
}
