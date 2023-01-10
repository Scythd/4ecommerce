package com.moklyak.ecommerce.controllers;

import com.moklyak.ecommerce.entities.*;
import com.moklyak.ecommerce.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private LibraryRepository libraryRepository;


    @GetMapping("/addItem/{id}")
    public ModelAndView addItem(Model model, @Nullable @PathVariable Long id){
        Cart cart = getCurrentUserCart();
        Item i = itemRepository.findById(id).get();
        if (i == null){
            model.addAttribute("error", "No such product");
            return new ModelAndView("/", model.asMap());
        }
        cart.getItems().add(i);
        cartRepository.save(cart);
        model.addAttribute("error", "\"%s\" successfully added to cart".formatted(i.getName()));
        return new ModelAndView("redirect:/", model.asMap());
    }

    @GetMapping("/removeItem/{id}")
    public ModelAndView removeItem(Model model, @Nullable @PathVariable Long id){
        Cart cart = getCurrentUserCart();
        List<Item> items =  cart.getItems().stream().filter(x->id.equals(x.getId())).collect(Collectors.toList());
        if (items.size() != 1){
            model.addAttribute("error", "Can't remove item from cart. Call support for help.");
            return new ModelAndView("redirect:/cart/list", model.asMap());
        }
        Item i = items.get(0);
        cart.getItems().remove(i);
        cartRepository.save(cart);
        model.addAttribute("error", "\"%s\" successfully removed from cart".formatted(i.getName()));
        return new ModelAndView("redirect:/cart/list", model.asMap());
    }

    private Cart getCurrentUserCart() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Cart cart = cartRepository.findByUser_Email(email);
        if (cart == null){
            cart = new Cart();
            cart.setUser(userRepository.findByEmail(email));
            cart.setItems(new HashSet<>());
        }
        return cart;
    }

    @GetMapping("/list")
    public ModelAndView list(Model model, @Nullable @RequestParam String error){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Cart cart = cartRepository.findByUser_Email(email);
        if (cart == null){
            cart = new Cart();
            cart.setUser(userRepository.findByEmail(email));
            cart.setItems(new HashSet<>());
        }
        if (model.getAttribute("error") == null){
            model.addAttribute("error", error);
        }
        model.addAttribute("cart", cart);
        Double sum = cart.getItems().stream().mapToDouble(x -> x.getPrice()).sum();
        model.addAttribute("sum", sum);
        return new ModelAndView("cart", model.asMap());
    }

    @GetMapping("/buy")
    public ModelAndView buy(Model model){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Cart cart = cartRepository.findByUser_Email(email);
        if (cart == null){
            cart = new Cart();
            cart.setUser(userRepository.findByEmail(email));
            cart.setItems(new HashSet<>());
        }
        if (cart.getItems().size() == 0){
            model.addAttribute("error", "Cart shouldn't be empty");
            return new ModelAndView("redirect:/cart/list", model.asMap());
        }
        model.addAttribute("cart", cart);
        Double sum = cart.getItems().stream().mapToDouble(x -> x.getPrice()).sum();
        model.addAttribute("sum", sum);
        return new ModelAndView("order", model.asMap());
    }

    @GetMapping("/buyConfirm")
    public ModelAndView confirm(Model model){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Cart cart = cartRepository.findByUser_Email(email);
        Order order = new Order();
        User u = userRepository.findByEmail(email);
        order.setUser(u);
        order.setDateProvided(Timestamp.from(Instant.now()));
        order.setItems(cart.getItems().stream().collect(Collectors.toList()));
        order = orderRepository.save(order);
        cartRepository.delete(cart);
        Library lib = libraryRepository.findByUser_Email(email);
        if (lib == null) {
            lib = new Library();
            lib.setUser(u);
            lib.setItems(new HashSet<>());
        }
        lib.getItems().addAll(order.getItems());
        libraryRepository.save(lib);
        return new ModelAndView("orderSuccess", model.asMap());
    }
}
