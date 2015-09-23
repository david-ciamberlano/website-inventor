package it.alfrescoinaction.lab.awsi.controller;


import it.alfrescoinaction.lab.awsi.domain.Downloadable;
import it.alfrescoinaction.lab.awsi.domain.SearchFilters;
import it.alfrescoinaction.lab.awsi.domain.WebPage;
import it.alfrescoinaction.lab.awsi.service.WebPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {

    @Autowired
    WebPageService webPageService;

    @RequestMapping("/{sitename}")
    public String homepage(Model model, @PathVariable("sitename") String site) {
        return "forward:/" + site + "/page/home";
    }

    @RequestMapping("/{sitename}/page/{id}")
    public String pageById( @ModelAttribute("searchFilters") SearchFilters searchFilters, Model model,
                            @PathVariable("sitename") String siteName, @PathVariable("id") String id) {
        WebPage wp = webPageService.buildWebPage(siteName, id);
        model.addAttribute("page", wp);
        model.addAttribute("site", siteName);

        String view = "index";
        if (wp.isHomepage()) {
            view = "index";
        }

        return view;
    }

    @RequestMapping(value = "/{sitename}/search", method = RequestMethod.POST)
    public String search( @ModelAttribute("searchFilters") SearchFilters searchFilters, Model model,
                          @PathVariable("sitename") String siteName) {
        List<String> filters = new ArrayList<>();
        filters.add(searchFilters.getFilter1());
        filters.add(searchFilters.getFilter2());
        filters.add(searchFilters.getFilter3());
        filters.add(searchFilters.getFilter4());
        filters.add(searchFilters.getFilter5());
        filters.add(searchFilters.getFilter6());

        WebPage wp = webPageService.buildSearchResultPage(siteName, filters);

        model.addAttribute("page", wp);
        model.addAttribute("site", siteName);

        String view = "themes/sena/searchresult";

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


    //------------- PRIVATE -------------

}
