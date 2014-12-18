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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

@Controller
public class MainController {

    @Autowired
    AlfrescoCmisRepository alfrescoCmisRepository;

    @RequestMapping("/")
    public String homepage (Model model) {

        // the parent of / is itself
        WebPage wp = alfrescoCmisRepository.buildWebPage("/");
        model.addAttribute("webPage", wp);

        return "page";
    }

    @RequestMapping(value = "/p", method = RequestMethod.GET)
    public String page (Model model, @RequestParam("path") String path) {

        WebPage wp = alfrescoCmisRepository.buildWebPage(path);
        model.addAttribute("childPages", wp.getChildPages());
        model.addAttribute("parentPath", wp.getParentPath());

        return "page";
    }



}
