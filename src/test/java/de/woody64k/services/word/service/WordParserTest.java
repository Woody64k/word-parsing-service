package de.woody64k.services.word.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

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
            parser.parseConent(testFile);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
