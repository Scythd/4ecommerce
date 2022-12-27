package com.moklyak.ecommerce.controllers;

import com.moklyak.ecommerce.dtos.LoginDTO;
import com.moklyak.ecommerce.dtos.RegisterDTO;
import com.moklyak.ecommerce.entities.Role;
import com.moklyak.ecommerce.entities.User;
import com.moklyak.ecommerce.repositories.RoleRepository;
import com.moklyak.ecommerce.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@Controller
@RequestMapping("/")
public class AuthController {

    @Autowired
    private static UserRepository userRepository;

    @Autowired
    private static RoleRepository roleRepository;
    @Autowired
    private static PasswordEncoder passwordEncoder;

    static Role userRole;

    @GetMapping("/login")
    public static String getLogin(Model model){
        return "login";
    }

    @PostMapping("/login")
    public static String postLogin(Model model, @ModelAttribute LoginDTO loginDTO){
        // login logic
        String pwd = loginDTO.getPassword();
        loginDTO.setPassword("");
        User user = userRepository.findByEmail(loginDTO.getEmail());
        if (user == null){
            return handleError(model, loginDTO, "No such user", "reg");
        }
        pwd = passwordEncoder.encode(pwd);
        if (user.getPassword().equals(pwd)) {

        } else {
            return handleError(model, loginDTO, "Invalid username or password", "reg");
        }
        return MainController.index(model);
    }

    @GetMapping("/reg")
    public static String getReg(Model model){
       /* Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        CustomUser custom = (CustomUser) authentication == null ? null : authentication.getPrincipal();
      */
        return "reg";
    }

    @PostMapping("/reg")
    public static String postReg(Model model, RegisterDTO regDTO){
        // register logic
        if (!regDTO.getPassword().equals(regDTO.getPasswordC())){
            regDTO.setPassword("");
            return handleError(model, regDTO, "Passwords don't match", "reg");
        }
        regDTO.setPasswordC("");
        String pwd = regDTO.getPassword();
        regDTO.setPassword("");
        if (pwd.length() < 8){
            return handleError(model, regDTO, "Password must be longer or equals 8 characters", "reg");
        }

        if (pwd.matches("[A-Z]+") || pwd.matches("[a-z]+") || pwd.matches("[0-9]+")){
            return handleError(model, regDTO,
                    "Password must contain at least one of each kind: " +
                            "number, uppercase letter, lowercase letter", "reg");
        }

        if (userRole == null){
            userRole = roleRepository.findByName("ROLE_USER");
        }
        if (userRepository.findByEmail(regDTO.getEmail()) == null){
            User user = new User();
            user.setUsername(regDTO.getUsername());
            user.setPassword(passwordEncoder.encode(pwd));
            user.setEmail(regDTO.getEmail());
            user.setRoles(List.of(userRole));
            userRepository.save(user);
        } else {
            model.addAttribute("dto", regDTO);
            model.addAttribute("error", "User this same email already exist.");
            return "reg";
        }
        return MainController.index(model);
    }

    private static String handleError(Model model, Object dto, String msg, String view){
        model.addAttribute("dto", dto);
        model.addAttribute("error", msg);
        return view;
    }


}
