package it.alfrescoinaction.lab.awsi.controller;


import it.alfrescoinaction.lab.awsi.domain.Downloadable;
import it.alfrescoinaction.lab.awsi.domain.WebPage;
import it.alfrescoinaction.lab.awsi.service.WebPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletResponse;
import java.io.IOException;

@Controller
public class MainController {

    @Autowired
    WebPageService webPageService;

    @RequestMapping(value="/{sitename}", method = RequestMethod.GET)
    public String homepage(Model model, @PathVariable("sitename") String site) {
        return "forward:/" + site + "/o/home";
    }

    @RequestMapping(value="/{sitename}/o/{id}", method = RequestMethod.GET)
    public String pageById(Model model, @PathVariable("sitename") String siteName, @PathVariable("id") String id) {
        WebPage wp = webPageService.buildWebPage(siteName, id);
        model.addAttribute("page", wp);
        model.addAttribute("site", siteName);

        String view = "genericpage";
        if (wp.isHomepage()) {
//            view = "homepage";
        }

        return view;
    }

//    @RequestMapping(value = "/{sitename}/p/{path}", method = RequestMethod.GET)
//    public String pageByPath(Model model, @PathVariable("sitename") String siteName, @PathVariable("path") String path) {
//
//        String decodedPath = path.replaceAll("\\|","/");
//        String pageId = webPageService.getPageIdByPath(decodedPath);
//
//        return "forward:/" + siteName + "/d/" + pageId;
//    }

    @RequestMapping(value = "proxy/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<InputStreamResource> get(ServletResponse response, @PathVariable("id") String id) throws IOException {
        Downloadable downloadable = webPageService.getDownloadable(id);

        return ResponseEntity.ok()
            .header("content-disposition", "inline; filename=\"" + downloadable.getName() + "\"")
            .contentLength(downloadable.getContentLength())
            .contentType(MediaType.parseMediaType(downloadable.getMimeType()))
            .body(new InputStreamResource(downloadable.getStream()));
    }

}
