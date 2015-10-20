package it.alfrescoinaction.lab.awsi.exceptions;

public class InvalidParameterException extends RuntimeException {
    private static final long serialVersionUID = 638433258729123411L;

    String message = "";

    public InvalidParameterException(String message) {

    }
}
