package org.storkforge.beatsbust.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "BeatsBust - Home");
        model.addAttribute("message", "Welcome to BeatsBust!");
        return "index";
    }

    @GetMapping("/home")
    public String homePage(Model model) {
        return home(model);
    }

    @GetMapping("/index")
    public String indexPage(Model model) {
        return home(model);
    }
}
