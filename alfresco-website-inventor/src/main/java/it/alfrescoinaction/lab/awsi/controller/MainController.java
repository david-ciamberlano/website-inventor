package it.alfrescoinaction.lab.awsi.controller;


import it.alfrescoinaction.lab.awsi.domain.Downloadable;
import it.alfrescoinaction.lab.awsi.domain.PropertyTuple;
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
    private WebPageService webPageService;

    private final String homeTemplate = "page";
    private final String pageTemplate = "page";
    private String searchResultTemplate = "searchresult";
    @Value("${alfresco.search.filter1}") private String filter1;
    @Value("${alfresco.search.filter2}") private String filter2;

    @Value("${alfresco.document.property1}") String property1;
    @Value("${alfresco.document.property2}") String property2;
    @Value("${alfresco.document.property3}") String property3;



    @RequestMapping("/{siteid}")
    public String homepage(Model model, @PathVariable("siteid") String site) {
        return "forward:/" + site + "/page/home";
    }

    @RequestMapping("/{siteid}/page/{id}")
    public String pageById( @ModelAttribute("searchFilters") SearchFilters searchFilters, Model model,
                            @PathVariable("siteid") String siteId, @PathVariable("id") String id) {
        //Init the search filters
        initSearchFilters(searchFilters);
        WebPage wp = webPageService.buildWebPage(siteId, id);
        model.addAttribute("page", wp);
        model.addAttribute("siteid", siteId);
        model.addAttribute("sitename", wp.getSiteName());
        model.addAttribute("sitedescription", wp.getSiteDescription());

        List<PropertyTuple> documentProps = new ArrayList<>(3);
        documentProps.add(new PropertyTuple(property1));
        documentProps.add(new PropertyTuple(property2));
        documentProps.add(new PropertyTuple(property3));

        model.addAttribute("documentProps", documentProps);

        String view = pageTemplate;
        if (wp.isHomepage()) {
            view = homeTemplate;
        }
        return view;
    }

    @RequestMapping(value = "/{siteid}/search", method = RequestMethod.POST)
    public String search( @ModelAttribute("searchFilters") SearchFilters searchFilters, Model model,
                          @PathVariable("siteid") String siteId) {
        initSearchFilters(searchFilters);

        WebPage wp = webPageService.buildSearchResultPage(siteId, searchFilters);

        model.addAttribute("page", wp);
        model.addAttribute("siteid", siteId);
        model.addAttribute("sitename", wp.getSiteName());
        model.addAttribute("sitedescription", wp.getSiteDescription());

        List<PropertyTuple> documentProps = new ArrayList<>(3);
        documentProps.add(new PropertyTuple(property1));
        documentProps.add(new PropertyTuple(property2));
        documentProps.add(new PropertyTuple(property3));

        model.addAttribute("documentProps", documentProps);

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

    private void initSearchFilters(SearchFilters searchFilters) {

        String[] filter1Parts;
        filter1Parts = (filter1 != null?  filter1.split("\\|"):new String[0]);
        String[] filter2Parts;
        filter2Parts = (filter2 != null? filter2.split("\\|"): new String[0]);

        // if lenght != 3, the filterData were left uninitialized ("","","")
        if (filter1Parts.length == 3) {
            searchFilters.setFilter1Data(filter1Parts[0], filter1Parts[1], filter1Parts[2]);
        }

        if (filter2Parts.length == 3) {
            searchFilters.setFilter2Data(filter2Parts[0], filter2Parts[1], filter2Parts[2]);
        }

    }
}