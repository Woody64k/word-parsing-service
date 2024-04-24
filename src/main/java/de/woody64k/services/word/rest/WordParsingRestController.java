package de.woody64k.services.word.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import de.woody64k.services.word.service.WordParser;

@RestController
@RequestMapping("/word")
public class WordParsingRestController {

    @Autowired
    private WordParser parser;

    @PostMapping("/parser")
    public void handleFileUpload(@RequestParam("file") MultipartFile uploadFile) {
        try {
            parser.parseConent(uploadFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
