package de.woody64k.services.word.rest;

import static org.junit.jupiter.api.Assertions.assertTrue;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import de.woody64k.services.word.model.value.request.DocumentValueRequirement;
import de.woody64k.services.word.model.value.response.WordValues;

@ActiveProfiles("test")
@SpringBootTest
class WordParsingRestControllerTest {
    @Value("classpath:TestDocument-request.json")
    Resource requestFile;

    @Value("classpath:TestDokument.docx")
    Resource wordFile;

    @Autowired
    WordParsingRestController analyser;

    private static ObjectMapper mapper;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        mapper = new ObjectMapper();
    }

    @Test
    void testValueMapper() {
        try {
            MockMultipartFile testFile = new MockMultipartFile("TestDokument", "TestDokument.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", Files.readAllBytes(wordFile.getFile().toPath()));
            DocumentValueRequirement valueRequest = mapper.readValue(requestFile.getFile(), DocumentValueRequirement.class);
            WordValues result = analyser.parseValuesFromWord(valueRequest, testFile);
            assertTrue(result.getData().size() > 0);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
