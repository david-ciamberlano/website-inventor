package it.alfrescoinaction.lab.awsi.service.repository;


import it.alfrescoinaction.lab.awsi.domain.WebPage;


public interface CmisRepository {

    WebPage buildWebPage (String path);

}
