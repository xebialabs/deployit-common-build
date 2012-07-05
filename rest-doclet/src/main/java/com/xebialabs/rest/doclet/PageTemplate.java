package com.xebialabs.rest.doclet;

import java.io.PrintWriter;

import com.xebialabs.commons.html.HtmlWriter;

public class PageTemplate extends HtmlWriter {

    public PageTemplate(PrintWriter writer) {
        super(writer);
    }

    public void writeHeader(String title) {
        html().writeOpen();
        head(
                title(title),
                linkCss("layout.css"),
                linkCss("restdoc.css")
        ).write();

        // Start body
        FileCatalog.write(getWriter(), "html-body-start.html");
    }

    public void writeFooter() {
        FileCatalog.write(getWriter(), "html-body-end.html");
    }
}
