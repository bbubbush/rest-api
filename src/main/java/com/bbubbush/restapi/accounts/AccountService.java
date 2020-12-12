package com.bbubbush.restapi.accounts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService implements UserDetailsService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Account saveAccount(Account account) {
        account.setPassword(this.passwordEncoder.encode(account.getPassword()));
        return accountRepository.save(account);
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(userName).orElseThrow(() -> new UsernameNotFoundException(userName));
        return new AccountAdapter(account);
    }

}
