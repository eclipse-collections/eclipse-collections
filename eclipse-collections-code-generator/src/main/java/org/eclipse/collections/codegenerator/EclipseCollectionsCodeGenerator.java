/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.codegenerator;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.zip.CRC32;

import org.eclipse.collections.codegenerator.model.Primitive;
import org.eclipse.collections.codegenerator.tools.FileUtils;
import org.eclipse.collections.codegenerator.tools.IntegerOrStringRenderer;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STErrorListener;
import org.stringtemplate.v4.STGroupFile;

public class EclipseCollectionsCodeGenerator
{
    private final Pattern resourcePattern;
    private final ErrorListener errorListener;
    private final File outputDirectory;
    private final String fileExtension;

    private int numFileWritten = 0;

    public EclipseCollectionsCodeGenerator(
            String templateDirectory,
            ErrorListener errorListener,
            File outputDirectory,
            String fileExtension)
    {
        Objects.requireNonNull(templateDirectory);
        this.resourcePattern = Pattern.compile(templateDirectory + "/.*\\.stg");
        this.errorListener = Objects.requireNonNull(errorListener);
        this.outputDirectory = Objects.requireNonNull(outputDirectory);
        this.fileExtension = Objects.requireNonNull(fileExtension);
    }

    /**
     * Generates code and only write contents to disk which differ from the current file contents.
     *
     * @return The number of files written.
     */
    public int generateFiles()
    {
        List<URL> templateFiles = FileUtils.findTemplateFiles(this.resourcePattern);

        for (URL url : templateFiles)
        {
            STGroupFile templateFile = new STGroupFile(url, "UTF-8", '<', '>');
            if (!templateFile.isDefined("fileName"))
            {
                continue;
            }

            STErrorListener stErrorListener = new LoggingErrorListener(this.errorListener, url);

            templateFile.setListener(stErrorListener);
            templateFile.registerRenderer(String.class, new IntegerOrStringRenderer());

            File targetPath = this.constructTargetPath(templateFile);
            FileUtils.createDirectory(targetPath);

            boolean hasTwoPrimitives = this.renderBooleanTemplate(templateFile, "hasTwoPrimitives");
            boolean skipBoolean = this.renderBooleanTemplate(templateFile, "skipBoolean");
            boolean skipBooleanKeys = this.renderBooleanTemplate(templateFile, "skipBooleanKeys");
            boolean forSpecializedStream = this.renderBooleanTemplate(templateFile, "forSpecializedStream");

            if (hasTwoPrimitives)
            {
                for (Primitive primitive1 : Primitive.values())
                {
                    if (primitive1 == Primitive.BOOLEAN && (skipBoolean || skipBooleanKeys))
                    {
                        continue;
                    }
                    for (Primitive primitive2 : Primitive.values())
                    {
                        if (primitive2 == Primitive.BOOLEAN && skipBoolean)
                        {
                            continue;
                        }
                        String sourceFileName = this.executeTemplate(templateFile, "fileName", primitive1, primitive2);
                        File outputFile = new File(targetPath, sourceFileName + this.fileExtension);

                        if (!EclipseCollectionsCodeGenerator.sourceFileExists(outputFile))
                        {
                            String classContents = this.executeTemplate(templateFile, "class", primitive1, primitive2);
                            this.checkSumClassContentsAndWrite(classContents, outputFile);
                        }
                    }
                }
            }
            else
            {
                for (Primitive primitive : Primitive.values())
                {
                    if (primitive == Primitive.BOOLEAN && skipBoolean)
                    {
                        continue;
                    }
                    if (forSpecializedStream && !primitive.hasSpecializedStream())
                    {
                        continue;
                    }
                    String sourceFileName = this.executeTemplate(templateFile, "fileName", primitive);
                    File outputFile = new File(targetPath, sourceFileName + this.fileExtension);

                    if (!EclipseCollectionsCodeGenerator.sourceFileExists(outputFile))
                    {
                        String classContents = this.executeTemplate(templateFile, "class", primitive);
                        this.checkSumClassContentsAndWrite(classContents, outputFile);
                    }
                }
            }
        }

        return this.numFileWritten;
    }

    private void checkSumClassContentsAndWrite(String classContents, File outputFile)
    {
        long checksumValue = EclipseCollectionsCodeGenerator.calculateChecksum(classContents);

        Path outputChecksumPath = Paths.get(outputFile.getAbsolutePath() + ".crc");
        if (!outputChecksumPath.toFile().exists())
        {
            this.writeFileAndChecksum(outputFile, classContents, checksumValue, outputChecksumPath, false);
            return;
        }

        String existingChecksum = FileUtils.readFile(outputChecksumPath);
        if (existingChecksum.equals(String.valueOf(checksumValue)))
        {
            return;
        }

        this.writeFileAndChecksum(outputFile, classContents, checksumValue, outputChecksumPath, true);
    }

    private static long calculateChecksum(String string)
    {
        CRC32 checksum = new CRC32();
        checksum.update(string.getBytes(StandardCharsets.UTF_8));
        return checksum.getValue();
    }

    private void writeFileAndChecksum(
            File outputFile,
            String output,
            long checksumValue,
            Path outputChecksumPath,
            boolean outputFileMustExist)
    {
        this.numFileWritten++;
        FileUtils.writeToFile(output, outputFile, outputFileMustExist);
        FileUtils.writeToFile(String.valueOf(checksumValue), outputChecksumPath.toFile(), outputFileMustExist);
    }

    private String executeTemplate(
            STGroupFile templateFile,
            String templateName,
            Primitive primitive)
    {
        ST template = this.findTemplate(templateFile, templateName);
        template.add("primitive", primitive);
        return template.render();
    }

    private String executeTemplate(
            STGroupFile templateFile,
            String templateName,
            Primitive primitive1,
            Primitive primitive2)
    {
        ST template = this.findTemplate(templateFile, templateName);
        template.add("primitive1", primitive1);
        template.add("primitive2", primitive2);
        template.add("sameTwoPrimitives", primitive1 == primitive2);
        return template.render();
    }

    private ST findTemplate(STGroupFile templateFile, String templateName)
    {
        ST template = templateFile.getInstanceOf(templateName);
        if (template == null)
        {
            throw new RuntimeException("Could not find template " + templateName + " in " + templateFile.getFileName());
        }
        return template;
    }

    private boolean renderBooleanTemplate(STGroupFile templateFile, String templateName)
    {
        if (!templateFile.isDefined(templateName))
        {
            return false;
        }
        ST template = templateFile.getInstanceOf(templateName);
        String render = template.render();
        return Boolean.valueOf(render);
    }

    private File constructTargetPath(STGroupFile templateFile)
    {
        ST targetPath = this.findTemplate(templateFile, "targetPath");
        String renderedTargetPath = targetPath.render();
        return new File(this.outputDirectory, renderedTargetPath);
    }

    private static boolean sourceFileExists(File outputFile)
    {
        File newPath = new File(outputFile.getAbsolutePath()
                .replace("target", "src")
                .replace("generated-sources", "main")
                .replace("generated-test-sources", "test"));
        return newPath.exists();
    }
}
