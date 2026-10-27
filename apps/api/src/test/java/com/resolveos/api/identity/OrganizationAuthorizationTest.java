package com.resolveos.api.identity;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/identity-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class OrganizationAuthorizationTest {

    private static final String ORGANIZATION_A = "aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa";
    private static final String ORGANIZATION_B = "bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbbbb";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void unauthenticatedRequestIsRejected() throws Exception {
        mockMvc.perform(get("/api/v1/me/organizations")).andExpect(status().isUnauthorized());
    }

    @Test
    void subjectSeesOnlyActiveMembershipOrganizations() throws Exception {
        mockMvc.perform(get("/api/v1/me/organizations").with(jwt().jwt(token -> token.subject("subject-admin-a"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(ORGANIZATION_A));
    }

    @Test
    void exactForeignTenantUuidDoesNotGrantReadAccess() throws Exception {
        mockMvc.perform(get("/api/v1/organizations/{id}", ORGANIZATION_B)
                        .with(jwt().jwt(token -> token.subject("subject-admin-a"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
    }

    @Test
    void tokenOrganizationClaimCannotOverrideDatabaseMembership() throws Exception {
        mockMvc.perform(get("/api/v1/organizations/{id}", ORGANIZATION_B)
                        .with(jwt().jwt(token -> token
                                .subject("subject-admin-a")
                                .claim("organizationId", ORGANIZATION_B))))
                .andExpect(status().isNotFound());
    }

    @Test
    void wrongOrganizationRoleIsForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/organizations/{id}/memberships", ORGANIZATION_A)
                        .with(jwt().jwt(token -> token.subject("subject-operator-a"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("INSUFFICIENT_ORGANIZATION_ROLE"));
    }

    @Test
    void organizationAdminCanListOnlyOwnTenantMemberships() throws Exception {
        mockMvc.perform(get("/api/v1/organizations/{id}/memberships", ORGANIZATION_A)
                        .with(jwt().jwt(token -> token.subject("subject-admin-a"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].organizationId").value(ORGANIZATION_A))
                .andExpect(jsonPath("$[1].organizationId").value(ORGANIZATION_A));
    }

    @Test
    @Sql(
            statements = "UPDATE memberships SET role = 'IMPLEMENTATION_OPERATOR' WHERE subject_id = 'subject-admin-a'",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void databaseRoleChangeTakesEffectWithoutTrustingTokenRoles() throws Exception {
        mockMvc.perform(get("/api/v1/organizations/{id}/memberships", ORGANIZATION_A)
                        .with(jwt().jwt(token -> token
                                .subject("subject-admin-a")
                                .claim("roles", new String[] {"ORG_ADMIN"}))))
                .andExpect(status().isForbidden());
    }
}
