
package questforge.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

@Entity
@Table(
    name = "users",
    uniqueConstraints = {@UniqueConstraint(
        name = "uk_users_email",
        columnNames = "email")
    }
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 254)
    @Column(nullable = false, length = 254)
    private String email;

    @NotBlank(message = "Password is required")
    @Column(nullable = false, length = 255)
    private String password;

    public enum Role {
        USER,
        ADMIN,
        EMPLOYEE;

        public boolean matches(String string) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'matches'");
        }

        public String toUpperCase() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'toUpperCase'");
        }
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Role role = Role.USER;

    @PositiveOrZero(message = "XP cannot be negative")
    @Column(nullable = false)
    private long xp = 0L;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "profile_image", columnDefinition = "BYTEA")
    private byte[] profileImage;

    @Size(max = 100)
    @Column(name = "profile_image_content_type", length = 100)
    private String profileImageContentType;

    public User() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public long getXp() {
        return xp;
    }

    public void setXp(long xp) {
        this.xp = xp;
    }

    public byte[] getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(byte[] profileImage) {
        this.profileImage = profileImage;
    }

    public String getProfileImageContentType() {
        return profileImageContentType;
    }

    public void setProfileImageContentType(String contentType) {
        this.profileImageContentType = contentType;
    }

    public void setRole(String normalizedRole) {
        this.id = id;
    }
}
