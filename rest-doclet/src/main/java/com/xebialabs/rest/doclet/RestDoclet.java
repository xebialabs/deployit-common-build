package com.xebialabs.rest.doclet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.sun.javadoc.*;

/**
 * Doclet implementation for generating REST documentation. Main entry point for the Javadoc tool.
 */
public class RestDoclet {

    private static File destinationDir = new File(".");

    /**
     * Needed by Javadoc engine.
     */
    public static int optionLength(@SuppressWarnings("unused") String option) {
        // Dummy implementation
        return 2;
    }

    /**
     * Needed by Javadoc engine.
     */
    public static boolean validOptions(String[][] options, @SuppressWarnings("unused") DocErrorReporter reporter) {
        for (String[] optionList : options) {
            if (optionList[0].equals("-d")) {
                destinationDir = new File(optionList[1]);
            }
        }
        return true;
    }

    /**
     * Needed by Javadoc engine to provide generic type information.
     */
    public static LanguageVersion languageVersion() {
        return LanguageVersion.JAVA_1_5;
    }

    /**
     * Main entry point for Javadoc.
     */
    public static boolean start(RootDoc root) {
        List<ClassDoc> restServices = findRestServices(root);

        copyCss();
        writeOverview(restServices);
        writeServices(restServices);
        writeLinkedFiles();

        return true;
    }

    private static void copyCss() {
        Resource.fromClasspath("restdoc/layout.css").copy(new File(destinationDir, "layout.css"));
        Resource.fromClasspath("restdoc/restdoc.css").copy(new File(destinationDir, "restdoc.css"));
        Resource.fromClasspath("restdoc/image.zip").unzip(destinationDir);
    }

    private static List<ClassDoc> findRestServices(RootDoc root) {
        List<ClassDoc> restServices = new ArrayList<ClassDoc>();
        for (ClassDoc classDoc : root.classes()) {
            if (isRestService(classDoc)) {
                restServices.add(classDoc);
            }
        }
        return restServices;
    }

    private static boolean isRestService(ClassDoc classDoc) {
        for (AnnotationDesc annotation : classDoc.annotations()) {
            if (annotation.annotationType().qualifiedName().equals("javax.ws.rs.Path")) {
                return true;
            }
        }
        return false;
    }

    private static void writeOverview(List<ClassDoc> restServices) {
        PrintWriter writer = openFile("index.html", "Deployit REST API");
        try {
            new OverviewWriter(writer).write(restServices);
        } finally {
            closeFile(writer);
        }
    }

    private static void writeServices(List<ClassDoc> restServices) {
        // Register files that are going to be generated, for cross-referencing
        for (ClassDoc service : restServices) {
            FileCatalog.SINGLETON.add(fileNameFor(service));
        }

        // Generate documentation per service
        for (ClassDoc service : restServices) {
            PrintWriter writer = openFile(fileNameFor(service), service.name());
            try {
                new RestServiceWriter(writer, service).writeRestService();
            } finally {
                closeFile(writer);
            }
        }
    }

    private static void writeLinkedFiles() {
        for (String item : FileCatalog.SINGLETON.getItems()) {
            if (FileCatalog.isResource(item)) {
                PrintWriter writer = openFile(item, item.replace(".html", ""));
                try {
                    FileCatalog.write(item, writer);
                } finally {
                    closeFile(writer);
                }
            }
        }

        for (String item : FileCatalog.SINGLETON.getMissing()) {
            System.out.println("Missing cross reference: " + item);
        }
    }

    public static String fileNameFor(Type service) {
        if (service == null) {
            return null;
        }
        return service.qualifiedTypeName() + ".html";
    }

    public static String fileNameFor(String reference) {
        if (reference == null) {
            return null;
        }
        return reference + ".html";
    }

    public static PrintWriter openFile(String fileName, String title) {
        File file = new File(destinationDir, fileName);
        System.out.println("Writing " + file.getAbsolutePath());
        try {
            PrintWriter writer = new PrintWriter(file);
            new PageTemplate(writer).writeHeader(title);
            return writer;
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("Can't open " + file.getAbsolutePath() + " for writing.");
        }
    }

    public static void closeFile(PrintWriter writer) {
        new PageTemplate(writer).writeFooter();
        writer.flush();
        writer.close();
    }

}
