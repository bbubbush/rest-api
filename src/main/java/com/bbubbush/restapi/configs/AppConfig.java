package com.bbubbush.restapi.configs;

import com.bbubbush.restapi.accounts.Account;
import com.bbubbush.restapi.accounts.AccountRoles;
import com.bbubbush.restapi.accounts.AccountService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class AppConfig {
    @Autowired
    private AccountService accountService;

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public ApplicationRunner applicationRunner() {
        return args -> {
            Account bbubbush = Account.builder()
                    .email("bbubbush@gmail.com")
                    .password("bbubbush")
                    .roles(Set.of(AccountRoles.ADMIN, AccountRoles.USER))
                    .build();
            accountService.saveAccount(bbubbush);
        };
    }
}
