package com.multishop.repository;

import org.springframework.web.multipart.MultipartFile;

public interface StorageRepository 
{
	String saveFile(MultipartFile file, String category);
}
