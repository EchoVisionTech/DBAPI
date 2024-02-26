package cat.iesesteveterradas.dbapi.persistencia;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UsuarisDAO {
    private static final Logger logger = LoggerFactory.getLogger(GenericDAO.class);

    public static Usuaris trobaORegistreUsuaris(String telefon, String nickname, String email, String codi_validacio) {
        Session session = SessionFactoryManager.getSessionFactory().openSession();
        Transaction tx = null;
        Usuaris usuari = null;
        try {
            tx = session.beginTransaction();
            // Intenta trobar una configuració existent amb el nom donat
            Query<Usuaris> query = session.createQuery("FROM Usuaris WHERE telefon = :telefon", Usuaris.class);
            query.setParameter("telefon", telefon);
            usuari = query.uniqueResult();
            // Si no es troba, crea una nova configuració
            if (usuari == null) {
                usuari = new Usuaris(telefon, nickname, email, codi_validacio);
                session.save(usuari);
                tx.commit();
                logger.info("Nova peticio creada amb el telefon: {}, nickname: {}, email: {}, codi_validacio: {}", telefon, nickname, email, codi_validacio);
            } else {
                logger.info("Petició ja existent amb el telefon: {}", telefon);
            }
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            logger.error("Error al crear o trobar el usuari", e);
        } finally {
            session.close();
        }
        return usuari;
    }

}