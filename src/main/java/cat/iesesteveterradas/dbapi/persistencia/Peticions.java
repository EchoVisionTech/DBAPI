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

    private String prompts;
    private String imatges;
    private String model;

    // Constructors
    public Peticions() {
    }

    public Peticions(String prompts, String imatges, String model) {
        this.prompts = prompts;
        this.imatges = imatges;
        this.model = model;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrompts() {
        return prompts;
    }

    public void setPrompts(String prompts) {
        this.prompts = prompts;
    }

    public String getImatges() {
        return imatges;
    }

    public void setImatges(String imatges) {
        this.imatges = imatges;
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
                ", prompts='" + prompts + '\'' +
                ", imatges='" + imatges + '\'' +
                ", model='" + model + '\'' +
                '}';
    }
}
