package de.woody64k.services.document.word.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import de.woody64k.services.document.model.content.DocumentContent;
import de.woody64k.services.document.word.service.WordParser;

@ActiveProfiles("test")
@SpringBootTest
class WordParserTest {
    @Value("classpath:data/data.json")
    Resource resourceFile;
    private static File file;

    @Autowired
    WordParser parser;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        file = new File(WordParserTest.class.getClassLoader()
                .getResource("TestDokument.docx")
                .getFile());

    }

    @Test
    void testParser() {
        try {
            MockMultipartFile testFile = new MockMultipartFile("TestDokument", "TestDokument.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", Files.readAllBytes(file.toPath()));

            DocumentContent content = parser.parseConent(testFile);
            assertTrue(content.getTables()
                    .size() == 6, "Number of found Tables is wrong.");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    void testValueMapper() {
        try {
            MockMultipartFile testFile = new MockMultipartFile("TestDokument", "TestDokument.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", Files.readAllBytes(file.toPath()));

            WordParser parser = new WordParser();
            DocumentContent content = parser.parseConent(testFile);
            assertTrue(content.getTables()
                    .size() == 6, "Number of found Tables is wrong.");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
