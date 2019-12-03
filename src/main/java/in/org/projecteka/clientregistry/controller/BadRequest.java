package in.org.projecteka.clientregistry.controller;

public class BadRequest extends RuntimeException {

    private String errorMessage;

    public BadRequest(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String getMessage() {
        return errorMessage;
    }

}
