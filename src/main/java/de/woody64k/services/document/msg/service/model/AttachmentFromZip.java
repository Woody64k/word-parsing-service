package de.woody64k.services.document.msg.service.model;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.activation.MimetypesFileTypeMap;

public class AttachmentFromZip implements MultipartFile {

    private ZipEntry attachment;
    private byte[] data;
    private static final MimetypesFileTypeMap TYPE_MAP = new MimetypesFileTypeMap();

    public AttachmentFromZip(ZipEntry attachment, ZipInputStream zipIn) throws IOException {
        super();
        this.attachment = attachment;
        this.data = zipIn.readAllBytes();
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
        return URLConnection.guessContentTypeFromName(attachment.getName());
    }

    @Override
    public boolean isEmpty() {
        return data.length == 0L;
    }

    @Override
    public long getSize() {
        return data.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return data;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(data);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        FileCopyUtils.copy(data, dest);
    }

}
