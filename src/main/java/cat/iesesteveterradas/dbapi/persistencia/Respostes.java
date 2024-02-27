package cat.iesesteveterradas.dbapi.persistencia;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

@Entity
public class Respostes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text_generat;

    @OneToOne
    private Peticions peticio; // Reference to the Peticions table (One-to-One)

    @ManyToOne
    private Usuaris user; // Reference to the Usuaris table (Many-to-One)

    // Constructors
    public Respostes() {
    }

    public Respostes(String text_generat, Peticions peticio, Usuaris user) {
        this.text_generat = text_generat;
        this.peticio = peticio;
        this.user = user;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText_generat() {
        return text_generat;
    }

    public void setText_generat(String text_generat) {
        this.text_generat = text_generat;
    }

    public Peticions getPeticio() {
        return peticio;
    }

    public void setPeticio(Peticions peticio) {
        this.peticio = peticio;
    }

    public Usuaris getUser() {
        return user;
    }

    public void setUser(Usuaris user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Respostes{" +
                "id=" + id +
                ", text_generat='" + text_generat + '\'' +
                ", peticio=" + peticio +
                ", user=" + user +
                '}';
    }
}
