package com.moklyak.ecommerce.dtos;


import lombok.Data;

@Data
public class RegisterDTO {
    String username;
    String email;
    String password;
    String passwordC;
}
