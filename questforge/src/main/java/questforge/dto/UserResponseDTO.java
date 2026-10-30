package questforge.dto;

public class UserResponseDTO {

    private Integer id;

    private String name;

    private String email;

    private String role;

    private Integer xp;

    private Integer level;

    // =========================
    // CONSTRUCTOR
    // =========================

    public UserResponseDTO() {
    }

    public UserResponseDTO(
            Integer id,
            String name,
            String email,
            String role,
            Integer xp,
            Integer level) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.xp = xp;
        this.level = level;
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

    public Integer getXp() {
        return xp;
    }

    public void setXp(Integer xp) {
        this.xp = xp;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}