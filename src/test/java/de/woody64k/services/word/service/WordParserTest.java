package de.woody64k.services.word.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import de.woody64k.services.word.model.WordContent;

class WordParserTest {
    private static File file;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        file = new File(WordParserTest.class.getClassLoader().getResource("TestDokument.docx").getFile());

    }

    @Test
    // @Disabled("Not implemented yet")
    void test() {
        try {
            MockMultipartFile testFile = new MockMultipartFile("TestDokument", "TestDokument.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", Files.readAllBytes(file.toPath()));

            WordParser parser = new WordParser();
            WordContent content = parser.parseConent(testFile);
            assertTrue(content.getTables().size() == 1, "Number of found Tables is wrong.");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
