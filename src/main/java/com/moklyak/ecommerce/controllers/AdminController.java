package com.moklyak.ecommerce.controllers;


import com.moklyak.ecommerce.entities.Order;
import com.moklyak.ecommerce.entities.User;
import com.moklyak.ecommerce.repositories.OrderRepository;
import com.moklyak.ecommerce.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final int USER_PAGE_SIZE = 20;

    private final int ORDER_PAGE_SIZE = 20;
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private OrderRepository orderRepo;


    @GetMapping("/users")
    public ModelAndView users(Model model, @RequestParam @Nullable Integer pageNumber){
        if (pageNumber == null){
            pageNumber = 0;
        }
        List<User> page = userRepo.findAll(Pageable.ofSize(USER_PAGE_SIZE).withPage(pageNumber)).toList();
        model.addAttribute("clients", page);
        model.addAttribute("pageNumber", pageNumber);
        return new ModelAndView("adminClients", model.asMap());
    }

    @GetMapping("/orders")
    public ModelAndView orders(Model model, @RequestParam @Nullable Integer pageNumber){
        if (pageNumber == null){
            pageNumber = 0;
        }
        List<Order> page = orderRepo.findAll(Pageable.ofSize(ORDER_PAGE_SIZE).withPage(pageNumber)).toList();
        model.addAttribute("orders", page);
        model.addAttribute("pageNumber", pageNumber);
        return new ModelAndView("adminOrders", model.asMap());
    }

    @GetMapping("/addItem")
    public ModelAndView addItem(Model model){

        return new ModelAndView("adminItemForm", model.asMap());
    }

    @PostMapping("/blockUser")
    public ModelAndView blockUser(Model model, @RequestParam String email){
        User u = userRepo.findByEmail(email);
        u.setBlocked(true);
        userRepo.save(u);
        return new ModelAndView("redirect:/admin/users", model.asMap());
    }
    @PostMapping("/unblockUser")
    public ModelAndView unblockUser(Model model, @RequestParam String email){
        User u = userRepo.findByEmail(email);
        u.setBlocked(false);
        userRepo.save(u);
        return new ModelAndView("redirect:/admin/users", model.asMap());
    }
}
