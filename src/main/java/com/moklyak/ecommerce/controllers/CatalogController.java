package com.moklyak.ecommerce.controllers;

import com.moklyak.ecommerce.entities.Item;
import com.moklyak.ecommerce.entities.Tag;
import com.moklyak.ecommerce.repositories.ItemRepository;
import com.moklyak.ecommerce.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/")
public class CatalogController {

    @Autowired
    private ItemRepository itemRepository;

    public int PAGE_SIZE = 20;
    @Autowired
    private TagRepository tagRepository;

    @GetMapping
    public ModelAndView index(Model model, @Nullable @RequestParam Integer pageNumber, @Nullable @RequestParam String tags){
        if (pageNumber == null){
            pageNumber = 0;

        }
        if (tags == null){
            tags = "";
        }

        List<Tag> filter = Arrays.stream(tags.split(","))
                .map(x-> x.trim())
                .map(x-> tagRepository.findByName(x))
                .filter(x->x!=null)
                .collect(Collectors.toList());

        List<Item> page;
        if (filter.size() == 0){
            page = itemRepository.findAll(Pageable.ofSize(PAGE_SIZE)
                            .withPage(pageNumber))
                    .stream()
                    .map(x-> {
                        x.setPosterURI(x.getPosterURI().substring(x.getPosterURI().lastIndexOf("/")));
                        return x;
                    })
                    .toList();
        } else {
            page = itemRepository.findDistinctByTagsIn(filter, Pageable.ofSize(PAGE_SIZE)
                            .withPage(pageNumber))
                    .stream()
                    .map(x-> {
                        x.setPosterURI(x.getPosterURI().substring(x.getPosterURI().lastIndexOf("/")));
                        return x;
                    })
                    .toList();
        }


        model.addAttribute("lastPage", (itemRepository.count() - 1)/PAGE_SIZE == pageNumber);
        model.addAttribute("page", pageNumber);
        model.addAttribute("items", page);
        model.addAttribute("tags", tags);

        return new ModelAndView("index", model.asMap());
    }

    @GetMapping(value = "/", params = "inc")
    public ModelAndView incrementPage(Model model, @Nullable @RequestParam Integer pageNumber, @Nullable @RequestParam String tags){
        if (pageNumber == null){
            pageNumber = 0;
        }
        pageNumber++;
        return index(model, pageNumber, tags);
    }
    @GetMapping(value = "/", params = "dec")
    public ModelAndView decrementPage(Model model, @Nullable @RequestParam Integer pageNumber, @Nullable @RequestParam String tags){
        if (pageNumber == null){
            pageNumber = 0;
        }
        pageNumber--;
        return index(model, pageNumber, tags);
    }
}
