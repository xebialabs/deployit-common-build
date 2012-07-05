package com.xebialabs.rest.doclet;

import java.io.PrintWriter;
import java.util.List;

import com.sun.javadoc.ClassDoc;

public class OverviewWriter extends JavadocWriter {

    public OverviewWriter(PrintWriter writer) {
        super(writer);
    }

    public void write(List<ClassDoc> services) {
        writeHeader();
        writeServices(services);
        writeFooter();
    }

    private void writeServices(List<ClassDoc> services) {
        table().cssClass("services").writeOpen();

        for (ClassDoc service : services) {
            row(
                    link(RestDoclet.fileNameFor(service), service.name()),
                    asText(service.firstSentenceTags())
             ).write();
        }

        table().writeClose();
    }

    //
    // Html rendering
    //

    public void writeHeader() {
        h1("Deployit REST API").write();
    }

    private void writeFooter() {
        p("&#169; 2012 XebiaLabs BV").cssClass("footer").write();
    }

}
