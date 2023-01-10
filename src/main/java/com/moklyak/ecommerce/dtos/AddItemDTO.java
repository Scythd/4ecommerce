package com.moklyak.ecommerce.dtos;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class AddItemDTO {

    private String name;

    private String description;

    private String creator;

    private String additionalInfo;

    private String tags;

    private String releaseDate;

    private Double price;

}
