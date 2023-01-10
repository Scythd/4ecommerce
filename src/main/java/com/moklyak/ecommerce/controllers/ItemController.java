package com.moklyak.ecommerce.controllers;


import com.moklyak.ecommerce.entities.Item;
import com.moklyak.ecommerce.repositories.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.text.DateFormat;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/item")
public class ItemController {

    @Autowired
    private ItemRepository itemRepository;

    @GetMapping("/{id}")
    public ModelAndView get(Model model, @PathVariable Long id){
        Item i = itemRepository.findById(id).get();
        model.addAttribute("item", i);

        String tagString;

        tagString = i.getTags().stream().map(x->x.getName()).collect(Collectors.joining(", "));
        model.addAttribute("tagString", tagString);

        model.addAttribute("date", DateFormat.getDateInstance().format(i.getReleaseDate()));
        return new ModelAndView("item", model.asMap());
    }


}
