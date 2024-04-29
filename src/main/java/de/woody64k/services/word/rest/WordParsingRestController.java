package de.woody64k.services.word.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import de.woody64k.services.word.model.content.WordContent;
import de.woody64k.services.word.model.value.DocumentValueRequirement;
import de.woody64k.services.word.model.value.response.WordValues;
import de.woody64k.services.word.service.WordAnalyser;
import de.woody64k.services.word.service.WordParser;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/word")
public class WordParsingRestController {

    @Autowired
    private WordParser parser;

    @Autowired
    private WordAnalyser analyser;

    @PostMapping(value = "/content", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public WordContent parseWordContent(@RequestParam("file") MultipartFile uploadFile) {
        try {
            return parser.parseConent(uploadFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(value = "/content/values", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @RequestBody(content = @Content(encoding = @Encoding(name = "request", contentType = "application/json")))
    public WordValues parseValuesFromWord(@RequestPart DocumentValueRequirement request, @RequestPart("file") MultipartFile uploadFile) {
        try {
            WordContent parsedData = parseWordContent(uploadFile);
            return analyser.getValues(parsedData, request);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
