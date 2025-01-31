package de.woody64k.services.document.msg.service.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.simplejavamail.api.email.AttachmentResource;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

public class Attachment implements MultipartFile {

    private AttachmentResource attachment;

    public Attachment(AttachmentResource attachment) {
        super();
        this.attachment = attachment;
    }

    @Override
    public String getName() {
        return attachment.getName();
    }

    @Override
    public String getOriginalFilename() {
        return attachment.getName();
    }

    @Override
    public String getContentType() {
        return attachment.getDataSource()
                .getContentType();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public long getSize() {
        return 1;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return attachment.getDataSourceInputStream()
                .readAllBytes();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return attachment.getDataSourceInputStream();
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        FileCopyUtils.copy(attachment.getDataSourceInputStream()
                .readAllBytes(), dest);
    }

}
