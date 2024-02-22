package cat.iesesteveterradas.dbapi.persistencia;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Peticions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String prompt;
    private String imatge;
    private String model;

    // Constructors
    public Peticions() {
    }

    public Peticions(String prompt, String imatge, String model) {
        this.prompt = prompt;
        this.imatge = imatge;
        this.model = model;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getImatge() {
        return imatge;
    }

    public void setImatge(String imatge) {
        this.imatge = imatge;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Override
    public String toString() {
        return "Peticions{" +
                "id=" + id +
                ", prompt='" + prompt + '\'' +
                ", imatge='" + imatge + '\'' +
                ", model='" + model + '\'' +
                '}';
    }
}
