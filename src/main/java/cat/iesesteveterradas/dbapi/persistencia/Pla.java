package cat.iesesteveterradas.dbapi.persistencia;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class Pla {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String plaName;
    private int quota;

    @OneToOne(mappedBy = "pla")
    private Usuaris user;

    // Constructors
    public Pla() {
    }

    public Pla(String plaName, int quota) {
        this.plaName = plaName;
        this.quota = quota;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlaName() {
        return plaName;
    }

    public void setPlaName(String plaName) {
        this.plaName = plaName;
    }

    public int getQuota() {
        return quota;
    }

    public void setQuota(int quota) {
        this.quota = quota;
    }

    public Usuaris getUser() {
        return user;
    }

    public void setUser(Usuaris user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Pla{" +
                "id=" + id +
                ", plaName='" + plaName + '\'' +
                ", quota=" + quota +
                '}';
    }
}
