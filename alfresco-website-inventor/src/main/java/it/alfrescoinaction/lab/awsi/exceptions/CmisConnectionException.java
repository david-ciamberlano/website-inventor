package it.alfrescoinaction.lab.awsi.exceptions;

import org.apache.chemistry.opencmis.commons.exceptions.CmisBaseException;

public class CmisConnectionException extends CmisBaseException {

    @Override
    public String getExceptionName() {
        return "Repository not found";
    }
}
