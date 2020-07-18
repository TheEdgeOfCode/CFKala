package com.codefathers.cfkserver.service.file;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Service
@ConfigurationProperties(prefix = "storage")
public class StorageService {
    private String users;
    private String products;

    @PostConstruct
    private void init() {
        File user = new File(users);
        File product = new File(products);
        user.mkdirs();
        product.mkdirs();
    }

    public void saveProfile(String username, MultipartFile file) throws IOException {
        File image = new File(users + username + ".jpg");
        byte[] buffer = file.getBytes();
        OutputStream outStream = new FileOutputStream(image);
        outStream.write(buffer);
        outStream.close();
    }
}
