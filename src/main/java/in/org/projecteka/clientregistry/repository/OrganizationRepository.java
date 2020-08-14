package in.org.projecteka.clientregistry.repository;

import in.org.projecteka.clientregistry.model.Address;
import in.org.projecteka.clientregistry.model.BridgeServiceRequest;
import in.org.projecteka.clientregistry.model.Coding;
import in.org.projecteka.clientregistry.model.Identifier;
import in.org.projecteka.clientregistry.model.Organization;
import in.org.projecteka.clientregistry.model.OrganizationType;
import in.org.projecteka.clientregistry.model.Telecom;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;

@Repository
public class OrganizationRepository {
    final JdbcTemplate jdbcTemplate;

    public OrganizationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Organization> findAll(String name) {
        String search = "%" + name.toUpperCase() + "%";
        return jdbcTemplate.query(
                "SELECT org_id, org_name, org_type, active, org_alias, phone, city, state " +
                        "FROM organization " +
                        "WHERE UPPER(org_name) like ? OR UPPER(org_alias) like ?",
                new Object[]{search, search},
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

    public int addOrUpdateOrganization(BridgeServiceRequest organization) {
        Organization org = find(organization.getId());
        if (org == null) {
            return jdbcTemplate.update(
                    "INSERT INTO organization(org_id, org_name, org_alias, org_type, active, phone, city, state," +
                            " date_created, date_modified)" +
                            " VALUES(?,?,?,?,?,?,?,?,?,?)"
                    , organization.getId(), organization.getName(), String.join(",", organization.getOrgAlias()),
                    organization.getType(), organization.getActive(), organization.getPhone(), organization.getCity(),
                    organization.getState(), LocalDateTime.now(ZoneOffset.UTC), LocalDateTime.now(ZoneOffset.UTC));
        } else {
            String updateOrganizationQuery = "UPDATE organization SET ";
            if (organization.getName() != null) {
                updateOrganizationQuery += "org_name = '" + organization.getName() + "', ";
            }
            if (organization.getOrgAlias() != null) {
                updateOrganizationQuery += "org_alias = '" + String.join(",", organization.getOrgAlias()) + "', ";
            }
            if (organization.getActive() != null) {
                updateOrganizationQuery += "active = '" + organization.getActive() + "', ";
            }
            if (organization.getPhone() != null) {
                updateOrganizationQuery += "phone = '" + organization.getPhone() + "', ";
            }
            if (organization.getCity() != null) {
                updateOrganizationQuery += "city = '" + organization.getCity() + "', ";
            }
            if (organization.getState() != null) {
                updateOrganizationQuery += "state = '" + organization.getState() + "', ";
            }
            updateOrganizationQuery += "date_modified = '" + LocalDateTime.now(ZoneOffset.UTC) + "' WHERE org_id = '" + organization.getId() + "'";
            return jdbcTemplate.update(updateOrganizationQuery);
        }
    }
}
