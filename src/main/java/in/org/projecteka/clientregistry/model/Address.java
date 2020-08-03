package in.org.projecteka.clientregistry.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include. NON_NULL)
public class Address {
    private String type;
    private String city;
    private String state;
    private String use;
}
