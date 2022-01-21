package com.nkd.quizmaker.service;

import com.nkd.quizmaker.exception.FileStorageException;
import com.nkd.quizmaker.exception.MyFileNotFoundException;
import com.nkd.quizmaker.model.FileStorageProperties;
import com.nkd.quizmaker.repo.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {


    private Path rootDir;
    private final FileRepository fileRepository;

    @Autowired
    public FileStorageService(FileStorageProperties fileStorageProperties, FileRepository fileRepository) {
        this.fileRepository = fileRepository;
        Path path = Paths.get(fileStorageProperties.getUploadDir()).normalize().toAbsolutePath();
        try {
            //create root directory
            rootDir = Files.createDirectories(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Path createSubDir(String dirName) {
        try {
            Path createdDirPath = Files.createDirectories(this.rootDir.resolve(dirName));
            log.info("Created directory : " + createdDirPath);
            return createdDirPath;
        } catch (IOException e) {
            log.error("Could not create storage directory - {}", e.getMessage());
            throw new FileStorageException("Could not create storage directory", e.getCause());
        }
    }

    public String storeFile(MultipartFile file) throws FileStorageException {
        return store(file, rootDir);
    }


    public String storeFile(MultipartFile file, String folder) throws FileStorageException {
        Path path = createSubDir(folder);
        return store(file, path);

    }


    String store(MultipartFile file, Path dir) {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        if (fileName.contains(".."))
            throw new FileStorageException("File contains invalid characters");
        String[] split = fileName.split("\\.");
        com.nkd.quizmaker.model.File f = new com.nkd.quizmaker.model.File();
        f.setType(file.getContentType());
        String modifiedName = split[0] + "-" + System.currentTimeMillis();
        f.setName(modifiedName);
        String ext = "." + split[1];
        f.setExt(ext);
        fileName = modifiedName + ext;
        Path path = dir.resolve(fileName);
        try {
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            fileRepository.save(f);

            return modifiedName;
        } catch (IOException e) {
            throw new FileStorageException("Could not store file ", e.getCause());
        }
    }

    public Resource loadFile(String fileName) {
        Optional<com.nkd.quizmaker.model.File> optional = fileRepository.findByName(fileName);
        if (optional.isEmpty())
            return null;
        com.nkd.quizmaker.model.File file = optional.get();

        String rs = getFile(new File(this.rootDir.toUri()), file.getName() + file.getExt());
        Resource resource;
        try {
            resource = new FileUrlResource(rs);
            if (resource.exists()) {
                return resource;
            } else {
                throw new MyFileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        throw new MyFileNotFoundException("File not found " + fileName);
    }

    //get all files by parent folder
    public List<File> getFiles(String folder) {
        List<File> rs = new ArrayList<>();
        File file = new File(this.rootDir.resolve(folder).toUri());
        if (!file.exists()) {
            return new ArrayList<>();
        }
        return getChildren(file, rs);
    }

    List<File> getChildren(File file, List<File> rs) {
        if (file.isDirectory()) {
            for (File f :
                    Objects.requireNonNull(file.listFiles())) {
                if (f.isDirectory()) {
                    return getChildren(f, rs);
                }
                rs.add(f);
            }
        } else {
            rs.add(file);
        }
        return rs;
    }

    String getFile(File file, String name) {
        if (file.isDirectory()) {
            for (File f :
                    file.listFiles()) {
                String s = getFile(f, name);
                if (s != null) {
                    return s;
                }
            }
        } else {
            String rs = null;
            if (file.getName().equals(name)) {
                log.info("FOUND FILE NAME : " + file.getName());
                rs = file.getPath();
            }
            return rs;
        }
        return null;
    }


}
