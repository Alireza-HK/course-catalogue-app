package com.example.catalogue.backend.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/soapui-doc")
@Hidden
public class SoapUiDocumentationController {

    @GetMapping
    @ResponseBody
    public Resource getSoapUiDocumentation() {
        return new ClassPathResource("static/soapui_documentation.html");
    }
}