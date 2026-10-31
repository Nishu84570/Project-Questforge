
package questforge.dto;

public class UserResponseDTO {

    private Long id;
    private String name;
    private String email;
    private String role;
    private long xp;
    private Integer level;

    public UserResponseDTO() {}

    public UserResponseDTO(
            Long id,
            String name,
            String email,
            String role,
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

    public void setId(Long id) {
        this.id = id;
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

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}
