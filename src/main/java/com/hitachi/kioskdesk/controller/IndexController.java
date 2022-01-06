package com.hitachi.kioskdesk.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;

/**
 * Shiva Created on 05/01/22
 */
@Slf4j
@Controller
public class IndexController {

    @RequestMapping("/")
    public String index(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated()) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            boolean isAdmin = authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
            boolean isOperator = authorities.contains(new SimpleGrantedAuthority("ROLE_OPERATOR"));
            boolean isQC = authorities.contains(new SimpleGrantedAuthority("ROLE_QC"));
            if (isAdmin) {
                return "redirect:/admin/products";
            } else if (isOperator) {
                return "redirect:/operator/new-item";
            } else if (isQC) {
                return "qc_home";
            }
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String customLogin(Model model) {
        model.addAttribute("title", "Login Page");
        return "login";
    }
}
