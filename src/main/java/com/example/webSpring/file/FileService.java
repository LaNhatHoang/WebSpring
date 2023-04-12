package com.example.webSpring.file;

import com.example.webSpring.response.FileResponse;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class FileService {
    private final Path storageFolder = Paths.get("uploads");
    public FileService(){
        try{
            Files.createDirectories(storageFolder);
        }catch (Exception e){
            throw new RuntimeException("Connot init storage");
        }
    }
    private boolean isImageFile(MultipartFile file){
        String fileExtend = FilenameUtils.getExtension(file.getOriginalFilename());
        return Arrays.asList(new String[]{"png", "jpg", "jpeg", "bmp"}).contains(fileExtend.trim().toLowerCase());
    }
    public FileResponse storageFile(MultipartFile file){
        try{
            if(file.isEmpty()){
                return FileResponse.builder().status(false).message("Failed to storage empty file").build();
            }
            if(!isImageFile(file)){
                return FileResponse.builder().status(false).message("File is not image").build();
            }
            float fileSizeMb = file.getSize()/1_000_000.0f;
            if(fileSizeMb > 0.4f){
                return FileResponse.builder().status(false).message("Size image must be <= 4Mb").build();
            }
            String fileExtend = FilenameUtils.getExtension(file.getOriginalFilename());
            String generatedFileName = UUID.randomUUID().toString().replace("-", "");
            generatedFileName = generatedFileName+"."+fileExtend;
            Path destinationFilePath = storageFolder.resolve(Paths.get(generatedFileName)).normalize().toAbsolutePath();
            if(!destinationFilePath.getParent().equals(storageFolder.toAbsolutePath())){
                throw new RuntimeException("Cannot store file outside current directory");
            }
            try(InputStream inputStream = file.getInputStream()){
                Files.copy(inputStream, destinationFilePath, StandardCopyOption.REPLACE_EXISTING);
            }
            return FileResponse.builder().status(true).message("Upload success").nameImage(generatedFileName).build();
        }catch (Exception e){
            return FileResponse.builder().status(false).message("Can not storage image").build();
        }
    }
    public byte[] readFileContent(String fileName){
        try {
            Path file = storageFolder.resolve(fileName);
            Resource resource = new UrlResource(file.toUri());
            if(resource.exists() || resource.isReadable()){
                byte[] bytes = StreamUtils.copyToByteArray(resource.getInputStream());
                return bytes;
            }
            else{
                throw new RuntimeException("Could not read file");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public Stream<Path> loadAll(){
        try{
            return Files.walk(storageFolder,1)
                    .filter(path -> !path.equals(storageFolder))
                    .map(storageFolder::relativize);
        }catch (Exception e){
            throw new RuntimeException("Failed to load stored file");
        }
    }
}
