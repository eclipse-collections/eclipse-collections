/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.codegenerator.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public final class FileUtils
{
    private FileUtils()
    {
        throw new AssertionError("Suppress for noninstantiability");
    }

    public static void writeToFile(String data, File outputFile, boolean outputFileMustExist)
    {
        if (!outputFile.delete() && outputFileMustExist)
        {
            throw new IllegalStateException(outputFile.getAbsolutePath());
        }
        try (BufferedWriter bufferedWriter = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8)))
        {
            bufferedWriter.write(data);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Could not write generated sources to file: " + e);
        }
    }

    public static List<URL> getAllTemplateFilesFromClasspath(String templateDirectory, List<URL> classPathURLs)
    {
        List<URL> files = new ArrayList<>();
        try
        {
            for (URL url : classPathURLs)
            {
                recurseURL(url, files, templateDirectory);
            }
        }
        catch (IOException | URISyntaxException e)
        {
            throw new RuntimeException(e);
        }
        return files;
    }

    private static void recurseURL(URL url, List<URL> files, String templateDirectory) throws URISyntaxException, IOException
    {
        if ("file".equals(url.getProtocol()))
        {
            recurse(new File(url.toURI()), new File(url.toURI()), files, templateDirectory);
        }
        else
        {
            if (url.getPath().endsWith(".jar"))
            {
                JarInputStream stream = new JarInputStream(url.openStream());
                processJar(stream, files, templateDirectory);
                stream.close();
            }
        }
    }

    private static void recurse(File rootDirectory, File file, List<URL> files, String templateDirectory) throws IOException
    {
        if (file.isDirectory())
        {
            File[] children = file.listFiles();
            if (children != null)
            {
                for (File child : children)
                {
                    recurse(rootDirectory, child, files, templateDirectory);
                }
            }
        }
        else
        {
            if (file.getName().endsWith(".jar"))
            {
                try (JarInputStream stream = new JarInputStream(new FileInputStream(file)))
                {
                    processJar(stream, files, templateDirectory);
                }
            }
            else
            {
                String rootPath = rootDirectory.getAbsolutePath();
                String filePath = file.getAbsolutePath();
                if (filePath.contains(templateDirectory) && !rootPath.equals(filePath) && isTemplateFile(filePath))
                {
                    files.add(new URL("file:" + filePath));
                }
            }
        }
    }

    private static void processJar(
            JarInputStream stream,
            List<URL> files,
            String templateDirectory) throws IOException
    {
        JarEntry entry;
        while ((entry = stream.getNextJarEntry()) != null)
        {
            String entryName = entry.getName();
            if (isTemplateFile(entryName) && entryName.startsWith(templateDirectory))
            {
                files.add(FileUtils.class.getClassLoader().getResource(entryName));
            }
        }
    }

    public static void createDirectory(File path)
    {
        if (!path.exists())
        {
            boolean mkdirs = path.mkdirs();
            if (!mkdirs)
            {
                throw new RuntimeException("Could not create directory " + path);
            }
        }
    }

    private static boolean isTemplateFile(String filePath)
    {
        return filePath.endsWith(".stg");
    }

    public static String readFile(Path path)
    {
        try
        {
            return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
