package de.woody64k.services.document.rest;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.woody64k.services.document.model.value.request.DocumentValueRequirement;
import de.woody64k.services.document.model.value.response.GenericObject;
import de.woody64k.services.document.model.value.response.DocumentValues;
import de.woody64k.services.document.rest.WordParsingRestController;

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
            MockMultipartFile testFile = new MockMultipartFile("TestDokument", "TestDokument.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", Files.readAllBytes(wordFile.getFile()
                    .toPath()));
            DocumentValueRequirement valueRequest = mapper.readValue(requestFile.getFile(), DocumentValueRequirement.class);
            DocumentValues result = analyser.parseValuesFromWord(valueRequest, testFile);
            assertTrue(result.getData()
                    .size() > 0);
            assertSplit(result);
            assertMergeAndFilter(result);
            assertDefaultValue(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void assertDefaultValue(DocumentValues result) {
        Object value = result.getData()
                .get("defaultValue");
        assertTrue(((String) value).contentEquals("Default Test Value"));
    }

    private void assertSplit(DocumentValues result) {
        List<GenericObject> pets = (List<GenericObject>) result.getData()
                .get("pets");
        GenericObject measures = (GenericObject) pets.get(0)
                .get("measures");
        assertTrue(((String) measures.get("additionalSizeInformation")).contentEquals("Wingspan: 24 cm"));
    }

    public void assertMergeAndFilter(DocumentValues result) {
        List<GenericObject> mergeAndFilterTestData = (List<GenericObject>) result.getData()
                .get("wettervorhersage");
        assertTrue(mergeAndFilterTestData.size() == 3);
        for (GenericObject mergedData : mergeAndFilterTestData) {
            List<GenericObject> filteredData = (List<GenericObject>) mergedData.get("weather");
            assertTrue(filteredData.size() == 1);
            String filteredValue = (String) filteredData.get(0)
                    .get("isPrediction");
            assertTrue(filteredValue == null || filteredValue.isBlank() || filteredValue.contentEquals("prediction"));
        }
    }
}
