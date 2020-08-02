package in.org.projecteka.clientregistry.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include. NON_NULL)
public class Organization {
    private String resourceType = "Organization";
    private String id;
    private List<Identifier> identifier;
    private String name;
    private boolean active;
    private  List<OrganizationType> type;
    private List<Telecom> telecom;
    private List<Address> address;
}
