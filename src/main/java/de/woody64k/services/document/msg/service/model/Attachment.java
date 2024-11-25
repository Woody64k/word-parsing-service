package de.woody64k.services.document.msg.service.model;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hsmf.datatypes.AttachmentChunks;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

public class Attachment implements MultipartFile {

    private final AttachmentChunks attachment;

    public Attachment(AttachmentChunks attachment) {
        super();
        this.attachment = attachment;
    }

    @Override
    public String getName() {
        return attachment.getAttachFileName()
                .getValue();
    }

    @Override
    public String getOriginalFilename() {
        return attachment.getAttachLongFileName()
                .getValue();
    }

    @Override
    public String getContentType() {
        return attachment.getAttachMimeTag()
                .getValue();
    }

    @Override
    public boolean isEmpty() {
        return attachment.getEmbeddedAttachmentObject().length == 0;
    }

    @Override
    public long getSize() {
        return attachment.getEmbeddedAttachmentObject().length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return attachment.getEmbeddedAttachmentObject();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(attachment.getEmbeddedAttachmentObject());
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        FileCopyUtils.copy(attachment.getEmbeddedAttachmentObject(), dest);
    }

}
