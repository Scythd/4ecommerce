package com.moklyak.ecommerce.controllers;

import com.moklyak.ecommerce.dtos.LoginDTO;
import com.moklyak.ecommerce.dtos.RegisterDTO;
import com.moklyak.ecommerce.entities.Role;
import com.moklyak.ecommerce.entities.User;
import com.moklyak.ecommerce.repositories.RoleRepository;
import com.moklyak.ecommerce.repositories.UserRepository;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;


@Controller
@RequestMapping("/")
public class AuthController {

    String EMAIL_REG_EXP = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    @Autowired
    CatalogController mc;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    static Role userRole;

    @Autowired
    private AuthenticationManager authManager;

    @PreAuthorize("!isAuthenticated()")
    @GetMapping("/login")
    public ModelAndView getLogin(Model model, @RequestParam(name = "error", required = false) Object error){
        if (error != null){
            model.addAttribute("error", error);
        }
        LoginDTO ldto = new LoginDTO();
        ldto.setPassword("");
        ldto.setEmail("");
        model.addAttribute("loginDTO", ldto);
        return new ModelAndView("login", model.asMap());
    }

    @PreAuthorize("!isAuthenticated()")
    @PostMapping("/login")
    public ModelAndView postLogin(HttpServletRequest req, Model model, @ModelAttribute LoginDTO loginDTO){
        // login logic
        String pwd = loginDTO.getPassword();
        loginDTO.setPassword("");
        User user = userRepository.findByEmail(loginDTO.getEmail());
        if (user == null){
            return handleError(model, loginDTO, "No such user", "login");
        }
        if (user.isBlocked()){
            return handleError(model, loginDTO, "User was blocked", "login");
        }
        if (passwordEncoder.matches(pwd, user.getPassword())) {
            UsernamePasswordAuthenticationToken authReq
                    = new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), pwd);
            Authentication auth = authManager.authenticate(authReq);
            SecurityContext sc = SecurityContextHolder.getContext();
            sc.setAuthentication(auth);
            HttpSession session = req.getSession(true);
            session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);
            return new ModelAndView("redirect:/", model.asMap());
        } else {
            return handleError(model, loginDTO, "Invalid username or password", "login");
        }
    }

    @PreAuthorize("!isAuthenticated()")
    @GetMapping("/register")
    public ModelAndView getReg(Model model, RegisterDTO regDTO){
       /* Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        CustomUser custom = (CustomUser) authentication == null ? null : authentication.getPrincipal();
      */
        return new ModelAndView("reg", model.asMap());
    }


    @PostMapping("/register")
    public ModelAndView postReg(Model model, RegisterDTO regDTO){
        // register logic
        if (regDTO.getUsername().length() < 3){
            regDTO.setPassword("");
            regDTO.setPasswordC("");
            return handleError(model, regDTO, "Username should be longer then or equals 3 letters", "reg");
        }
        if (!regDTO.getEmail().matches(EMAIL_REG_EXP)){
            regDTO.setPassword("");
            regDTO.setPasswordC("");
            return handleError(model, regDTO, "Wrong email pattern, should be something like a@a.a", "reg");
        }
        if (!regDTO.getPassword().equals(regDTO.getPasswordC())){
            regDTO.setPassword("");
            regDTO.setPasswordC("");
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
            return new ModelAndView("reg", model.asMap());
        }
        return new ModelAndView("regSuccess", model.asMap());
    }

    private ModelAndView handleError(Model model, Object dto, String msg, String view){
        model.addAttribute("dto", dto);
        model.addAttribute("error", msg);
        return new ModelAndView(view, model.asMap());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/logout")
    private ModelAndView logout(HttpServletRequest req, Model model){
        try {
            req.logout();
        } catch (ServletException e) {
            System.out.println(e.getMessage());
        }
        return new ModelAndView("redirect:/", model.asMap());
    }
}
