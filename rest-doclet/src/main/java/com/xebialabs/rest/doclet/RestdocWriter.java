package com.xebialabs.rest.doclet;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.sun.javadoc.ParameterizedType;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.Tag;
import com.sun.javadoc.Type;
import com.xebialabs.commons.html.HtmlWriter;

public class RestdocWriter extends HtmlWriter {

    public RestdocWriter(PrintWriter writer) {
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
        String text = tag.label() != null ? tag.label() : tag.text();

        if (file != null && FileCatalog.SINGLETON.check(file)) {
            builder.append(link(file, text));
        } else {
            builder.append(bold(text));
        }
    }

    protected String asText(Type type) {
        return asText(type, " of ", ", ", "");
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

    public static String firstWord(Tag tag) {
        return tag.text().split("\\s")[0];
    }

    public static String restOfSentence(Tag tag) {
        return tag.text().substring(firstWord(tag).length());
    }

    protected String asReference(Type type) {
        List<Type> types = getParameterizedTypes(type);

        // Default case: fully qualified name of non-parameterized type.
        if (types.isEmpty()) {
            return type.qualifiedTypeName() + ".html";
        }

        // Cook something up for 'List of'
        return types.get(0) + "-" + type.simpleTypeName() + ".html";
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

    protected Object renderType(Type type) {
        Object returnTypeText = asText(type);
        String externalFile = asReference(type);

        // Add references to catalog
        if (FileCatalog.SINGLETON.check(externalFile)) {
            returnTypeText = link(externalFile, returnTypeText);
        }
        for (Type paramType : getParameterizedTypes(type)) {
            FileCatalog.SINGLETON.check(asReference(paramType));
        }

        return returnTypeText;
    }
}
