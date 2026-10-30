
package questforge.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

@Entity
@Table(
    name = "users",
    uniqueConstraints = {@UniqueConstraint(
    name = "uk_users_email",
    columnNames = {"email"})}
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Pattern(
        regexp = "^[A-Za-z0-9._%+-]+@gmail\\.com$",
        message = "Email must be a valid @gmail.com address"
    )
    @Size(max = 254, message = "Email cannot exceed 254 characters")
    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(max = 255, message = "Password cannot exceed 255 characters")
    @Column(nullable = false, length = 255)
    private String password;

    @NotBlank(message = "Role is required")
    @Size(max = 30, message = "Role cannot exceed 30 characters")
    @Column(nullable = false, length = 30)
    private String role;

    @PositiveOrZero(message = "XP cannot be negative")
    @Column(nullable = false)
    private long xp = 0L;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "profile_image", columnDefinition = "BYTEA")
    private byte[] profileImage;

    @Size(max = 100, message = "Image content type cannot exceed 100 characters")
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
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

    public void setProfileImageContentType(
            String profileImageContentType) {
        this.profileImageContentType = profileImageContentType;
    }
}
