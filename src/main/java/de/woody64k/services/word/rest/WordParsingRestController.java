package de.woody64k.services.word.rest;

import java.io.IOException;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/word")
public class WordParsingRestController {

    @PostMapping("/parser")
    public void handleFileUpload(@PathVariable String templateName, @RequestParam("file") MultipartFile uploadFile) {
        try {
            uploadFile.getBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
