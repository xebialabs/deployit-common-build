package com.xebialabs.commons.html;

import java.io.PrintWriter;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class HtmlWriterTest extends HtmlWriter {

    public HtmlWriterTest() {
        super(new PrintWriter(System.out));
    }
    
    @Test
    public void testDiv() {
        checkHtml(div(), "<div/>");
    }

    @Test
    public void testRow() {
        checkHtml(
                row(link("target", "Link text"), "First sentence.").cssClass("odd"), 
                "<tr class=\"odd\"><td><a href=\"target\">Link text</a></td><td>First sentence.</td></tr>");
    }
    
    @Test
    public void testBr() {
        checkHtml(
                hr().cssClass("break"), 
                "<hr class=\"break\"/>");
    }
    
    //
    // Helper methods
    //
    
    static void checkHtml(Element element, String expected) {
        assertEquals(expected, element.toString().replaceAll("\\n", ""));
        
    }
}
