package de.woody64k.services.document.pdf.service;

import java.io.File;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PdfParser {

    public void parseConent(MultipartFile uploadFile) {
        File file = new File("path/to/your/pdf.pdf");
//            PdfDocument pdf = new PdfDocument(file);
//            BasicExtractionAlgorithm bea = new BasicExtractionAlgorithm();
//
//            for (Page page : pdf.getPages()) {
//                List<String> text = bea.extract(page)
//                        .getText();
//                for (String line : text) {
//                    System.out.println(line);
//                }
//            }
    }
}