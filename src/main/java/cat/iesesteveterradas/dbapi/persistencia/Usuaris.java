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
    private String password;
    private String API_KEY;
    private String codi_validacio;
    private Integer quota;

    @ManyToOne
    private Grup grup; // Reference to the Group table (Many-to-One)

    @OneToOne
    private Pla pla; // Reference to the Plan table (One-to-One)

    // Constructors
    public Usuaris() {
    }

    public Usuaris(String telefon, String nickname, String email, String password, String API_KEY, String codi_validacio, Integer quota, Grup grup, Pla pla) {
        this.telefon = telefon;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.API_KEY = API_KEY;
        this.codi_validacio = codi_validacio;
        this.quota = quota;
        this.grup = grup;
        this.pla = pla;
    }

    public Usuaris(String telefon, String nickname, String email, String codi_validacio) {
        this.telefon = telefon;
        this.nickname = nickname;
        this.email = email;
        this.codi_validacio = codi_validacio;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAPI_KEY() {
        return API_KEY;
    }

    public void setAPI_KEY(String API_KEY) {
        this.API_KEY = API_KEY;
    }

    public String getCodi_validacio() {
        return codi_validacio;
    }

    public void setCodi_validacio(String codi_validacio) {
        this.codi_validacio = codi_validacio;
    }

    public Integer getQuota() {
        return quota;
    }

    public void setQuota(Integer quota) {
        this.quota = quota;
    }

    public Grup getGrup() {
        return grup;
    }

    public void setGrup(Grup grup) {
        this.grup = grup;
    }

    public Pla getPla() {
        return pla;
    }

    public void setPla(Pla pla) {
        this.pla = pla;
    }

    @Override
    public String toString() {
        return "Usuaris{" +
                "id=" + id +
                ", telefon='" + telefon + '\'' +
                ", nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                ", API_KEY='" + API_KEY + '\'' +
                ", codi_validacio=" + codi_validacio +
                ", quota=" + quota +
                ", grup=" + grup +
                ", pla=" + pla +
                '}';
    }
}
