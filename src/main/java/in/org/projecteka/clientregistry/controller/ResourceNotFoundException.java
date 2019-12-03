package in.org.projecteka.clientregistry.controller;

public class ResourceNotFoundException extends RuntimeException {
    private String message = "Not found";

    public ResourceNotFoundException() {
    }

    public ResourceNotFoundException(String message) {
        this.message = message;
    }
}