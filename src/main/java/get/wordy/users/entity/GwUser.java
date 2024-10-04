package get.wordy.users.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "gw_users")
@Getter
@Setter
@NoArgsConstructor
@SecondaryTable(name = "usr_authorities", pkJoinColumns = @PrimaryKeyJoinColumn(name = "email"))
public class GwUser {

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Id
    @Column(nullable = false)
    private String email;

    @Column(nullable = false, length = 60)
    private String password;

    @Column(name = "authority", table = "usr_authorities")
    private String role;

}
