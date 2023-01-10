package com.moklyak.ecommerce.controllers;


import com.moklyak.ecommerce.entities.Cart;
import com.moklyak.ecommerce.entities.Item;
import com.moklyak.ecommerce.entities.Library;
import com.moklyak.ecommerce.repositories.ItemRepository;
import com.moklyak.ecommerce.repositories.LibraryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/library")
public class LibraryController {
    @Autowired
    private LibraryRepository libraryRepository;
    @Autowired
    private ItemRepository itemRepository;

    private final int PAGE_SIZE = 20;

    @GetMapping("/list")
    public ModelAndView list(Model model, @Nullable @RequestParam Integer pageNumber) {
        if (pageNumber == null) {
            pageNumber = 0;
        }
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Library lib = libraryRepository.findByUser_Email(email);
        List<Item> page = itemRepository.findAllByLibrariesId(lib.getId(), Pageable.ofSize(PAGE_SIZE)
                        .withPage(pageNumber))
                    .stream()
                    .map(x -> {
                        x.setPosterURI(x.getPosterURI().substring(x.getPosterURI().lastIndexOf("/")));
                        if (x.getDescription().length() > 220){
                            x.setDescription(x.getDescription().substring(0, 220) + "...");
                        }

                        return x;
                    })
                    .toList();
        model.addAttribute("cart", lib);
        model.addAttribute("page", pageNumber);
        return new ModelAndView("lib", model.asMap());
    }
}
