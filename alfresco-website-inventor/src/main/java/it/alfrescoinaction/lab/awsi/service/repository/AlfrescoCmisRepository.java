package it.alfrescoinaction.lab.awsi.service.repository;

import it.alfrescoinaction.lab.awsi.service.RemoteConnection;
import org.apache.chemistry.opencmis.client.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.NoSuchElementException;


@Repository
public class AlfrescoCmisRepository implements CmisRepository {

    @Autowired
    private RemoteConnection connection;

    private String siteName;


    @Override
    public ItemIterable<CmisObject> getCategories() {
        Session session = connection.getSession();

        String alfrescoHomePath = "/Sites/" + siteName + "/documentLibrary";
        CmisObject obj = session.getObjectByPath(alfrescoHomePath);

        // procceed only if the node is a folder
        if (obj.getType().getId().equals("cmis:folder")){
            Folder folder = (Folder)obj;
            ItemIterable<CmisObject> children = folder.getChildren();

            return children;
        }
        else {
            throw new NoSuchElementException("Home folder not found: " + alfrescoHomePath);
        }
    }

    @Override
    public Folder getFolderById(String id) throws NoSuchElementException {
        Session session = connection.getSession();

        if (id.equals("home")) {
            String alfrescoHomePath = "/Sites/" + siteName + "/documentLibrary";
            id = session.getObjectByPath(alfrescoHomePath).getId();
        }

        CmisObject obj = session.getObject(id);

        // procceed only if the node is a folder
        if (obj.getType().getId().equals("cmis:folder")){
            Folder folder = (Folder)obj;
            return folder;
        }
        else {
            throw new NoSuchElementException("Folder not found: "  + id);
        }
    }

    @Override
    public String getFolderIdByPath(String path) throws NoSuchElementException {
        Session session = connection.getSession();

        String fullPath = "/Sites/" + siteName + "/documentLibrary/" +path;
        CmisObject obj = session.getObjectByPath(fullPath);

        // procceed only if the node is a folder
        if (obj.getType().getId().equals("cmis:folder")){
            return obj.getId();
        }
        else {
            throw new NoSuchElementException("Folder not found: "  + path);
        }

    }

    @Override
    public Document getDocumentById(String id) throws NoSuchElementException {
        Session session = connection.getSession();

        CmisObject obj = session.getObject(id);

        if (obj.getType().getId().equals("cmis:document") || obj.getType().getId().equals("D:cm:thumbnail")){
            Document doc = (Document)obj;
            return doc;
        }
        else {
            throw new NoSuchElementException("Document not found: "  + id);
        }
    }

    @Override
    public ItemIterable<CmisObject> getChildren (Folder folder) {
        Session session = connection.getSession();
        OperationContext oc = session.createOperationContext();
        oc.setRenditionFilterString("*");
        ItemIterable<CmisObject> children = folder.getChildren(oc);

        return children;
    }


    //***** getter/setter *****
    @Override
    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }
}
