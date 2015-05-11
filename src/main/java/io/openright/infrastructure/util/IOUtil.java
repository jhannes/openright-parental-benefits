package io.openright.infrastructure.util;

import java.io.*;
import java.net.URL;

public class IOUtil {
    public static File extractResourceFile(String filename) {
        File file = new File(filename);
        if (file.exists())
            return file;

        try (InputStream input = IOUtil.class.getResourceAsStream("/" + filename)) {
            if (input == null) {
                throw new IllegalArgumentException("Can't find /" + filename + " in classpath");
            }

            copy(input, file);
            return file;
        } catch (IOException e) {
            throw ExceptionUtil.soften(e);
        }
    }

    public static String toString(URL url) {
        try (InputStream content = (InputStream) url.getContent()) {
            return toString(content);
        } catch (IOException e) {
            throw ExceptionUtil.soften(e);
        }
    }

    public static String toString(InputStream content) {
        return toString(new InputStreamReader(content));
    }

    public static String toString(Reader reader) {
        StringWriter writer = new StringWriter();
        copy(reader, writer);
        return writer.toString();
    }

    private static void copy(Reader in, Writer out) {
        try {
            char[] buf = new char[1024];
            int count;
            while ((count = in.read(buf)) >= 0) {
                out.write(buf, 0, count);
            }
        } catch (IOException e) {
            throw ExceptionUtil.soften(e);
        }
    }

    public static File copy(URL url, File file) {
        if (file.isDirectory()) {
            file = new File(file, new File(url.getPath()).getName());
        }
        try (InputStream content = (InputStream) url.getContent()) {
            copy(content, file);
            return file;
        } catch (IOException e) {
            throw ExceptionUtil.soften(e);
        }

    }

    public static void copy(InputStream content, File file) {
        try (FileOutputStream output = new FileOutputStream(file)) {
            copy(content, output);
        } catch (IOException e) {
            throw ExceptionUtil.soften(e);
        }
    }

    public static void copy(InputStream in, OutputStream out) {
        try {
            byte[] buf = new byte[1024];
            int count;
            while ((count = in.read(buf)) >= 0) {
                out.write(buf, 0, count);
            }
        } catch (IOException e) {
            throw ExceptionUtil.soften(e);
        }
    }
}
