package cat.iesesteveterradas.dbapi.persistencia;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.util.Date;

@Entity
public class Peticions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String prompt;
    private String[] imatges;
    private String model;
    private Date data_peticio;

    @ManyToOne // Many Peticions can be associated with one Usuaris
    private Usuaris user; // Reference to the Usuaris table (Many-to-One)

    // Constructors
    public Peticions() {
    }

    public Peticions(String model, String prompt, String[] imatges, Date data_peticio, Usuaris user) {
        this.prompt = prompt;
        this.imatges = imatges;
        this.model = model;
        this.data_peticio = data_peticio;
        this.user = user;
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

    public String[] getImatges() {
        return imatges;
    }

    public void setImatges(String[] imatges) {
        this.imatges = imatges;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Date getData_peticio() {
        return data_peticio;
    }

    public void setData_peticio(Date data_peticio) {
        this.data_peticio = data_peticio;
    }

    public Usuaris getUser() {
        return user;
    }

    public void setUser(Usuaris user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Peticions{" +
                "id=" + id +
                ", prompt='" + prompt + '\'' +
                ", imatges='" + imatges + '\'' +
                ", model='" + model + '\'' +
                ", data_peticio='" + data_peticio + '\'' +
                ", user='" + user + '\'' +
                '}';
    }
}
