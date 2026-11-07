package ma.fstg.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Vérifie les comportements attendus du tableau de l'étape 8.
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuthFlowTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void accesSansLoginRedirigeVersLogin() throws Exception {
        mvc.perform(get("/user/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void connexionReussieRedirigeVersHome() throws Exception {
        mvc.perform(formLogin("/authenticate").user("user").password("1111"))
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    void mauvaisMotDePasseRedirigeVersLoginError() throws Exception {
        mvc.perform(formLogin("/authenticate").user("user").password("faux"))
                .andExpect(redirectedUrl("/login?error=true"));
    }

    @Test
    void deconnexionRedirigeVersLoginLogout() throws Exception {
        mvc.perform(logout("/logout"))
                .andExpect(redirectedUrl("/login?logout=true"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void userSansRoleAdminRecoit403() throws Exception {
        mvc.perform(get("/admin/dashboard"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminAccedeAuxDeuxEspaces() throws Exception {
        mvc.perform(get("/admin/dashboard")).andExpect(status().isOk());
        mvc.perform(get("/user/dashboard")).andExpect(status().isOk());
    }
}
