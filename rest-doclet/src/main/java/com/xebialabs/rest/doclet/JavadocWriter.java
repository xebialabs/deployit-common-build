package com.xebialabs.rest.doclet;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.sun.javadoc.ParameterizedType;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.Tag;
import com.sun.javadoc.Type;
import com.xebialabs.commons.html.HtmlWriter;

public class JavadocWriter extends HtmlWriter {

    public JavadocWriter(PrintWriter writer) {
        super(writer);
    }

    protected String asText(Tag[] tags) {
        StringBuilder builder = new StringBuilder();
        for (Tag tag : tags) {
            if (tag instanceof SeeTag) {
                appendLink(builder, (SeeTag) tag);
            } else {
                builder.append(tag.text());
            }
        }
        return builder.toString();
    }

    private void appendLink(StringBuilder builder, SeeTag tag) {
        String file = RestDoclet.fileNameFor(tag.referencedClass());
        if (file != null && FileCatalog.SINGLETON.getItems().contains(file)) {
            builder.append(link(file, tag.text()));
        } else {
            builder.append(code(tag.text()));
        }
    }

    protected String asText(Type type) {
        return asText(type, " of ", ", ", "");
    }

    protected String asReference(Type type) {
        return asText(type, "-", "-", ".html");
    }

    private static String asText(Type type, String separator1, String separator2, String end) {
        StringBuilder builder = new StringBuilder();
        builder.append(type.simpleTypeName());

        ParameterizedType paramType = type.asParameterizedType();
        if (paramType != null) {
            builder.append(separator1);
            for (int i = 0; i < paramType.typeArguments().length; i++) {
                Type param = paramType.typeArguments()[i];
                builder.append(param.simpleTypeName());
                if (i < paramType.typeArguments().length - 1) {
                    builder.append(separator2);
                }
            }
        }
        builder.append(end);
        return builder.toString();
    }

    public List<Type> getParameterizedTypes(Type type) {

        List<Type> types = new ArrayList<Type>();

        ParameterizedType paramType = type.asParameterizedType();
        if (paramType != null) {
            for (Type param : paramType.typeArguments()) {
                types.add(param);
            }
        }

        return types;
    }
}
