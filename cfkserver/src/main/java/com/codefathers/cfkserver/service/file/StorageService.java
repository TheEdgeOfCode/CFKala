package com.codefathers.cfkserver.service.file;

import org.apache.commons.io.FileUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.*;

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

    public void saveProfile(String username, InputStreamResource file) throws IOException {
        File image = new File(users + username + ".jpg");
        byte[] buffer = file.getInputStream().readAllBytes();
        image.createNewFile();
        OutputStream outStream = new FileOutputStream(image);
        outStream.write(buffer);
        outStream.close();
    }

    public Resource getProfile(String username) {
        File image = new File(users + username + ".jpg");
        if (image.exists()) {
            return loadFileAsResource(image);
        } else {
            return null;
        }
    }

    private Resource loadFileAsResource(File file) {
        try {
            Path filePath = file.toPath();
            Resource resource = new ByteArrayResource(new FileInputStream(file).readAllBytes());
            if (resource.exists()) {
                return resource;
            } else {
                throw new StorageException("File not found ");
            }
        } catch (IOException e) {
            throw new StorageException("File not found ", e);
        }
    }

    public void saveProductImage(int id, InputStreamResource[] resource) throws IOException {
        File imageDir = new File(products + id);
        imageDir.mkdirs();
        FileUtils.cleanDirectory(imageDir);
        createMainImage(id, resource[0]);
        updateOtherImages(id, resource);
    }

    private void createMainImage(int id, InputStreamResource resource) throws IOException {
        File image = new File(products + id + "/main.jpg");
        image.createNewFile();
        saveDataToFile(resource.getInputStream(), image);
    }

    private void updateOtherImages(int id, InputStreamResource[] resources) throws IOException {
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

    public ArrayList<Resource> getProductImages(int id) {
        ArrayList<Resource> resources = new ArrayList<>();
        resources.add(getProductMainImage(id));
        resources.addAll(getAllOtherProductImages(id));
        return resources;
    }

    private ArrayList<Resource> getAllOtherProductImages(int id) {
        ArrayList<Resource> resources = new ArrayList<>();
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

    public Resource getProductMainImage(int id) {
        File image = new File(products + id + "/main.jpg");
        return loadFileAsResource(image);
    }
}
