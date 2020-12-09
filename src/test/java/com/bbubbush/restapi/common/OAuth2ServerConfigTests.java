package com.bbubbush.restapi.common;

import com.bbubbush.restapi.accounts.Account;
import com.bbubbush.restapi.accounts.AccountRoles;
import com.bbubbush.restapi.accounts.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class OAuth2ServerConfigTests extends BaseControllerTest {
    @Autowired
    private AccountService accountService;

    @Test
    public void getAuthToken() throws Exception{
        // given
        String username = "bbubbush@mail.com";
        String password = "bbubbush";
        Account account = Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRoles.ADMIN, AccountRoles.USER))
                .build();
        Account saveAccount = this.accountService.saveAccount(account);

        String clientId = "myApp";
        String clientPass = "pass";
        mockMvc.perform(post("/oauth/token")
                    .with(httpBasic(clientId, clientPass))
                    .param("username", username)
                    .param("password", password)
                    .param("grant_type", "password"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists())
        ;

    }
}
