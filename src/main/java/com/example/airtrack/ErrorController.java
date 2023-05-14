package com.example.airtrack;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

        @PostMapping("/error")
        public String handleError(HttpServletRequest request) {
            String referer = request.getHeader("referer");
            if (referer != null && !referer.isEmpty()) {
                return "redirect:" + referer;
            }
            return "/general/login";
        }

        @GetMapping("/error")
        public String getErrorPath() {
            return "/general/error";
        }
}

