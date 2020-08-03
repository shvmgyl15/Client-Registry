package in.org.projecteka.clientregistry.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class OrganizationType {
    private List<Coding> coding;
}
