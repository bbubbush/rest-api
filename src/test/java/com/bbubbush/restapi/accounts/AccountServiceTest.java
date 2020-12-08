package com.bbubbush.restapi.accounts;

import lombok.RequiredArgsConstructor;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.support.discovery.SelectorResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AccountServiceTest {
    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void findByUserName() {
        // given
        String email = "bbubbush@gmail.com";
        String password = "bbubbush";
        Account account = Account.builder()
                .email(email)
                .password(password)
                .roles(Set.of(AccountRoles.ADMIN, AccountRoles.USER))
                .build();
        accountRepository.save(account);

        // when
        UserDetails userDetails = accountService.loadUserByUsername(account.getEmail());

        // then
        assertEquals(password, userDetails.getPassword());
        assertEquals(email, userDetails.getUsername());
    }

    @Test
    public void findByUserName_ThrowUsernameNotFoundException() {
        // given
        String email = "bbubbush@naver.com";
        String password = "bbubbush";
        Account account = Account.builder()
                .email(email)
                .password(password)
                .roles(Set.of(AccountRoles.ADMIN, AccountRoles.USER))
                .build();

        // when
        UsernameNotFoundException usernameNotFoundException = assertThrows(UsernameNotFoundException.class, () -> {
            UserDetails userDetails = accountService.loadUserByUsername(account.getEmail());
        });

        // then
        System.out.println("message :: " + usernameNotFoundException.getMessage());
        assertTrue(usernameNotFoundException.getMessage().contains(email));
    }


}