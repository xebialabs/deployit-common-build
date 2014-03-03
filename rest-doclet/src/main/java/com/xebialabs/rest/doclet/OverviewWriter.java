package com.xebialabs.rest.doclet;

import java.io.PrintWriter;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;

/**
 * Generates the summary page.
 */
public class OverviewWriter extends RestdocWriter {

    public OverviewWriter(PrintWriter writer) {
        super(writer);
    }

    public void write(List<ClassDoc> services) {
        writeHeader();
        writePackageInfo(services);
        writeServices(services);
        writeFooter();
    }

    private void writePackageInfo(List<ClassDoc> services) {
        // Collect packages
        Set<PackageDoc> packages = new LinkedHashSet<PackageDoc>();
        for (ClassDoc service : services) {
            packages.add(service.containingPackage());
        }

        // Write packages
        for (PackageDoc pack : packages) {
            p(asText(pack.inlineTags())).write();
        }
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
        h1("XL Deploy REST API").write();
    }

    private void writeFooter() {
        p("&#169; 2012 XebiaLabs BV").cssClass("footer").write();
    }

}
