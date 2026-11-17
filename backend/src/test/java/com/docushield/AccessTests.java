package com.docushield;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccessTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void beforeAll() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    // 1. Anonymous access to documents -> 401 Unauthorized
    @Test
    @WithAnonymousUser
    public void whenAnonymAccessDocumentsThenStatusUnauthorized() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/v1/documents"));
        int status = result.andReturn().getResponse().getStatus();
        assertEquals(401, status);
    }

    // 2. Anonymous access to protected secret-vault route -> 401 Unauthorized
    @Test
    @WithAnonymousUser
    public void whenAnonymAccessSecretVaultThenStatusUnauthorized() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/v1/documents/secret-vault"));
        int status = result.andReturn().getResponse().getStatus();
        assertEquals(401, status);
    }

    // 3. User role access to documents -> 200 OK
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void whenUserAccessDocumentsThenStatusOk() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/v1/documents"));
        int status = result.andReturn().getResponse().getStatus();
        assertEquals(200, status);
    }

    // 4. User role access to user-profile -> 200 OK
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void whenUserAccessUserProfileThenStatusOk() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/v1/documents/user-profile"));
        int status = result.andReturn().getResponse().getStatus();
        assertEquals(200, status);
    }

    // 5. User role access to hello/user -> 200 OK
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void whenUserAccessHelloUserThenStatusOk() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/v1/documents/hello/user"));
        int status = result.andReturn().getResponse().getStatus();
        String content = result.andReturn().getResponse().getContentAsString();
        assertEquals(200, status);
        assertEquals("Hello User!", content);
    }

    // 6. User role access to hello/admin (requires ADMIN) -> 403 Forbidden
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void whenUserAccessHelloAdminThenStatusForbidden() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/v1/documents/hello/admin"));
        int status = result.andReturn().getResponse().getStatus();
        assertEquals(403, status);
    }

    // 7. User role access to secret-vault -> 403 Forbidden
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void whenUserAccessSecretVaultThenStatusForbidden() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/v1/documents/secret-vault"));
        int status = result.andReturn().getResponse().getStatus();
        assertEquals(403, status);
    }

    // 8. Manager role access to management-overview -> 200 OK
    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    public void whenManagerAccessManagementOverviewThenStatusOk() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/v1/documents/management-overview"));
        int status = result.andReturn().getResponse().getStatus();
        assertEquals(200, status);
    }

    // 9. Manager role access to secret-vault (requires ADMIN) -> 403 Forbidden
    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    public void whenManagerAccessSecretVaultThenStatusForbidden() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/v1/documents/secret-vault"));
        int status = result.andReturn().getResponse().getStatus();
        assertEquals(403, status);
    }

    // 10. Admin role access to secret-vault -> 200 OK
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenAdminAccessSecretVaultThenStatusOk() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/v1/documents/secret-vault"));
        int status = result.andReturn().getResponse().getStatus();
        assertEquals(200, status);
    }
}
