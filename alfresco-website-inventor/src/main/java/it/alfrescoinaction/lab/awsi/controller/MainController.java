package it.alfrescoinaction.lab.awsi.controller;

import it.alfrescoinaction.lab.awsi.domain.PropertyTuple;
import it.alfrescoinaction.lab.awsi.domain.SearchFilters;
import it.alfrescoinaction.lab.awsi.domain.WebPage;
import it.alfrescoinaction.lab.awsi.exceptions.ConnectionException;
import it.alfrescoinaction.lab.awsi.exceptions.InvalidParameterException;
import it.alfrescoinaction.lab.awsi.exceptions.PageNotFoundException;
import it.alfrescoinaction.lab.awsi.service.WebPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {

    @Autowired
    private WebPageService webPageService;

    private final String homeTemplate = "page";
    private final String pageTemplate = "page";
    private final String searchResultTemplate = "searchresult";
    @Value("${alfresco.search.filter1}") private String filter1;
    @Value("${alfresco.search.filter2}") private String filter2;
    @Value("${alfresco.search.filter3}") private String filter3;
    @Value("${alfresco.search.filter4}") private String filter4;
    @Value("${alfresco.search.filter5}") private String filter5;

    @Value("${alfresco.document.property1}") String property1;
    @Value("${alfresco.document.property2}") String property2;
    @Value("${alfresco.document.property3}") String property3;



    @RequestMapping("/{siteid}")
    public String homepage(Model model, @PathVariable("siteid") String site) {
        return "forward:/" + site + "/page/home";
    }

    @RequestMapping("/{siteid}/page/{id}")
    public String pageById(Model model, @PathVariable("siteid") String siteId, @PathVariable("id") String id) {

        SearchFilters searchFilters = new SearchFilters();

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


    @ModelAttribute("searchFilters")
    public SearchFilters createSeachBean() {
        SearchFilters searchFilters = new SearchFilters();

        String[] filter1Parts = (filter1 != null? filter1.split("\\|"):new String[0]);
        String[] filter2Parts = (filter2 != null? filter2.split("\\|"): new String[0]);
        String[] filter3Parts = (filter3 != null? filter3.split("\\|"): new String[0]);
        String[] filter4Parts = (filter4 != null? filter4.split("\\|"): new String[0]);
        String[] filter5Parts = (filter5 != null? filter5.split("\\|"): new String[0]);

        // if lenght != 3, the filterData were left uninitialized ("","","")
        if (filter1Parts.length == 3) {
            searchFilters.addFilterItem(filter1Parts[0], filter1Parts[1], filter1Parts[2]);
        }

        if (filter2Parts.length == 3) {
            searchFilters.addFilterItem(filter2Parts[0], filter2Parts[1], filter2Parts[2]);
        }

        if (filter3Parts.length == 3) {
            searchFilters.addFilterItem(filter3Parts[0], filter3Parts[1], filter3Parts[2]);
        }

        if (filter4Parts.length == 3) {
            searchFilters.addFilterItem(filter4Parts[0], filter4Parts[1], filter4Parts[2]);
        }

        if (filter5Parts.length == 3) {
            searchFilters.addFilterItem(filter5Parts[0], filter5Parts[1], filter5Parts[2]);
        }

        return searchFilters;
    }



    //------------- EXCEPTION -------------

    @ExceptionHandler(PageNotFoundException.class)
    public ModelAndView handlePageNotFoundError(HttpServletRequest req, PageNotFoundException exc) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("Invalid Page", exc.getPageId());
        mav.addObject("exception", exc);
        mav.addObject("utl",req.getRequestURL());
        mav.setViewName("themes/s/error_page");

        return mav;
    }

    @ExceptionHandler(ConnectionException.class)
    public ModelAndView handleConnectionError(HttpServletRequest req, PageNotFoundException exc) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("Connection exception", exc.getPageId());
        mav.addObject("exception", exc);
        mav.addObject("utl",req.getRequestURL());
        mav.setViewName("themes/s/error_page");

        return mav;
    }

    @ExceptionHandler(InvalidParameterException.class)
    public ModelAndView handleInvParamException(HttpServletRequest req, InvalidParameterException exc) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("Connection exception", exc.getMessage());
        mav.addObject("exception", exc);
        mav.addObject("utl",req.getRequestURL());
        mav.setViewName("themes/s/error_page");

        return mav;
    }



    //------------- PRIVATE -------------


}