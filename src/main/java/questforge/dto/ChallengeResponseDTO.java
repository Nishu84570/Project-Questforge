
package questforge.dto;

public class ChallengeResponseDTO {

    private Long id;
    private String title;
    private String description;
    private String difficulty;
    private String category;

    public ChallengeResponseDTO() {
    }

    public ChallengeResponseDTO(
            Long id,
            String title,
            String description,
            String difficulty,
            String category) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
