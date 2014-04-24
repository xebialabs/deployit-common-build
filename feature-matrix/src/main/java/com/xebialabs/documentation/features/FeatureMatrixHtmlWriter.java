package com.xebialabs.documentation.features;

import java.io.PrintWriter;
import java.util.List;

import com.xebialabs.commons.html.Element;
import com.xebialabs.commons.html.HtmlWriter;

/**
 * Produces HTML based on a {@link FeatureMatrix}.
 *
 * @author Hes Siemelink
 */
public class FeatureMatrixHtmlWriter extends HtmlWriter {

    private final FeatureMatrix matrix;

    public FeatureMatrixHtmlWriter(FeatureMatrix features, PrintWriter writer) {
        super(writer);
        this.matrix = features;
    }

    public void writeHtml() {
        html().writeOpen();
        body().writeOpen();
        table().writeOpen();

        // Header
        List<Element> headerCells = wrap("th", matrix.getSupportedVersions().toArray());
        headerCells.add(0, th(bold("Feature")));
        element("tr", headerCells.toArray()).write();

        // Content
        writeFeatureRows(matrix, "");

        table().writeClose();
        body().writeClose();
        html().writeClose();

        getWriter().flush();
    }

    protected void writeFeatureRows(FeatureMatrix features, String indent) {
        for (FeatureMatrix feature: features.getChildren().values()) {
            createRow(feature, indent).write();

            writeFeatureRows(feature, indent + " - ");
        }
    }

    protected Element createRow(FeatureMatrix type, String indent) {
        Element row = tr();
        row.add(td(indent + type.getName()));
        for (String version: matrix.getSupportedVersions()) {
            if (type.getSupportedVersions().contains(version)) {
                row.add(td("y"));
            } else {
                row.add(td("-"));
            }
        }

        return row;
    }

}
