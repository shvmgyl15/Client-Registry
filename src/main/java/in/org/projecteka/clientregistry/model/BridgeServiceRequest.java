package in.org.projecteka.clientregistry.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class BridgeServiceRequest {
    private String id;
    private String name;
    @JsonProperty("serviceType")
    private String orgType;
    private List<String> orgAlias;
    @Default
    private Boolean active=true;
    private String phone;
    private String city;
    private String state;
}
