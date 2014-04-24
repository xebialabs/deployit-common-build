package com.xebialabs.documentation.features;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.google.common.io.Files;

import com.xebialabs.deployit.booter.local.LocalBooter;
import com.xebialabs.xltype.serialization.json.JsonWriter;

/**
 * Utility functions to create a feature matrix; loading one from JSON; combining a list of JSON files into one feature
 * matrix and producing an HTML view.
 *
 * @author Hes Siemelink
 */
public class FeatureMatrixMaker {

    static {
        LocalBooter.bootWithoutGlobalContext();
    }

    public void create(String name, String version, String prefix, File destination) {

        String fullVersion = name + "-" + version;
        XLTypeFeatureMatrix matrix = new XLTypeFeatureMatrix(name);
        matrix.addVersion(fullVersion);
        matrix.scanTypes(fullVersion, prefix);

        JsonWriter writer = new JsonWriter();
        matrix.writeJson(writer);
        try {
            Files.write(writer.toString(), destination, Charset.forName("UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FeatureMatrix combine(List<File> files) {
        FeatureMatrix matrix = null;
        for (File file : files) {
            FeatureMatrix features = parse(file);
            if (matrix == null) {
                matrix = features;
            } else {
                matrix.combine(features);
            }
        }

        return matrix;
    }

    private static FeatureMatrix parse(File file) {
        try {
            String contents = Files.toString(file, Charset.forName("UTF-8"));
            JSONObject json = new JSONObject(contents);
            return new FeatureMatrix(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveAsHtml(FeatureMatrix matrix, File destination) {

        try (PrintWriter writer = new PrintWriter(new FileWriter(destination))) {
            new FeatureMatrixHtmlWriter(matrix, writer).writeHtml();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Relic for testing */
    public static void main(String[] args) {

        FeatureMatrixMaker maker = new FeatureMatrixMaker();
        File parent = new File("/Users/Hes/Code/deployit-common-build/feature-matrix/build");
        FeatureMatrix matrix = maker.combine(Arrays.asList(
            new File(parent, "was-plugin-3.9.0-features.json"),
            new File(parent, "was-plugin-extensions-3.9.0-features.json"),
            new File(parent, "was-plugin-4.0.0-features.json"),
            new File(parent, "was-plugin-extensions-4.0.0-features.json"),
            new File(parent, "was-plugin-4.1.0-SNAPSHOT-features.json"),
            new File(parent, "was-plugin-extensions-4.1.0-SNAPSHOT-features.json")
            ));

        maker.saveAsHtml(matrix, new File("feature-matrix.html"));
    }
}
