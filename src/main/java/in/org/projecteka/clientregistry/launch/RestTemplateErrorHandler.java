package in.org.projecteka.clientregistry.launch;

import in.org.projecteka.clientregistry.controller.BadCredentialsException;
import in.org.projecteka.clientregistry.controller.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

@Component
public class RestTemplateErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse httpResponse)
            throws IOException {

        return (
                httpResponse.getStatusCode().is4xxClientError()
                        || httpResponse.getStatusCode().is5xxServerError());
    }

    @Override
    public void handleError(ClientHttpResponse httpResponse)
            throws IOException {
        if (httpResponse.getStatusCode()
                .series() == HttpStatus.Series.SERVER_ERROR) {
            throw new RuntimeException("Unable to process request");
        } else if (httpResponse.getStatusCode()
                .series() == HttpStatus.Series.CLIENT_ERROR) {
            if (httpResponse.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new BadCredentialsException("Invalid credentials");
            }
            if (httpResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ResourceNotFoundException();
            }
        }
    }
}
