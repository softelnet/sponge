/*
 * Copyright 2016-2019 The Sponge authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openksavi.sponge.integration.tests.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.jar.JarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import org.openksavi.sponge.config.ConfigException;
import org.openksavi.sponge.core.kb.FileKnowledgeBaseScript;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.spring.SpringSpongeEngine;

public class KnowledgeBaseLoadFromArchive {

    private static final String ARCHIVE_LOCATION_DIR = "target/kb-archive-test";

    private static final String ARCHIVE_FILE = ARCHIVE_LOCATION_DIR + "/kb-archive.jar";

    private static final String KB_SOURCEFILE_DIR = "examples/core";

    private static final String KB_FILE_1 = "knowledge_base_from_archive_1.py";

    private static final String KB_FILE_2 = "knowledge_base_from_archive_2.py";

    private static final String KB_FILE_2_DIR = "file2";

    private static final String ARCHIVE_FILE_2 = ARCHIVE_LOCATION_DIR + "/kb-archive2.jar";

    private static final String KB_FILE_3 = "knowledge_base_from_archive_3.py";

    private static final String ACTION_ARG = "Action arg";

    private static final String NON_EXISTING_ARCHIVE_FILE = ARCHIVE_LOCATION_DIR + "/non-existing-dir/kb-non-existing-*-archive.jar";

    private void addKbFile(ArchiveOutputStream o, String kbFile, String entryName) throws IOException {
        File source = new File(KB_SOURCEFILE_DIR, kbFile);
        o.putArchiveEntry(o.createArchiveEntry(source, entryName));
        try (InputStream i = Files.newInputStream(source.toPath())) {
            IOUtils.copy(i, o);
        }
        o.closeArchiveEntry();
    }

    private void addKbFile(ArchiveOutputStream o, String kbFile) throws IOException {
        addKbFile(o, kbFile, kbFile);
    }

    private void createOneLevelArchive() throws IOException {
        Files.createDirectories(Paths.get(ARCHIVE_LOCATION_DIR));

        OutputStream fo = Files.newOutputStream(Paths.get(ARCHIVE_FILE));
        try (ArchiveOutputStream o = new JarArchiveOutputStream(fo)) {
            addKbFile(o, KB_FILE_1);
            addKbFile(o, KB_FILE_2);
            o.finish();
        }
    }

    private void deleteArchives() throws IOException {
        FileUtils.deleteQuietly(new File(ARCHIVE_FILE));
        FileUtils.deleteQuietly(new File(ARCHIVE_FILE_2));
    }

    private void finish(SpongeEngine engine, boolean assertEngineState) throws IOException {
        if (assertEngineState) {
            assertFalse(engine.isError());
            assertTrue(engine.isRunning());
        }

        engine.shutdown();
        deleteArchives();
    }

    private void finish(SpongeEngine engine) throws IOException {
        finish(engine, true);
    }

    @Test
    public void testLoadFromArchiveByWildcard() throws IOException {
        createOneLevelArchive();

        SpongeEngine engine = SpringSpongeEngine.builder().knowledgeBase("kb", "spar:" + ARCHIVE_FILE + "!/*.py").build();
        engine.startup();

        try {
            assertEquals(ACTION_ARG.toUpperCase(),
                    engine.getOperations().call(String.class, "Action1FromArchive", Arrays.asList(ACTION_ARG)));
            assertEquals(ACTION_ARG.toLowerCase(),
                    engine.getOperations().call(String.class, "Action2FromArchive", Arrays.asList(ACTION_ARG)));
        } finally {
            finish(engine);
        }
    }

    @Test
    public void testLoadFromArchiveByFilename() throws IOException {
        createOneLevelArchive();

        SpongeEngine engine = SpringSpongeEngine.builder().knowledgeBase("kb", "spar:" + ARCHIVE_FILE + "!/" + KB_FILE_1).build();
        engine.startup();

        try {
            assertEquals(ACTION_ARG.toUpperCase(),
                    engine.getOperations().call(String.class, "Action1FromArchive", Arrays.asList(ACTION_ARG)));
            assertFalse(engine.getOperations().hasAction("Action2FromArchive"));
        } finally {
            finish(engine);
        }
    }

    private void createTwoLevelArchive() throws IOException {
        Files.createDirectories(Paths.get(ARCHIVE_LOCATION_DIR));

        OutputStream fo = Files.newOutputStream(Paths.get(ARCHIVE_FILE));
        try (ArchiveOutputStream o = new JarArchiveOutputStream(fo)) {
            addKbFile(o, KB_FILE_1);
            addKbFile(o, KB_FILE_2, KB_FILE_2_DIR + "/" + KB_FILE_2);
            o.finish();
        }
    }

    @Test
    public void testLoadFromTwoLevelArchiveByWildcardLevel1() throws IOException {
        createTwoLevelArchive();

        SpongeEngine engine = SpringSpongeEngine.builder().knowledgeBase("kb", "spar:" + ARCHIVE_FILE + "!/*.py").build();
        engine.startup();

        try {
            assertEquals(ACTION_ARG.toUpperCase(),
                    engine.getOperations().call(String.class, "Action1FromArchive", Arrays.asList(ACTION_ARG)));
            assertFalse(engine.getOperations().hasAction("Action2FromArchive"));
        } finally {
            finish(engine);
        }
    }

    @Test
    public void testLoadFromTwoLevelArchiveByWildcardAllLevels() throws IOException {
        createTwoLevelArchive();

        SpongeEngine engine = SpringSpongeEngine.builder().knowledgeBase("kb", "spar:" + ARCHIVE_FILE + "!/**/*.py").build();
        engine.startup();

        try {
            assertEquals(ACTION_ARG.toUpperCase(),
                    engine.getOperations().call(String.class, "Action1FromArchive", Arrays.asList(ACTION_ARG)));
            assertEquals(ACTION_ARG.toLowerCase(),
                    engine.getOperations().call(String.class, "Action2FromArchive", Arrays.asList(ACTION_ARG)));
        } finally {
            finish(engine);
        }
    }

    @Test
    public void testLoadFromTwoLevelArchiveByWildcardLevel2() throws IOException {
        createTwoLevelArchive();

        SpongeEngine engine =
                SpringSpongeEngine.builder().knowledgeBase("kb", "spar:" + ARCHIVE_FILE + "!/" + KB_FILE_2_DIR + "/*.py").build();
        engine.startup();

        try {
            assertFalse(engine.getOperations().hasAction("Action1FromArchive"));
            assertEquals(ACTION_ARG.toLowerCase(),
                    engine.getOperations().call(String.class, "Action2FromArchive", Arrays.asList(ACTION_ARG)));
        } finally {
            finish(engine);
        }
    }

    private void createSecondArchive() throws IOException {
        Files.createDirectories(Paths.get(ARCHIVE_LOCATION_DIR));

        OutputStream fo = Files.newOutputStream(Paths.get(ARCHIVE_FILE_2));
        try (ArchiveOutputStream o = new JarArchiveOutputStream(fo)) {
            addKbFile(o, KB_FILE_3);
            o.finish();
        }
    }

    @Test
    public void testLoadFromTwoLevelArchiveByWildcardLevelAndArchiveFileWildcard() throws IOException {
        createTwoLevelArchive();
        createSecondArchive();

        SpongeEngine engine = SpringSpongeEngine.builder().knowledgeBase("kb", "spar:" + ARCHIVE_LOCATION_DIR + "/kb-*.jar!/*.py").build();
        engine.startup();

        try {
            assertEquals(ACTION_ARG.toUpperCase(),
                    engine.getOperations().call(String.class, "Action1FromArchive", Arrays.asList(ACTION_ARG)));
            assertFalse(engine.getOperations().hasAction("Action2FromArchive"));
            assertEquals(ACTION_ARG, engine.getOperations().call(String.class, "Action3FromArchive", Arrays.asList(ACTION_ARG)));
        } finally {
            finish(engine);
        }
    }

    @Test
    public void testLoadFromNonExistingArchiveByWildcardRequired() throws IOException {
        createTwoLevelArchive();

        String file = "spar:" + NON_EXISTING_ARCHIVE_FILE + "!/*.py";

        SpongeEngine engine = SpringSpongeEngine.builder().knowledgeBase("kb", new FileKnowledgeBaseScript(file, true)).build();

        try {
            ConfigException exception = assertThrows(ConfigException.class, () -> engine.startup());

            assertEquals(String.format("Knowledge base file(s) %s not found", file), exception.getMessage());
        } finally {
            finish(engine, false);
        }
    }

    @Test
    public void testLoadFromNonExistingArchiveByWildcardOptional() throws IOException {
        createTwoLevelArchive();

        String file = "spar:" + NON_EXISTING_ARCHIVE_FILE + "!/*.py";

        SpongeEngine engine = SpringSpongeEngine.builder().knowledgeBase("kb", new FileKnowledgeBaseScript(file, false)).build();

        try {
            engine.startup();
        } finally {
            finish(engine, false);
        }
    }
}
