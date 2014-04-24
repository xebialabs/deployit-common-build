package com.xebialabs.documentation.features;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.List;

import com.google.common.io.Resources;

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
        String css = loadCss();
        head(element("style", css).attribute("type", "text/css")).write();
        body().writeOpen();
        table().writeOpen();

        // Header
        List<Element> headerCells = wrap("th", matrix.getSupportedVersions().toArray());
        headerCells.add(0, th(bold("Feature")));
        element("tr", headerCells.toArray()).write();

        // Content
        writeFeatureRows(matrix, "", true);

        table().writeClose();
        body().writeClose();
        html().writeClose();

        getWriter().flush();
    }

    private static String loadCss() {
        try {
            return Resources.toString(Resources.getResource("table.css"), Charset.forName("UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void writeFeatureRows(FeatureMatrix features, String indent, boolean showVirtual) {
        if (features.isVirtual() && !showVirtual) {
            return;
        }

        for (FeatureMatrix feature: features.getChildren().values()) {
            createRow(feature, indent).write();

            writeFeatureRows(feature, indent + " - ", false);
        }
    }

    protected Element createRow(FeatureMatrix type, String indent) {
        Element row = tr();
        row.add(td(indent + type.getName()));
        for (String version: matrix.getSupportedVersions()) {
            String indicator = "-";
            if (type.getSupportedVersions().contains(version)) {
                indicator = "&#10003;";
            }
            row.add(td(span(indicator).attribute("title", version)));
        }

        return row;
    }

}
