package de.woody64k.services.document.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import de.woody64k.services.document.model.content.DocumentContent;
import de.woody64k.services.document.model.value.request.DocumentValueRequirement;
import de.woody64k.services.document.model.value.request.validate.RequestValidator;
import de.woody64k.services.document.model.value.response.DocumentValues;
import de.woody64k.services.document.pdf.service.PdfParser;
import de.woody64k.services.document.service.DocumentAnalyser;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/pdf")
public class PdfParsingRestController {

    @Autowired
    private PdfParser parser;

    @Autowired
    private DocumentAnalyser analyser;

    @CrossOrigin
    @PostMapping(value = "/content", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public DocumentContent parseContent(@RequestParam List<String> tableHeaderIndicator, @RequestParam("file") MultipartFile uploadFile) {
        try {
            return parser.parseContent(tableHeaderIndicator, uploadFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @CrossOrigin
    @PostMapping(value = "/content/values", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @RequestBody(content = @Content(encoding = @Encoding(name = "request", contentType = "application/json")))
    public DocumentValues parseValues(@RequestParam List<String> tableHeaderIndicator, @RequestPart DocumentValueRequirement request, @RequestPart("file") MultipartFile uploadFile) {
        try {
            RequestValidator.validate(request);
            DocumentContent parsedData = parseContent(tableHeaderIndicator, uploadFile);
            return analyser.getValues(parsedData, request);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
