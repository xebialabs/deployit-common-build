package com.xebialabs.rest.doclet;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.google.common.io.Files;
import com.google.common.io.InputSupplier;

/**
 * Utility class for working with files and class path resources.
 */
public abstract class Resource implements InputSupplier<InputStream> {

    public void write(PrintWriter writer) {
        BufferedReader in = new BufferedReader(new InputStreamReader(getInput()));
        try {
            String line = null;
            while ((line = in.readLine()) != null) {
                writer.println(line);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not read resource " + getName(), e);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                // Ignore
            }
        }
    }

    static final int BUFFER = 2048;

    public void unzip(File destinationDir) {
        try {
            destinationDir.mkdirs();
            BufferedOutputStream dest = null;
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(getInput()));
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                int count;
                byte data[] = new byte[BUFFER];
                // write the files to the disk
                File file = new File(destinationDir, entry.getName());
                if (entry.isDirectory()) {
                    file.mkdirs();
                } else {
                    FileOutputStream fos = new FileOutputStream(file);
                    dest = new BufferedOutputStream(fos, BUFFER);
                    while ((count = zis.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, count);
                    }
                    dest.flush();
                    dest.close();
                }
            }
            zis.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void copy(File destination) {
        try {
            Files.copy(this, destination);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public abstract InputStream getInput();

    public abstract String getName();

    public static Resource fromClasspath(final String name) {
        return new Resource() {

            @Override
            public InputStream getInput() {
                return Resource.class.getClassLoader().getResourceAsStream(name);
            }

            @Override
            public String getName() {
                return name;
            }
        };
    }

    public static Resource file(final File file) {
        return new Resource() {

            @Override
            public InputStream getInput() {
                try {
                    return new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    throw new IllegalArgumentException("Can't open " + getName(), e);
                }
            }

            @Override
            public String getName() {
                return file.getAbsolutePath();
            }
        };
    }
}
