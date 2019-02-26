package com.project.opticost.controller;

import com.project.opticost.model.City;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
public class MainController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String showMainScreen(Model model) {
        return "index";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String saveCities(List<City> cities, Model model) {
        return "index";
    }
}
