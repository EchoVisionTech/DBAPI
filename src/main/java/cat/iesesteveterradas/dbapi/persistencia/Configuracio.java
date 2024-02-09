package cat.iesesteveterradas.dbapi.persistencia;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Configuracio {
    @Id // Marca el camp com a clau primària
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Configura la generació automàtica del valor
    private Long id;

    private String nom;

    @OneToMany(mappedBy = "configuracio", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Propietat> propietats;

    // Constructors
    public Configuracio() {
    }

    public Configuracio(String nom) {
        this.nom = nom;
        this.propietats = new ArrayList<>();
    }

    // Getters i setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public List<Propietat> getPropietats() {
        return propietats;
    }

    public void setPropietats(List<Propietat> propietats) {
        this.propietats = propietats;
    }

    public void addPropietat(Propietat propietat) {
        this.propietats.add(propietat);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Configuració [id=").append(id)
                .append(", nom=").append(nom)
                .append(", propietats=[");
        for (Propietat propietat : propietats) {
            sb.append(propietat.toString()).append(", ");
        }
        if (!propietats.isEmpty()) {
            sb.setLength(sb.length() - 2); // Treu la última coma i espai
        }
        sb.append("]]");
        return sb.toString();
    }
}
