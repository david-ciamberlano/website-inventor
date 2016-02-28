package it.alfrescoinaction.lab.awsi.controller;

import com.sun.jndi.toolkit.dir.SearchFilter;
import it.alfrescoinaction.lab.awsi.domain.SearchFilterItem;
import it.alfrescoinaction.lab.awsi.domain.SearchFilters;
import it.alfrescoinaction.lab.awsi.domain.SiteProperty;
import it.alfrescoinaction.lab.awsi.domain.WebPage;
import it.alfrescoinaction.lab.awsi.exceptions.ConnectionException;
import it.alfrescoinaction.lab.awsi.exceptions.InvalidParameterException;
import it.alfrescoinaction.lab.awsi.exceptions.PageNotFoundException;
import it.alfrescoinaction.lab.awsi.service.WebPageService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;


@Controller
public class MainController {

    @Autowired
    private WebPageService webPageService;

    private final String homeTemplate = "page";
    private final String pageTemplate = "page";
    private final String searchResultTemplate = "searchresult";

    private static final Logger logger = Logger.getLogger(MainController.class);


    @RequestMapping("/{siteid}")
    public String homepage(Model model, @PathVariable("siteid") String site) {
        model.addAttribute("test","test");
        return "forward:/" + site + "/page/home";
    }

    @RequestMapping("/{siteid}/page/{id}")
    public String pageById(Model model,
                           HttpServletRequest request, @PathVariable("siteid") String siteId,
                           @PathVariable("id") String id) {

        if(logger.isDebugEnabled()){
            logger.debug("Requested page: " + id);
        }

        WebPage wp = webPageService.buildWebPage(siteId, id);
        model.addAttribute("page", wp);
        model.addAttribute("siteid", siteId);
        model.addAttribute("sitename", wp.getSiteName());
        model.addAttribute("sitedescription", wp.getSiteDescription());

        // init the model attribute for the form
        SearchFilters filters = new SearchFilters();

        for (SiteProperty sprop : wp.getSiteProperties().getSearchFields()){
            filters.addFilterItem(sprop.getLabel(),sprop.getPropertyId(),sprop.getType());
        }

        model.addAttribute("searchFilters", filters);

        String view = pageTemplate;
        if (wp.isHomepage()) {
            view = homeTemplate;
        }

        if(logger.isDebugEnabled()){
            logger.debug("Page Ready: " + id);
        }
        return view;
    }

    @RequestMapping(value = "/{siteid}/search", method = RequestMethod.POST)
    public String search( @ModelAttribute SearchFilters searchFilters, Model model,
                          @PathVariable("siteid") String siteId) {

        if(logger.isDebugEnabled()){
            logger.debug("Search Request");
        }

        WebPage wp = webPageService.buildSearchResultPage(siteId, searchFilters);

        model.addAttribute("page", wp);
        model.addAttribute("siteid", siteId);
        model.addAttribute("sitename", wp.getSiteName());
        model.addAttribute("sitedescription", wp.getSiteDescription());

        String view = searchResultTemplate;

        if(logger.isDebugEnabled()){
            logger.debug("Search Result ready");
        }

        return view;
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