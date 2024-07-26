package com.wxl.commons.util;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Created by wuxingle on 2018/1/13.
 * dom4jTest
 */
public class Dom4jUtilsTest {

    @Test
    public void test() throws Exception {
        String xml = "<Envelope><Body><sayHi name=\"wxl\">hi!  </sayHi></Body></Envelope>";
        System.out.println(Dom4jUtils.toPrettyFormat(xml));

        Document document = DocumentHelper.parseText(xml);
        Element envelope = document.getRootElement();
        Element body = envelope.element("Body");
        Element sayHi = body.element("sayHi");

        System.out.println(body.getText() + "|");
        System.out.println(body.getTextTrim() + "|");
        System.out.println(body.getStringValue() + "|");

        System.out.println("------------------------------------");

        System.out.println(sayHi.getText() + "|");
        System.out.println(sayHi.getTextTrim() + "|");
        System.out.println(sayHi.getStringValue() + "|");
    }

    @Test
    public void test2() throws Exception {
        String xml = "<a><b>nice</b><c>nice</c></a>";
        System.out.println(Dom4jUtils.toPrettyFormat(xml));
    }

    @Test
    public void getSafeChildElement() throws Exception {
        String xml = "<Envelope><Body><sayHi name=\"wxl\">hi!</sayHi><sayHi name=\"wxl\">hi2!</sayHi></Body></Envelope>";
        System.out.println(Dom4jUtils.toPrettyFormat(xml));

        Document document = DocumentHelper.parseText(xml);
        Element envelope = document.getRootElement();
        Element sayHi = Dom4jUtils.getRequireChildElement(envelope, "Body", "sayHi");
        System.out.println(sayHi.getText());

        try {
            Element notExist = Dom4jUtils.getRequireChildElement(envelope, "IBody", "sayHi");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getUnSafeChildElement() throws Exception {
        String xml = "<Envelope><Body><sayHi name=\"wxl\">hi!</sayHi></Body></Envelope>";
        System.out.println(Dom4jUtils.toPrettyFormat(xml));

        Document document = DocumentHelper.parseText(xml);
        Element envelope = document.getRootElement();
        Element sayHi = Dom4jUtils.getChildElement(envelope, "Body", "sayHi");
        System.out.println(sayHi.getText());


        Element notExist = Dom4jUtils.getChildElement(envelope, "IBody", "sayHi");
        System.out.println(notExist == null);
    }

    @Test
    public void getSafeAttributeValue() throws Exception {
        String xml = "<Envelope><Body><sayHi name=\"wxl\">hi!</sayHi></Body></Envelope>";
        System.out.println(Dom4jUtils.toPrettyFormat(xml));

        Document document = DocumentHelper.parseText(xml);
        Element envelope = document.getRootElement();
        Element sayHi = Dom4jUtils.getRequireChildElement(envelope, "Body", "sayHi");
        String v = Dom4jUtils.getRequireAttributeValue(sayHi, "name");
        System.out.println(v);

        try {
            Dom4jUtils.getRequireAttributeValue(sayHi, "age");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getUnSafeAttributeValue() throws Exception {
        String xml = "<Envelope><Body><sayHi name=\"wxl\">hi!</sayHi></Body></Envelope>";
        System.out.println(Dom4jUtils.toPrettyFormat(xml));

        Document document = DocumentHelper.parseText(xml);
        Element envelope = document.getRootElement();
        Element sayHi = Dom4jUtils.getRequireChildElement(envelope, "Body", "sayHi");
        String v = Dom4jUtils.getAttributeValue(sayHi, "name");
        System.out.println(v);

        v = Dom4jUtils.getAttributeValue(sayHi, "age");
        System.out.println(v == null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void findSafeEleByAttrEquals() throws Exception {
        String xml = "<Envelope><Body><sayHi name=\"wxl1\">hi!</sayHi><sayHi name=\"wxl2\">hi2!</sayHi></Body></Envelope>";
        System.out.println(Dom4jUtils.toPrettyFormat(xml));

        Document document = DocumentHelper.parseText(xml);
        Element envelope = document.getRootElement();
        Element body = Dom4jUtils.getRequireChildElement(envelope, "Body");
        Element sayHi = Dom4jUtils.findRequireEleByAttrEquals(body.elements("sayHi"), "name", "wxl2");
        System.out.println(sayHi.getText());

        try {
            Dom4jUtils.findRequireEleByAttrEquals(body.elements("sayHi"), "name", "wxl3");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void findUnSafeEleByAttrEquals() throws Exception {
        String xml = "<Envelope><Body><sayHi name=\"wxl1\">hi!</sayHi><sayHi name=\"wxl2\">hi2!</sayHi></Body></Envelope>";
        System.out.println(Dom4jUtils.toPrettyFormat(xml));

        Document document = DocumentHelper.parseText(xml);
        Element envelope = document.getRootElement();
        Element body = Dom4jUtils.getRequireChildElement(envelope, "Body");
        Element sayHi = Dom4jUtils.findFirstEleByAttrEquals(body.elements("sayHi"), "name", "wxl2");
        System.out.println(sayHi.getText());

        sayHi = Dom4jUtils.findFirstEleByAttrEquals(body.elements("sayHi"), "name", "wxl3");
        System.out.println(sayHi == null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void findUnSafeElesByAttrEquals() throws Exception {
        String xml = "<Envelope><Body>" +
                "<sayHi name=\"wxl1\">hi!</sayHi>" +
                "<sayHi name=\"wxl2\">hi2!</sayHi>" +
                "<sayHi name=\"wxl2\">hi2!</sayHi>" +
                "</Body></Envelope>";
        System.out.println(Dom4jUtils.toPrettyFormat(xml));

        Document document = DocumentHelper.parseText(xml);
        Element envelope = document.getRootElement();
        Element body = Dom4jUtils.getRequireChildElement(envelope, "Body");
        List<Element> sayHis = Dom4jUtils.findEleByAttrEquals(body.elements("sayHi"), "name", "wxl2");
        System.out.println(sayHis.size());

        sayHis = Dom4jUtils.findEleByAttrEquals(body.elements("sayHi"), "name", "wxl3");
        System.out.println(sayHis.size());
    }

}















