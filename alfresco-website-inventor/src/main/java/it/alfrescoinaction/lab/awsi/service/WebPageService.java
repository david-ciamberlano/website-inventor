package it.alfrescoinaction.lab.awsi.service;

import it.alfrescoinaction.lab.awsi.domain.*;
import it.alfrescoinaction.lab.awsi.domain.ecm.WSIFolder;
import it.alfrescoinaction.lab.awsi.exceptions.ObjectNotFoundException;
import it.alfrescoinaction.lab.awsi.repository.WSIRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WebPageService {

    private WSIRepository wsiRepository;

    @Autowired
    public WebPageService (WSIRepository wsiRepository) {
        this.wsiRepository = wsiRepository;
    }

    /**
     * Build the domain object representing a webpage
     * @param id the id of the page to build
     * @return the WebPage object
     * @throws ObjectNotFoundException
     */
    public WebPage  buildWebPage(String siteId, String id) throws ObjectNotFoundException {

        wsiRepository.init(siteId);

        WSIFolder folder = wsiRepository.getFolderById(id);
        String folderPath = folder.getPath();
        boolean isHomepage = wsiRepository.isHomePage(folderPath);

        WebPage wp = new WebPage(id, folder.getName(), folder.getParentId(), isHomepage,
                wsiRepository.getSiteName(), wsiRepository.getSiteTitle(), wsiRepository.getSiteDescription());

        wp.setSiteProperties(wsiRepository.getSiteProperties());

        Map<String, String> breadCrumbs = new LinkedHashMap<>();
        if (!isHomepage) {
            // breadcrumbs
            String relativeFolderPath = folderPath.replace(wsiRepository.getAlfrescoDocLibPath() + "/", "");
            String[] pathItems = relativeFolderPath.split("(?=/)");

            StringBuilder pathAcc = new StringBuilder();
            for (String pathItem : pathItems) {
                pathAcc.append(pathItem);
                String bcName = pathItem.startsWith("/") ? pathItem.substring(1) : pathItem;
                String currentPathId = wsiRepository.getFolderIdByRelativePath(pathAcc.toString());
                breadCrumbs.put(bcName, currentPathId);
            }

            // the last item is not part of breadcrumbs
            String lastItem = pathItems[pathItems.length - 1].startsWith("/") ? pathItems[pathItems.length - 1].substring(1) : pathItems[pathItems.length - 1];
            breadCrumbs.remove(lastItem);
        }

        wp.setBreadcrumbs(breadCrumbs);

        // get the links
        wp.setLinks(wsiRepository.getChildrenFolders(folder));

        Map<String,Content> contents = wsiRepository.getChildrenDocuments(folder);
        List<Content> genericContents = contents.entrySet().stream()
                .filter(e -> e.getKey().startsWith("content")).map(p->p.getValue()).collect(Collectors.toList());
        wp.setContents(genericContents);

        Map<String, Content> specialContents = contents.entrySet().stream()
                .filter(e -> !e.getKey().startsWith("content"))
                .collect(Collectors.toMap(p->p.getKey(),p->p.getValue()));
        wp.setSpecialContents(specialContents);

        // categories
        List<Link> categoryList = wsiRepository.getCategories();
        wp.setCategories(categoryList);

        return wp;
    }


    public String getPageIdByPath(String path) {
        return wsiRepository.getFolderIdByRelativePath(path);
    }


    public Downloadable<InputStream> getDownloadable(String id) {
        return wsiRepository.getDownloadable(id);
    }

    public Downloadable<byte[]> getRendition(String type, String objectId) {
        return wsiRepository.getRendition(type,objectId);
    }


}
