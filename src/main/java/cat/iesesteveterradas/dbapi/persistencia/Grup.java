package cat.iesesteveterradas.dbapi.persistencia;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;

@Entity
public class Grup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String grupName;

    @OneToMany(mappedBy = "grup")
    private List<Usuaris> users;

    // Constructors
    public Grup() {
    }

    public Grup(String grupName) {
        this.grupName = grupName;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getgrupName() {
        return grupName;
    }

    public void setgrupName(String grupName) {
        this.grupName = grupName;
    }

    public List<Usuaris> getUsers() {
        return users;
    }

    public void setUsers(List<Usuaris> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "Grup{" +
                "id=" + id +
                ", grupName='" + grupName + '\'' +
                '}';
    }
}
