package com.hitachi.kioskdesk.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
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
            String nameOf = authentication.getName();
            if (isAdmin) {
                log.info("ADMIN Login {}", nameOf);
                return "redirect:/admin/products";
            } else if (isOperator) {
                log.info("OPERATOR Login {}", nameOf);
                return "redirect:/operator/new-item";
            } else if (isQC) {
                log.info("QC Login {}", nameOf);
                return "redirect:/qc/scan";
            }
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String customLogin(Model model) {
        model.addAttribute("title", "Login Page");
        return "login";
    }

    @GetMapping("/customLogout")
    public String customLogout(HttpServletRequest request) {
        new SecurityContextLogoutHandler().logout(request, null, null);
        log.info("LOGGED OUT on to tab/window close");
        return "redirect:/login";
    }
}
