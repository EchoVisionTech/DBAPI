package cat.iesesteveterradas.dbapi.persistencia;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PeticionsDAO {
    private static final Logger logger = LoggerFactory.getLogger(GenericDAO.class);

    public static Peticions trobaOCreaPeticions(String model, String prompt, String[] imatges) {
        Session session = SessionFactoryManager.getSessionFactory().openSession();
        Transaction tx = null;
        Peticions peticio = null;
        try {
            System.err.println("Reaches petition creation");
            tx = session.beginTransaction();
            // Intenta trobar una configuració existent amb el nom donat
            Query<Peticions> query = session.createQuery("FROM Peticions WHERE model = :model AND prompt = :prompt", Peticions.class);
            query.setParameter("model", model);
            query.setParameter("prompt", prompt);
            peticio = query.uniqueResult();
            // Si no es troba, crea una nova configuració
            if (peticio == null) {
                peticio = new Peticions(model, prompt, imatges);
                session.save(peticio);
                tx.commit();
                logger.info("Nova peticio creada amb el model: {}, prompt: {}, imatges:(Codi Base64)", model, prompt);
            } else {
                logger.info("Petició ja existent amb el model: {} i prompt: {}", model, prompt);
            }
            System.err.println("Reaches petition end");
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            logger.error("Error al crear o trobar la petició", e);
        } finally {
            session.close();
        }
        return peticio;
    }

}