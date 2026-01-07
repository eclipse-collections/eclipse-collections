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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collection;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EclipseCollectionsCodeGeneratorTest
{
    @Test
    public void validateApiTemplates() throws Exception
    {
        validateTemplates("api", 1338);
    }

    @Test
    public void validateImplTemplates() throws Exception
    {
        validateTemplates("impl", 1469);
    }

    @Test
    public void validateTestTemplates() throws Exception
    {
        validateTemplates("test", 2342);
    }

    private static void validateTemplates(String templateDirectory, int expectedFileCount) throws Exception
    {
        BasicErrorListener errorListener = new BasicErrorListener();
        File outputDirectory = Files.createTempDirectory("temp-").toFile();
        outputDirectory.deleteOnExit();
        EclipseCollectionsCodeGenerator generator = new EclipseCollectionsCodeGenerator(
                templateDirectory,
                errorListener,
                outputDirectory,
                ".java");
        int fileCount = generator.generateFiles();
        assertEquals(expectedFileCount, fileCount);
        assertFalse(String.valueOf(errorListener), errorListener.hasErrors());
        String[] extensions = new String[]{"java"};
        Collection<File> javaFiles = org.apache.commons.io.FileUtils.listFiles(outputDirectory, extensions, true);
        assertEquals(expectedFileCount, javaFiles.size());
        for (File javaFile: javaFiles)
        {
            assertValidJavaSourceFile(javaFile);
        }
    }

    private static void assertValidJavaSourceFile(File file) throws Exception
    {
        assertTrue(file.exists());
        assertTrue(file.canRead());
        assertTrue(file.length() > 0);
        ParserConfiguration config = new ParserConfiguration();
        config.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_8);
        JavaParser parser = new JavaParser(config);
        String code = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        ParseResult<CompilationUnit> parseResult = parser.parse(code);
        assertTrue(String.valueOf(parseResult.getProblems()) + "\n" + code,
                parseResult.isSuccessful());
    }
}
