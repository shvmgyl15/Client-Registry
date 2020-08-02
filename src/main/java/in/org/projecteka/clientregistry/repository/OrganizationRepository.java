package in.org.projecteka.clientregistry.repository;

import in.org.projecteka.clientregistry.model.Address;
import in.org.projecteka.clientregistry.model.Coding;
import in.org.projecteka.clientregistry.model.Identifier;
import in.org.projecteka.clientregistry.model.Organization;
import in.org.projecteka.clientregistry.model.OrganizationType;
import in.org.projecteka.clientregistry.model.Telecom;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.Collections;
import java.util.List;

@Repository
public class OrganizationRepository {
    final JdbcTemplate jdbcTemplate;

    public OrganizationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Organization> findAll(String name) {
        return jdbcTemplate.query(
                "SELECT org_id, org_name, org_type, active, org_alias, phone, city, state " +
                        "FROM organization " +
                        "WHERE UPPER(org_name) like ?",
                new Object[]{"%"+name.toUpperCase()+"%"},
                this::mapRow);
    }

    private Organization mapRow(ResultSet rs, int rowNum) {
        try {
            String orgId = rs.getString("org_id");
            String orgName = rs.getString("org_name");
            String orgType = rs.getString("org_type");
            boolean active = rs.getBoolean("active");
            String phone = rs.getString("phone");
            String city = rs.getString("city");
            String state = rs.getString("state");
            Identifier identifier = Identifier.builder()
                    .system("http://projecteka.in")
                    .value(orgId)
                    .use("official")
                    .build();
            Coding orgTypeCode = Coding.builder()
                    .system("http://hl7.org/fhir/ValueSet/organization-type")
                    .code(orgType)
                    .display("Healthcare Provider")
                    .build();
            OrganizationType organizationType = OrganizationType.builder()
                    .coding(Collections.singletonList(orgTypeCode))
                    .build();

            Telecom tele = null;
            if (phone != null) {
                tele = Telecom.builder()
                        .system("phone")
                        .value(phone)
                        .use("work")
                        .build();
            }
            Address addr = null;
            if (state != null || city != null) {
                addr = Address.builder()
                        .use("work")
                        .city(city)
                        .state(state)
                        .build();
            }


            return Organization.builder()
                    .resourceType("Organization")
                    .name(orgName)
                    .id(orgId)
                    .identifier(Collections.singletonList(identifier))
                    .active(active)
                    .type(Collections.singletonList(organizationType))
                    .telecom(tele != null ? Collections.singletonList(tele)  : null)
                    .address(addr != null ? Collections.singletonList(addr)  : null)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Organization find(String id) {
        List<Organization> organizations = jdbcTemplate.query(
                "SELECT org_id, org_name, org_type, active, org_alias, phone, city, state " +
                        "FROM organization " +
                        "WHERE org_id=?", new Object[]{id},
                this::mapRow);
        if (organizations != null && !organizations.isEmpty()) {
            return organizations.get(0);
        }
        return null;
    }
}
