package it.alfrescoinaction.lab.awsi.controller;


import it.alfrescoinaction.lab.awsi.domain.Downloadable;
import it.alfrescoinaction.lab.awsi.domain.SearchFilters;
import it.alfrescoinaction.lab.awsi.domain.WebPage;
import it.alfrescoinaction.lab.awsi.exceptions.ConnectionException;
import it.alfrescoinaction.lab.awsi.exceptions.PageNotFoundException;
import it.alfrescoinaction.lab.awsi.service.WebPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {

    @Autowired
    WebPageService webPageService;

    @Value("${theme.home_template}")
    private String homeTemplate;

    @Value("${theme.page_template}")
    private String pageTemplate;

    @Value("${theme.searchresult_template}")
    private String searchResultTemplate;

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

        String view = pageTemplate;
        if (wp.isHomepage()) {
            view = homeTemplate;
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

        String view = searchResultTemplate;

        return view;
    }

    @RequestMapping(value = "proxy/d/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<InputStreamResource> download(ServletResponse response, @PathVariable("id") String id) throws IOException {
        Downloadable<InputStream> fileDownloadable = webPageService.getDownloadable(id);

        return ResponseEntity.ok()
            .header("content-disposition", "inline; filename=\"" + fileDownloadable.getName() + "\"")
            .contentLength(fileDownloadable.getContentLength())
            .contentType(MediaType.parseMediaType(fileDownloadable.getMimeType()))
            .body(new InputStreamResource(fileDownloadable.getContent()));
    }


    @RequestMapping(value = "proxy/r/{type}/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<byte[]> rendition(ServletResponse response,
                                     @PathVariable("type") String type,
                                     @PathVariable("id") String id) throws IOException {

        Downloadable<byte[]> rend;
        switch(type) {
            case "thumb": {
                rend = webPageService.getRendition("doclib", id);
                break;
            }

            case "preview": {
                rend = webPageService.getRendition("imgpreview", id);
                break;
            }

            default:
                rend = webPageService.getRendition(type, id);
        }

        return ResponseEntity.ok()
                .contentLength(rend.getContentLength())
                .contentType(MediaType.parseMediaType(rend.getMimeType()))
                .body(rend.getContent());

    }


    //------------- EXCEPTION -------------

    @ExceptionHandler(PageNotFoundException.class)
    public ModelAndView handlePageNotFoundError(HttpServletRequest req, PageNotFoundException exc) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("Invalid Page", exc.getPageId());
        mav.addObject("exception", exc);
        mav.addObject("utl",req.getRequestURL());
        mav.setViewName("themes/simple/pageNotFound");

        return mav;
    }

    @ExceptionHandler(ConnectionException.class)
    public ModelAndView handleConnectionError(HttpServletRequest req, PageNotFoundException exc) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("Connection exception", exc.getPageId());
        mav.addObject("exception", exc);
        mav.addObject("utl",req.getRequestURL());
        mav.setViewName("themes/simple/pageNotFound");

        return mav;
    }



    //------------- PRIVATE -------------

}