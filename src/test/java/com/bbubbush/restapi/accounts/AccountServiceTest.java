package com.bbubbush.restapi.accounts;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AccountServiceTest {
    @Autowired
    private AccountService accountService;
    @Autowired
    private PasswordEncoder passwordEncoder;

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
        accountService.saveAccount(account);

        // when
        UserDetails userDetails = accountService.loadUserByUsername(account.getEmail());

        // then
        assertTrue(passwordEncoder.matches(password, userDetails.getPassword()));
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