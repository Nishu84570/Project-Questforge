package questforge.model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "achievements",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"user_id", "code"}
                )
        }
)
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // User who unlocked the achievement
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    // Unique achievement code
    // Example: FIRST_CHALLENGE
    @Column(nullable = false)
    private String code;

    // Display name
    @Column(nullable = false)
    private String name;

    // Description of the achievement
    private String description;

    // Badge icon/emoji
    private String icon;

    public Achievement() {
    }

    // =========================
    // GETTERS AND SETTERS
    // =========================

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}