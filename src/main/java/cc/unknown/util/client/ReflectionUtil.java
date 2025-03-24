package cc.unknown.util.client;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ReflectionUtil {

    public static Set<Class<?>> getClassesInPackage(String packageName) {
        Set<Class<?>> classes = new HashSet<>();
        String path = packageName.replace('.', '/');

        try {
            Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(path);

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                if (resource.getProtocol().equals("file")) {
                    File directory = new File(resource.toURI());
                    findClassesInDirectory(directory, packageName, classes);
                } else if (resource.getProtocol().equals("jar")) {
                    findClassesInJar(resource, packageName, classes);
                }
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        return classes;
    }

    private static void findClassesInDirectory(File directory, String packageName, Set<Class<?>> classes) {
        if (!directory.exists()) return;

        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            String fileName = file.getName();
            if (file.isDirectory()) {
                findClassesInDirectory(file, packageName + "." + fileName, classes);
            } else if (fileName.endsWith(".class")) {
                String className = packageName + "." + fileName.substring(0, fileName.length() - 6);
                try {
                    classes.add(Class.forName(className));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void findClassesInJar(URL resource, String packageName, Set<Class<?>> classes) throws IOException {
        String jarPath = resource.getPath().substring(5, resource.getPath().indexOf("!"));
        try (JarFile jarFile = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName().replace('/', '.');

                if (entryName.startsWith(packageName) && entryName.endsWith(".class")) {
                    String className = entryName.substring(0, entryName.length() - 6);
                    try {
                        classes.add(Class.forName(className));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}