package com.moklyak.ecommerce.controllers;


import com.moklyak.ecommerce.dtos.AddItemDTO;
import com.moklyak.ecommerce.entities.Item;
import com.moklyak.ecommerce.entities.Order;
import com.moklyak.ecommerce.entities.Tag;
import com.moklyak.ecommerce.entities.User;
import com.moklyak.ecommerce.repositories.ItemRepository;
import com.moklyak.ecommerce.repositories.OrderRepository;
import com.moklyak.ecommerce.repositories.TagRepository;
import com.moklyak.ecommerce.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static Path WORKING_DIR;
    {
        try {
            WORKING_DIR = Paths.get(AdminController.class.getClassLoader().getResource("").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }
    public static final String PUBLIC_UPLOAD_DIR = "./uploads/";
    public static final String CONTENT_UPLOAD_DIR = "./contents/";
    private final int USER_PAGE_SIZE = 20;

    private final int ORDER_PAGE_SIZE = 20;
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private OrderRepository orderRepo;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private ItemRepository itemRepository;


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
    public ModelAndView addItemForm(Model model, AddItemDTO addItemDTO){
        if (addItemDTO == null){
            addItemDTO = new AddItemDTO();
        }
        model.addAttribute("addItemDTO", addItemDTO);
        return new ModelAndView("adminItemForm", model.asMap());
    }

    @PostMapping("/addItem")
    public ModelAndView addItem(Model model, AddItemDTO addItemDTO, @RequestParam("poster") MultipartFile poster, @RequestParam("contentItself") MultipartFile file) throws Exception{

        String picFileName = StringUtils.cleanPath(poster.getOriginalFilename());
        String contFileName = StringUtils.cleanPath(file.getOriginalFilename());
        if ("".equals(picFileName) || "".equals(contFileName)){
            model.addAttribute("error", "add poster or content files");
            return  new ModelAndView("adminItemForm", model.asMap());
        }
        Path path1;
        Path path2;
        try {
            path1 = WORKING_DIR.resolve(PUBLIC_UPLOAD_DIR + Instant.now().getEpochSecond() + picFileName);
            path2 = WORKING_DIR.resolve(CONTENT_UPLOAD_DIR + Instant.now().getEpochSecond() + contFileName);
            Files.write(path1, poster.getBytes());
            Files.write(path2, file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "add poster or content files");
            return  new ModelAndView("adminItemForm", model.asMap());
        }

        Item item = new Item();
        item.setName(addItemDTO.getName());
        item.setCreator(addItemDTO.getCreator());
        item.setDescription(addItemDTO.getDescription());
        item.setAdditionalInfo(addItemDTO.getAdditionalInfo());
        item.setReleaseDate(Date.valueOf(LocalDate.parse(addItemDTO.getReleaseDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        String posterPath = path1.toUri().toString().substring(path1.toUri().toString().lastIndexOf("/"));
        posterPath = URLDecoder.decode(posterPath, StandardCharsets.UTF_8.name());
        item.setPosterURI(posterPath);
        String filePath = path2.toUri().toString().substring(path2.toUri().toString().lastIndexOf("/"));
        filePath = URLDecoder.decode(filePath, StandardCharsets.UTF_8.name());
        item.setFileURI(filePath);
        item.setPrice(addItemDTO.getPrice());
        List<Tag> tags = new ArrayList<>();
        for (String tagStr : addItemDTO.getTags().split(",")){
            String tagName = tagStr.trim();
            Tag currTag = tagRepository.findByName(tagName);
            if (currTag == null){
                currTag = new Tag();
                currTag.setName(tagName);
                currTag = tagRepository.save(currTag);
                currTag.setItems(new ArrayList<>());

            }
            currTag.getItems().add(item);
            tags.add(currTag);

        }
        item.setTags(tags);
        item = itemRepository.save(item);


        model.addAttribute("error", "No error, Item added. Item id: %s".formatted(item.getId().toString()));
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

    @GetMapping("")
    public ModelAndView adminNavigator(Model model){

        return new ModelAndView("admin", model.asMap());
    }
}
