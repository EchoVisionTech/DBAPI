package cat.iesesteveterradas.dbapi.persistencia;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;



@Entity // Indica que aquesta classe és una entitat JPA
public class Propietat {

    @Id // Marca el camp com a clau primària
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Configura la generació automàtica del valor
    private Long id;
    private String clau;
    private String valor;

    @ManyToOne
    private Configuracio configuracio;

    // Constructors
    public Propietat() {
    }

    public Propietat(String clau, String valor) {
        this.clau = clau;
        this.valor = valor;
    }

    // Getters i setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClau() {
        return clau;
    }

    public void setClau(String clau) {
        this.clau = clau;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public Configuracio getConfiguracio() {
        return configuracio;
    }

    public void setConfiguracio(Configuracio configuracio) {
        this.configuracio = configuracio;
    }

    @Override
    public String toString() {
        return "Propietat [clau=" + clau + ", valor=" + valor + "]";
    }
}
