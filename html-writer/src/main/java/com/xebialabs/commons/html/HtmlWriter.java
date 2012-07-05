package com.xebialabs.commons.html;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory class that allows DSL-style HTML composing in Java. Subclass this and
 * you can write stuff like.
 *
 * <pre>
 * {@code
 * h1("Table of Contents").cssClass("toc")
 * }
 * </pre>
 *
 * @author Hes Siemelink
 */
public class HtmlWriter {

    private final PrintWriter writer;

    public HtmlWriter(PrintWriter writer) {
        this.writer = writer;
    }

    public PrintWriter getWriter() {
        return writer;
    }

    public void write(String text) {
        writer.print(text);
    }

    public Element element(String name, Object... items) {
        return new Element(name, items).setWriter(writer);
    }

    //
    // Common HTML elements
    //

    public Element html(Object... content) {
        return element("html", content);
    }

    public Element head(Object... content) {
        return element("head", content);
    }

    public Element title(String title) {
        return element("title", title);
    }

    public Element linkCss(String uri) {
        return element("link").attribute("rel", "stylesheet").attribute("type", "text/css").attribute("href", uri);
    }

    public Element body(Object... content) {
        return element("body", content);
    }

    public Element div(Object... content) {
        return element("div", content);
    }

    public Element span(Object... content) {
        return element("span", content);
    }

    public Element h1(Object... content) {
        return element("h1", content);
    }

    public Element h2(Object... content) {
        return element("h2", content);
    }

    public Element h3(Object... content) {
        return element("h3", content);
    }

    public Element table(Object... content) {
        return element("table", content);
    }

    public Element tr(Object... content) {
        return element("tr", content);
    }

    public Element th(Object... content) {
        return element("th", content);
    }

    public Element row(Object... content) {
        return element("tr", wrap("td", content).toArray());
    }

    public Element rowHeader(Object... content) {
        return element("tr", wrap("th", content).toArray());
    }

    public List<Element> wrap(String name, Object[] content) {
        List<Element> cells = new ArrayList<Element>();
        if (content != null) {
            for (Object item : content) {
                cells.add(element(name, item));
            }
        }

        return cells;
    }

    public Element td(Object... content) {
        return element("td", content);
    }

    public Element p(Object... content) {
        return element("p", content);
    }

    public Element link(String target, Object... content) {
        return element("a", content).attribute("href", target);
    }

    public Element anchor(String target, Object... content) {
        return element("a", content).attribute("id", target).add("");
    }

    public Element hr() {
        return element("hr");
    }

    public Element img(String src, Object... content) {
        return element("img", content).attribute("src", src);
    }

    public Element h4(Object... content) {
        return element("h4", content);
    }

    public Element bold(Object... content) {
        return element("b", content);
    }

    public Element italic(Object... content) {
        return element("i", content);
    }

    public Element code(Object... content) {
        return element("code", content);
    }

    public Element br() {
        return element("br");
    }

    public Element definitionList(Object term, Object... content) {
        Element dl = element("dl", element("dt", term));
        if (content != null) {
            for (Object item : content) {
                dl.add(element("dd", item));
            }
        }
        return dl;
    }
}
