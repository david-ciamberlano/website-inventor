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

@Controller
public class MainController {

    @Autowired
    WebPageService webPageService;

    private final String homeTemplate = "page";
    private final String pageTemplate = "page";
    private String searchResultTemplate = "searchresult";
    @Value("${alfresco.search.filter1}") String filter1;
    @Value("${alfresco.search.filter2}") String filter2;

//    @ModelAttribute("searchFilters")
//    public SearchFilters buildSearchFilter(
//            @Value("${alfresco.search.filter1}") String filter1,
//            @Value("${alfresco.search.filter2}") String filter2) {
//
//        String[] filter1Parts = filter1.split("\\|");
//        String[] filter2Parts = filter2.split("\\|");
//        //TODO check for null
//        SearchFilters searchFilters = new SearchFilters(filter1Parts[0],filter1Parts[1],filter1Parts[2],
//                filter2Parts[0],filter2Parts[1],filter2Parts[2]);
//
//        return searchFilters;
//
//    }

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

        String[] filter1Parts = filter1.split("\\|");
        String[] filter2Parts = filter2.split("\\|");
        //TODO check for null
        searchFilters.setFilter1Data(filter1Parts[0],filter1Parts[1],filter1Parts[2]);
        searchFilters.setFilter2Data(filter2Parts[0],filter2Parts[1],filter2Parts[2]);

        WebPage wp = webPageService.buildSearchResultPage(siteName, searchFilters);

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