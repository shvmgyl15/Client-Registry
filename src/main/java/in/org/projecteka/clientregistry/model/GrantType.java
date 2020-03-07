package in.org.projecteka.clientregistry.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum GrantType {
    PASSWORD("password");

    private final String grantType;

    GrantType(String value) {
        grantType = value;
    }

    @JsonValue
    public String getValue() {
        return grantType;
    }
}
