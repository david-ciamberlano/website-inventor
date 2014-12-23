package it.alfrescoinaction.lab.awsi.controller;


import it.alfrescoinaction.lab.awsi.domain.WebPage;
import it.alfrescoinaction.lab.awsi.service.repository.AlfrescoCmisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MainController {

    @Autowired
    AlfrescoCmisRepository alfrescoCmisRepository;

    @RequestMapping(value="/", method = RequestMethod.GET)
    public String homepage (Model model) {

        WebPage wp = alfrescoCmisRepository.buildWebPage("home");
        model.addAttribute("childPages", wp.getChildPages());
        model.addAttribute("parentPath", wp.getParentId());

        return "page";
    }

    @RequestMapping(value="/p/{id}", method = RequestMethod.GET)
    public String page (Model model, @PathVariable("id") String id) {

        WebPage wp = alfrescoCmisRepository.buildWebPage(id);
        model.addAttribute("childPages", wp.getChildPages());
        model.addAttribute("contents", wp.getContents());
        model.addAttribute("parentId", wp.getParentId());

        return "page";
    }



}
