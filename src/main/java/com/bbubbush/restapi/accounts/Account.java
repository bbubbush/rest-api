package com.bbubbush.restapi.accounts;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Builder @Getter @Setter @EqualsAndHashCode(of = "id")
@NoArgsConstructor @AllArgsConstructor
@Entity
public class Account {
    @Id @GeneratedValue
    private Integer id;
    private String email;
    private String password;
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<AccountRoles> roles;

}
