package cat.iesesteveterradas.dbapi.persistencia;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

@Entity
public class Usuaris {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String telefon;
    private String nickname;
    private String email;
    private String API_KEY; // New field for API Key

    @ManyToOne
    private Grup group; // Reference to the Group table (Many-to-One)

    @OneToOne
    private Pla plan; // Reference to the Plan table (One-to-One)

    // Constructors
    public Usuaris() {
    }

    public Usuaris(String telefon, String nickname, String email, String API_KEY, Grup group, Pla plan) {
        this.telefon = telefon;
        this.nickname = nickname;
        this.email = email;
        this.API_KEY = API_KEY;
        this.group = group;
        this.plan = plan;
    }

    public Usuaris(String telefon, String nickname, String email) {
        this.telefon = telefon;
        this.nickname = nickname;
        this.email = email;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAPI_KEY() {
        return API_KEY;
    }

    public void setAPI_KEY(String API_KEY) {
        this.API_KEY = API_KEY;
    }

    public Grup getGroup() {
        return group;
    }

    public void setGroup(Grup group) {
        this.group = group;
    }

    public Pla getPlan() {
        return plan;
    }

    public void setPlan(Pla plan) {
        this.plan = plan;
    }

    @Override
    public String toString() {
        return "Usuaris{" +
                "id=" + id +
                ", telefon='" + telefon + '\'' +
                ", nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                ", API_KEY='" + API_KEY + '\'' +
                ", group=" + group +
                ", plan=" + plan +
                '}';
    }
}
