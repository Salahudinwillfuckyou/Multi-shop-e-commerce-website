package com.multishop.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.multishop.repository.StorageRepository;

@Service
public class StorageService implements StorageRepository
{
	private final String BASE_UPLOAD_DIR = "src/main/resources/static/Images/uploads/";
	@Override
	public String saveFile(MultipartFile file, String category) {
		// TODO Auto-generated method stub
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is empty or null");
        }
 
        try 
        {
            String categoryDir = BASE_UPLOAD_DIR + category + "/";
            Files.createDirectories(Paths.get(categoryDir)); 
 
            // Generate a unique filename
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(categoryDir, fileName);
 
            Files.write(filePath, file.getBytes());
 
            return "/Images/uploads/" + category + "/" + fileName; // Return relative path
        } catch (IOException e) {
            throw new RuntimeException("Error saving file", e);
        }
	}

}
