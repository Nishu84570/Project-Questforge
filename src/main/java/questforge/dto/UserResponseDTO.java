
package questforge.dto;

import questforge.model.User.Role;

public class UserResponseDTO {

    private Long id;
    private String name;
    private String email;
    private questforge.model.User.Role role;
    private long xp;
    private Integer level;

    public UserResponseDTO() {
    }

    public UserResponseDTO(
            Long id,
            String name,
            String email,
            Role role,
            long xp,
            Integer level) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.xp = xp;
        this.level = level;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public long getXp() {
        return xp;
    }

    public Integer getLevel() {
        return level;
    }
}
