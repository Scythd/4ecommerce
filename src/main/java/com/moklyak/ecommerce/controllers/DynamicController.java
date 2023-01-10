package com.moklyak.ecommerce.controllers;

import com.moklyak.ecommerce.entities.Item;
import com.moklyak.ecommerce.entities.User;
import com.moklyak.ecommerce.repositories.ItemRepository;
import com.moklyak.ecommerce.repositories.LibraryRepository;
import com.moklyak.ecommerce.repositories.UserRepository;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/dynamic")
public class DynamicController {

    private static Path WORKING_DIR;
    {
        try {
            WORKING_DIR = Paths.get(AdminController.class.getClassLoader().getResource("").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    public static final String PUBLIC_UPLOAD_DIR = AdminController.PUBLIC_UPLOAD_DIR;
    public static final String PRIVATE_UPLOAD_DIR = AdminController.CONTENT_UPLOAD_DIR;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private LibraryRepository libraryRepository;


    @GetMapping(value = "/poster/{filename}", produces = MediaType.IMAGE_JPEG_VALUE )
    public @ResponseBody byte[] getImage(@PathVariable String filename) throws IOException {
        return Files.readAllBytes(WORKING_DIR.resolve(PUBLIC_UPLOAD_DIR + filename));
    }

    @GetMapping(value = "/content/{id}/{fileName}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE )
    public @ResponseBody byte[] getContent(@PathVariable Long id, @PathVariable String fileName) throws IOException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Item i = itemRepository.findByLibraries_User_EmailAndId(email, id);



        if (i== null){
            throw new org.springframework.security.access.AccessDeniedException("403 returned");
        } else {
            if (!i.getFileURI().contains(fileName)){
                throw new org.springframework.security.access.AccessDeniedException("403 returned");
            }
        }
        Path p = WORKING_DIR.resolve(PRIVATE_UPLOAD_DIR + fileName);
        return Files.readAllBytes(p);
    }

}
