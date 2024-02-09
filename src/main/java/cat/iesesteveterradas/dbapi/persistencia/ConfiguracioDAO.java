package cat.iesesteveterradas.dbapi.persistencia;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfiguracioDAO {
    private static final Logger logger = LoggerFactory.getLogger(ConfiguracioDAO.class);

    public static Configuracio trobaOCreaConfiguracioPerNom(String nom) {
        Session session = SessionFactoryManager.getSessionFactory().openSession();
        Transaction tx = null;
        Configuracio configuracio = null;
        try {
            tx = session.beginTransaction();
            // Intenta trobar una configuració existent amb el nom donat
            Query<Configuracio> query = session.createQuery("FROM Configuracio WHERE nom = :nom", Configuracio.class);
            query.setParameter("nom", nom);
            configuracio = query.uniqueResult();
            // Si no es troba, crea una nova configuració
            if (configuracio == null) {
                configuracio = new Configuracio(nom);
                session.save(configuracio);
                tx.commit();
                logger.info("Nova configuració creada amb el nom: {}", nom);
            } else {
                logger.info("Configuració ja existent amb el nom: {}", nom);
            }
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            logger.error("Error al crear o trobar la configuració", e);
        } finally {
            session.close();
        }
        return configuracio;
    }

    public static void afegeixPropietatAConfiguracio(Propietat novaPropietat, Configuracio configuracio) {
        Session session = SessionFactoryManager.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // Comprova si la configuració ja té una propietat amb la mateixa clau
            boolean trobat = false;
            for (Propietat propietatExistenta : configuracio.getPropietats()) {
                if (propietatExistenta.getClau().equals(novaPropietat.getClau())) {
                    // Si existeix, actualitza el valor de la propietat existent
                    propietatExistenta.setValor(novaPropietat.getValor());
                    session.update(propietatExistenta);
                    trobat = true;
                    logger.info("Valor de la propietat actualitzat amb èxit: {}", novaPropietat.getClau());
                    break;
                }
            }
    
            if (!trobat) {
                // Si no es troba una propietat existent amb la mateixa clau, afegeix la nova propietat
                novaPropietat.setConfiguracio(configuracio);
                configuracio.addPropietat(novaPropietat);
                session.save(novaPropietat);
                logger.info("Nova propietat afegida amb èxit a la configuració: {}", configuracio.getNom());
            }

            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            logger.error("Error al vincular o actualitzar la propietat amb la configuració", e);
        } finally {
            session.close();
        }
    }
    
    public static boolean eliminarPropietatDeConfiguracio(String clau, Configuracio configuracio) {
        Session session = SessionFactoryManager.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // Cerca la propietat per la clau dins la configuració
            Propietat propietatAEliminar = null;
            for (Propietat propietat : configuracio.getPropietats()) {
                if (propietat.getClau().equals(clau)) {
                    propietatAEliminar = propietat;
                    break;
                }
            }
            
            // Si no es troba la propietat, retorna false
            if (propietatAEliminar == null) {
                return false;
            }
            
            // Elimina la propietat de la base de dades
            session.remove(propietatAEliminar);
            tx.commit();
            
            // Elimina la propietat de la llista en memòria per mantenir la consistència
            configuracio.getPropietats().remove(propietatAEliminar);
            
            logger.info("Propietat {} eliminada amb èxit de la configuració: {}", clau, configuracio.getNom());
            return true;
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            logger.error("Error al eliminar la propietat de la configuració", e);
            return false;
        } finally {
            session.close();
        }
    }
}
