package com.xebialabs.commons.html;

import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A generic element class for composing XML or XHTML.  Supports writing to a {@link PrintWriter} or just a String.
 *
 * @author Hes Siemelink
 */
public class Element {

    private final String name;
    private Map<String, String> attributes = new LinkedHashMap<String, String>();
    private List<Object> content = new ArrayList<Object>();

    private PrintWriter myWriter;

    //
    // Constructors & Properties
    //

    public Element(String name, Object... items) {
        this.name = name;
        add(items);
    }

    public Element setWriter(PrintWriter writer) {
        this.myWriter = writer;
        return this;
    }

    public Element add(Object... items) {
        if (items == null) {
            return this;
        }

        content.addAll(Arrays.asList(items));

        return this;
    }

    public Element attribute(String attribute, String value) {
        attributes.put(attribute, value);

        return this;
    }

    //
    // Content
    //

    public String open() {
        return open(false);
    }

    public String open(boolean close) {
        StringBuilder builder = new StringBuilder("<").append(name);

        for (String key : attributes.keySet()) {
            builder.append(" ").append(key).append("=\"").append(attributes.get(key)).append("\"");
        }

        if (close) {
            builder.append("/");
        }
        builder.append(">");

        return builder.toString();
    }

    public String close() {
        return "</" + name + ">";
    }

    @Override
    public String toString() {

        Writer writer = new CharArrayWriter();
        write(new PrintWriter(writer));

        return writer.toString();
    }

    //
    // Write
    //

    public void write() {
        write(myWriter);
    }

    public void write(PrintWriter writer) {
        if (content.isEmpty()) {
            writer.println(open(true));
        } else {
            boolean newline = content.size() > 1 || (content.size() == 1 && content.get(0) instanceof Element);
            writeOpen(writer, newline);
            writeContent(writer);
            writeClose(writer);
        }
    }

    public void writeOpen() {
        writeOpen(myWriter, true);
    }

    public void writeOpen(PrintWriter writer, boolean newline) {
        writer.print(open());
        if (newline) {
            writer.println();
        }
    }

    public void writeContent() {
        writeContent(myWriter);
    }

    public void writeContent(PrintWriter writer) {
        for (Object item : content) {
            if (item instanceof Element) {
                ((Element) item).write(writer);
            } else {
                writer.print(item);
            }
        }
    }

    public void writeClose() {
        writeClose(myWriter);
    }

    public void writeClose(PrintWriter writer) {
        writer.println(close());
    }

    //
    // HTML
    //

    public Element id(String id) {
        return attribute("id", id);
    }

    public Element cssClass(String className) {
        return attribute("class", className);
    }

}
