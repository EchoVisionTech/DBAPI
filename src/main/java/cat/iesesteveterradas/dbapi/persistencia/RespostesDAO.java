package cat.iesesteveterradas.dbapi.persistencia;


import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RespostesDAO {
    private static final Logger logger = LoggerFactory.getLogger(GenericDAO.class);

    public static Respostes trobaOCreaRespostes(String text_generat, Peticions peticio, Usuaris usuari) {
        Session session = SessionFactoryManager.getSessionFactory().openSession();
        Transaction tx = null;
        Respostes resposta = null;
        try {
            tx = session.beginTransaction();
            resposta = new Respostes(text_generat, peticio, usuari);
            session.save(resposta);
            tx.commit();
            logger.info("Nova resposta amb id {} creada.", resposta.getId());
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            logger.error("Error al crear o trobar la resposta", e);
        } finally {
            session.close();
        }
        return resposta;
    }

}
