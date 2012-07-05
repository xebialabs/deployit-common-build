package com.xebialabs.rest.doclet;

import java.io.PrintWriter;
import java.util.Set;
import java.util.TreeSet;

import static com.xebialabs.rest.doclet.Resource.classpathResource;

public class FileCatalog {

    public final static FileCatalog SINGLETON = new FileCatalog();

    private final Set<String> items = new TreeSet<String>();
    private final Set<String> missing = new TreeSet<String>();

    public boolean check(String reference) {
        if (items.contains(reference)) {
            return true;
        }

        if (isResource(reference)) {
            items.add(reference);
            return true;
        } else {
            missing.add(reference);
        }

        return false;
    }

    public void add(String reference) {
        items.add(reference);
    }

    public static boolean isResource(String reference) {
        return FileCatalog.class.getClassLoader().getResource(asResourcePath(reference)) != null;
    }

    private static String asResourcePath(String reference) {
        return "restdoc/" + reference;
    }

    public Set<String> getItems() {
        return items;
    }

    public Set<String> getMissing() {
        return missing;
    }

    public static void write(PrintWriter writer, String item) {
        classpathResource(asResourcePath(item)).write(writer);
    }
}
