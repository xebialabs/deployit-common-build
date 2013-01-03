package com.xebialabs.rest.doclet;

import java.io.PrintWriter;

import com.xebialabs.commons.html.HtmlWriter;

/**
 * Provides header and footer for generated files.
 */
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
        FileCatalog.write("html-body-start.html", getWriter());
    }

    public void writeFooter() {
        FileCatalog.write("html-body-end.html", getWriter());
    }
}
