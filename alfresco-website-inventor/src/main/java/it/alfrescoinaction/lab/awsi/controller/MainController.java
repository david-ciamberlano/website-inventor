package it.alfrescoinaction.lab.awsi.controller;


import it.alfrescoinaction.lab.awsi.domain.WebPage;
import it.alfrescoinaction.lab.awsi.service.RemoteConnection;
import it.alfrescoinaction.lab.awsi.service.repository.AlfrescoCmisRepository;
import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.impl.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

@Controller
public class MainController {

    @Autowired
    AlfrescoCmisRepository alfrescoCmisRepository;

    @RequestMapping("/test")
    public String test (Model model) {

        WebPage wp = alfrescoCmisRepository.buildWebPage("/Sites/lab/documentLibrary/homepage");
        model.addAttribute("hello",wp.getTitle());
        model.addAttribute("test", wp.getPath() );

        return "test";
    }

    @RequestMapping("/p/{path}")
    public String page (Model model, @PathVariable("path") String path) {

        model.addAttribute(path);

        return "test";
    }



}
