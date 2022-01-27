package com.hitachi.kioskdesk.controller;

import com.hitachi.kioskdesk.domain.Product;
import com.hitachi.kioskdesk.domain.ProductModel;
import com.hitachi.kioskdesk.domain.User;
import com.hitachi.kioskdesk.enums.Status;
import com.hitachi.kioskdesk.helper.Message;
import com.hitachi.kioskdesk.repository.ProductModelRepository;
import com.hitachi.kioskdesk.repository.ProductRepository;
import com.hitachi.kioskdesk.repository.UserRepository;
import com.hitachi.kioskdesk.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Shiva Created on 04/01/22
 */
@Slf4j
@Controller
@RequestMapping("/admin/")
public class AdminController {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductModelRepository productModelRepository;

    @RequestMapping(value = "/products", method = RequestMethod.GET)
    public String listProducts(
            Model model,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(20);

        Page<Product> productPage = productService.findPaginated(PageRequest.of(currentPage - 1, pageSize), null);

        model.addAttribute("productPage", productPage);
        model.addAttribute("products", productPage.getContent());

        int totalPages = productPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "admin-products";
    }


    @RequestMapping("users")
    public String users(Model model) {
        model.addAttribute("title", "Kiosk - Users List");
        model.addAttribute("users", userRepository.findAll());
        return "admin-users";
    }

    @RequestMapping(value = "/product/complete", method = RequestMethod.GET)
    public String completeProduct(@Param(value = "id") Long id, HttpSession session, Principal principal) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        try {
            Product product = optionalProduct.get();
            product.setStatus(Status.COMPLETED);
            productRepository.save(product);
            log.info("ADMIN with ID {}", principal.getName());
            log.info("Admin completed product with ID {}", id);
            session.setAttribute("message", new Message("Product Status Updated for " + id, "alert-success"));
            return "redirect:/admin/products";
        } catch (NoSuchElementException ex) {
            log.info("ADMIN with ID {}", principal.getName());
            log.error("Product not found ", ex);
            session.setAttribute("message", new Message("Product Not Found with ID " + id, "alert-danger"));
            return "error/404";
        }
    }

    @RequestMapping(value = "/product/delete", method = RequestMethod.GET)
    public String deleteProduct(@Param(value = "id") Long id, HttpSession session, Principal principal) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        try {
            Product product = optionalProduct.get();
            productRepository.delete(product);
            log.info("ADMIN with ID {}", principal.getName());
            log.info("Admin deleted product with ID {}", id);
            session.setAttribute("message", new Message("Product deleted successfully with ID" + id, "alert-success"));
            return "redirect:/admin/products";
        } catch (NoSuchElementException ex) {
            log.error("Product not found ", ex);
            session.setAttribute("message", new Message("Product Not Found with ID " + id, "alert-danger"));
            return "error/404";
        }
    }

    @RequestMapping(value = "/user/edit", method = RequestMethod.GET)
    public String userEdit(@Param(value = "id") Long id, HttpSession session, Model model) {
        Optional<User> optionalProduct = userRepository.findById(id);
        try {
            User user = optionalProduct.get();
            model.addAttribute("u", user);
            return "user-edit";
        } catch (NoSuchElementException ex) {
            log.error("User not found ", ex);
            session.setAttribute("message", new Message("User Not Found with ID " + id, "alert-danger"));
            return "error/404";
        }
    }

    @PostMapping("/updateUserDetails")
    public String updateUserDetails(@Valid @ModelAttribute("u") User user, BindingResult result,
                                    HttpSession session, Model model, Principal principal) {

        try {
            if (result.hasErrors()) {
                log.info("ADMIN with ID {}", principal.getName());
                log.error("Validation error while saving user {}", result);
                model.addAttribute("u", user);
                return "user-edit";
            }
            User savedUser = userRepository.findById(user.getId()).get();
            savedUser.setRole(user.getRole());
            savedUser.setEmail(user.getEmail());
            if (user.getPassword() != null) {
                PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                savedUser.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            log.info("ADMIN with ID {}", principal.getName());
            log.info("Admin updated user with email {}", user.getEmail());
            userRepository.save(savedUser);
            session.setAttribute("message", new Message("Successfully Updated User", "alert-success"));
            return "redirect:/admin/users";
        } catch (Exception ex) {
            log.info("ADMIN with ID {}", principal.getName());
            log.error("Error while updating user", ex);
            session.setAttribute("message", new Message("Something went wrong!! " + ex.getMessage(), "alert-danger"));
            return "redirect:/admin/users";
        }
    }

    @RequestMapping(value = "/user/delete", method = RequestMethod.GET)
    public String deleteUser(@Param(value = "id") Long id, HttpSession session, Principal principal) {
        Optional<User> optionalUser = userRepository.findById(id);
        try {
            User user = optionalUser.get();
            userRepository.delete(user);
            log.info("ADMIN with ID {}", principal.getName());
            log.info("Admin deleted user with ID {}", id);
            session.setAttribute("message", new Message("User deleted successfully with ID" + id, "alert-success"));
            return "redirect:/admin/users";
        } catch (NoSuchElementException ex) {
            log.error("User not found ", ex);
            session.setAttribute("message", new Message("User Not Found with ID " + id, "alert-danger"));
            return "error/404";
        }
    }

    @RequestMapping("/addUser")
    public String addUser(Model model) {
        model.addAttribute("title", "Kiosk - Create User");
        model.addAttribute("u", new User());
        return "addUser";
    }

    @PostMapping("/saveNewUser")
    public String saveNewUser(@Valid @ModelAttribute("u") User user, BindingResult result,
                              HttpSession session, Model model, Principal principal) {

        try {
            User savedUser = userRepository.findByEmail(user.getEmail().trim());
            if(savedUser != null){
                log.info("ADMIN with ID {}", principal.getName());
                log.error("Validation error while saving user {}", result);
                model.addAttribute("u", user);
                result.rejectValue("email", "error.email", "Email already present!");
                return "addUser";
            }
            if (result.hasErrors()) {
                log.info("ADMIN with ID {}", principal.getName());
                log.error("Validation error while saving user {}", result);
                model.addAttribute("u", user);
                return "addUser";
            }
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            user.setEnabled(true);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            log.info("ADMIN with ID {}", principal.getName());
            log.info("Admin added user with email {}", user.getEmail());
            session.setAttribute("message", new Message("Successfully Added User", "alert-success"));
            return "redirect:/admin/users";
        } catch (Exception ex) {
            log.info("ADMIN with ID {}", principal.getName());
            log.error("Error while saving user", ex);
            session.setAttribute("message", new Message("Something went wrong!! " + ex.getMessage(), "alert-danger"));
            return "addUser";
        }
    }

    @RequestMapping("/addModel")
    public String addModel(Model model) {
        model.addAttribute("title", "Kiosk - Create Model");
        model.addAttribute("productModel", new ProductModel());
        return "addModel";
    }

    @PostMapping("/saveNewModel")
    public String saveNewModel(@Valid @ModelAttribute("productModel") ProductModel productModel, BindingResult result,
                              HttpSession session, Model model, Principal principal) {

        try {
            ProductModel savedModel = productModelRepository.findByModelName(productModel.getModelName());
            if(savedModel != null){
                log.info("ADMIN with ID {}", principal.getName());
                log.error("Validation error while saving model {}", result);
                model.addAttribute("productModel", productModel);
                result.rejectValue("modelName", "error.modelName", "Model already present!");
                return "addModel";
            }
            if (result.hasErrors()) {
                log.info("ADMIN with ID {}", principal.getName());
                log.error("Validation error while saving model {}", result);
                model.addAttribute("productModel", productModel);
                return "addUser";
            }
            productModelRepository.save(productModel);
            log.info("ADMIN with ID {}", principal.getName());
            log.info("Admin added Model with name {}", productModel.getModelName());
            session.setAttribute("message", new Message("Successfully Added Model", "alert-success"));
            return "redirect:/admin/models";
        } catch (Exception ex) {
            log.info("ADMIN with ID {}", principal.getName());
            log.error("Error while saving model", ex);
            session.setAttribute("message", new Message("Something went wrong!! " + ex.getMessage(), "alert-danger"));
            return "addModel";
        }
    }

    @RequestMapping("models")
    public String productModels(Model model) {
        model.addAttribute("title", "Kiosk - Models List");
        model.addAttribute("productModels", productModelRepository.findAll());
        return "admin-models";
    }

    @RequestMapping(value = "/model/delete", method = RequestMethod.GET)
    public String deleteModel(@Param(value = "id") Long id, HttpSession session, Principal principal) {
        Optional<ProductModel> optionalProductModel = productModelRepository.findById(id);
        try {
            ProductModel productModel = optionalProductModel.get();
            productModelRepository.delete(productModel);
            log.info("ADMIN with ID {}", principal.getName());
            log.info("Admin deleted model with id {}", id);
            session.setAttribute("message", new Message("Model deleted successfully with ID" + id, "alert-success"));
            return "redirect:/admin/models";
        } catch (NoSuchElementException ex) {
            log.info("ADMIN with ID {}", principal.getName());
            log.error("Model not found ", ex);
            session.setAttribute("message", new Message("Model Not Found with ID " + id, "alert-danger"));
            return "error/404";
        }
    }





}
