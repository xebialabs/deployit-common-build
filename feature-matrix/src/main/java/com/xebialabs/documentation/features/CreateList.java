package com.xebialabs.documentation.features;

import java.io.File;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

/**
 * Main entry point to create a feature matrix by scanning the XL Types currently on the classpath.
 *
 * @author Hes Siemelink
 */
public class CreateList {

    @Option(name = "-name", usage = "Name of plugin", required = true)
    private String name;

    @Option(name = "-version", usage = "Current version", required = true)
    private String version;

    @Option(name = "-prefix", usage = "Prefix of types to scan", required = false)
    private String prefix = "";

    @Option(name = "-file", usage = "Destination file name", required = true)
    private String file;

    public void create() {
        new FeatureMatrixMaker().create(name, version, prefix, new File(file));
    }

    public static void main(String[] args) {
        CreateList options = parseCommandLine(args, new CreateList());
        if (options == null) {
            return;
        }

        options.create();
    }

    public static <T> T parseCommandLine(String[] args, T object) {
        final CmdLineParser parser = new CmdLineParser(object);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println("\nUsage:");
            parser.printUsage(System.err);
            return null;
        }
        return object;

    }

}
