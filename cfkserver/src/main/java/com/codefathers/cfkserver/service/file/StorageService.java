package com.codefathers.cfkserver.service.file;

import com.codefathers.cfkserver.model.entities.product.Document;
import org.apache.commons.io.FileUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;


import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Path;
import java.util.*;

@Service
@ConfigurationProperties(prefix = "storage")
public class StorageService {
    private String users = "src/main/resources/db/users/";
    private String products = "src/main/resources/db/products/image/";
    private String productFiles = "src/main/resources/db/files/products/";

    @PostConstruct
    private void init() {
        File user = new File(users);
        File product = new File(products);
        File files = new File(productFiles);
        user.mkdirs();
        product.mkdirs();
        files.mkdirs();
    }

    public void saveProfile(String username, InputStreamResource file) throws IOException {
        File image = new File(users + username + ".jpg");
        byte[] buffer = file.getInputStream().readAllBytes();
        image.createNewFile();
        OutputStream outStream = new FileOutputStream(image);
        outStream.write(buffer);
        outStream.close();
    }

    public ByteArrayResource getProfile(String username) {
        File image = new File(users + username + ".jpg");
        if (image.exists()) {
            return loadFileAsResource(image);
        } else {
            return null;
        }
    }

    public void deleteProfile(String username){
        File image = new File(users + username + ".jpg");
        try {
            FileUtils.forceDeleteOnExit(image);
        } catch (IOException ignore) {}
    }

    private ByteArrayResource loadFileAsResource(File file) {
        try {
            Path filePath = file.toPath();
            ByteArrayResource resource = new ByteArrayResource(new FileInputStream(file).readAllBytes());
            if (resource.exists()) {
                return resource;
            } else {
                throw new StorageException("File not found ");
            }
        } catch (IOException e) {
            throw new StorageException("File not found ", e);
        }
    }

    public void saveProductImage(int id, ByteArrayResource resource) throws IOException {
        File imageDir = new File(products + id);
        imageDir.mkdirs();
        FileUtils.cleanDirectory(imageDir);
        createMainImage(id, resource);
    }

    private void createMainImage(int id, ByteArrayResource resource) throws IOException {
        File image = new File(products + id + "/main.jpg");
        image.createNewFile();
        saveDataToFile(resource.getInputStream(), image);
    }

    private void updateOtherImages(int id, ByteArrayResource[] resources) throws IOException {
        for (int i = 1; i < resources.length; i++) {
            saveImageForProduct(id, resources[i].getInputStream(), i);
        }
    }

    private void saveImageForProduct(int id, InputStream data, int i) {
        File directory = new File(products + id + "/other");
        directory.mkdirs();
        File image = new File(products + id + "/other/" + i + ".jpg");
        try {
            if (image.createNewFile()) {
                saveDataToFile(data, image);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveDataToFile(InputStream data, File file) throws IOException {
        byte[] buffer = new byte[data.available()];
        data.read(buffer);
        OutputStream outStream = new FileOutputStream(file);
        outStream.write(buffer);
        outStream.close();
    }

    public ByteArrayResource getProductImages(int id) {
        return getProductMainImage(id);
    }

    private ArrayList<ByteArrayResource> getAllOtherProductImages(int id) {
        ArrayList<ByteArrayResource> resources = new ArrayList<>();
        File otherImagesDirectory = new File(products + id + "/other");
        File[] otherImages = otherImagesDirectory.listFiles();
        if (otherImages != null) {
            List<File> files = Arrays.asList(otherImages);
            files.forEach(file -> {
                if (file.exists())
                    resources.add(loadFileAsResource(file));
            });
        }
        return resources;
    }

    public ByteArrayResource getProductMainImage(int id) {
        File image = new File(products + id + "/main.jpg");
        return loadFileAsResource(image);
    }

    public String saveProductFile(int id,ByteArrayResource resource,String format) throws IOException {
        File file = new File(productFiles + id + "." + format);
        file.createNewFile();
        saveDataToFile(resource.getInputStream(),file);
        return file.getPath();
    }

    public ByteArrayResource getFile(Document document) {
        File file = new File(productFiles + document.getProduct().getId() + "." + document.getFormat());
        if (file.exists()){
            return loadFileAsResource(file);
        }else {
            throw new RuntimeException("File Does Not Exist");
        }
    }
}
