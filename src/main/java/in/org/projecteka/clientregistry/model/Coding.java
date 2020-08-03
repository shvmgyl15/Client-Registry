package in.org.projecteka.clientregistry.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Coding {
    private String system;
    private String code;
    private String display;
}
