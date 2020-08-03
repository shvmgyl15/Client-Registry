package in.org.projecteka.clientregistry.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Identifier {
    private String system;
    private String value;
    private String use;
}
