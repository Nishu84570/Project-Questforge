package questforge.dto;

public class AchievementResponseDTO {

    private Integer id;

    private String code;

    private String name;

    private String description;

    private String icon;

    // =========================
    // CONSTRUCTOR
    // =========================

    public AchievementResponseDTO() {
    }

    public AchievementResponseDTO(
            Integer id,
            String code,
            String name,
            String description,
            String icon) {

        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.icon = icon;
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

    public void setDescription(
            String description) {

        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}