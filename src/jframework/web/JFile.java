package jframework.web;

import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class JFile {

    public Part part;


    public Part getPart() {
        return part;
    }

    public void setPart(Part part) {
        this.part = part;
    } 

    public JFile(){}  

    public String getName() {
        return part.getName();
    }

    public String getOriginalFilename() {
        return part.getSubmittedFileName();
    }

    public String getContentType() {
        return part.getContentType();
    }

    public long getSize() {
        return part.getSize();
    }

    public boolean isEmpty() {
        return getSize() == 0;
    }

    public InputStream getInputStream() throws IOException {
        return part.getInputStream();
    }

    public byte[] getBytes() throws IOException {
        try (InputStream in = getInputStream()) {
            return in.readAllBytes();
        }
    }

    public void transferTo(String path) throws IOException {
        try {
            
            File dest = new File(path);
            File parent = dest.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
            if (!parent.canWrite()) {
                throw new IOException("On ne peut pas ecrire dans : " + parent.getAbsolutePath());
            }
            Files.copy(this.getInputStream(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

}
