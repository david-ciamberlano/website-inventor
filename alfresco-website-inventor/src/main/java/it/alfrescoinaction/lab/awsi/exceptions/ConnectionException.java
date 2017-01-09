package it.alfrescoinaction.lab.awsi.exceptions;

import org.apache.chemistry.opencmis.commons.exceptions.CmisBaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason="Connection to repository error.")
public class ConnectionException extends CmisBaseException {

    private static final long  serialVersionUID = 1231443589693L;

    @Override
    public String getExceptionName() {
        return "CmisRepository not found";
    }

}
