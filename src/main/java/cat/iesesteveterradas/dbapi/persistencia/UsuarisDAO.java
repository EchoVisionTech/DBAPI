package cat.iesesteveterradas.dbapi.persistencia;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

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
                logger.info("Nou usuari creat amb el telefon: {}, nickname: {}, email: {}, codi_validacio: {}", telefon, nickname, email, codi_validacio);
            } else {
                logger.info("Usuari ja existent amb el telefon: {}", telefon);
            }
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            logger.error("Error al crear o trobar el usuari", e);
        } finally {
            session.close();
        }
        return usuari;
    }

    public static String validarUsuari(String telefon, String codi_validacio) {
        Session session = SessionFactoryManager.getSessionFactory().openSession();
        Transaction tx = null;
        Usuaris usuari = null;
        String API_KEY = null;
        try {
            tx = session.beginTransaction();
            // Intenta trobar una configuració existent amb el nom donat
            Query<Usuaris> query = session.createQuery("FROM Usuaris WHERE telefon = :telefon AND codi_validacio = :codi_validacio", Usuaris.class);
            query.setParameter("telefon", telefon);
            query.setParameter("codi_validacio", codi_validacio);
            usuari = query.uniqueResult();
            // Si no es troba, crea una nova configuració
            if (usuari == null) {
                logger.info("Usuari amb telefon: {} no existeix o el codi introduit es invalid", telefon);
                return null;
            } else {
                String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
                StringBuilder apiKey = new StringBuilder();
                Random random = new Random();
                for (int i = 0; i < 16; i++) {
                    int index = random.nextInt(characters.length());
                    apiKey.append(characters.charAt(index));
                }

                API_KEY = apiKey.toString();
                usuari.setAPI_KEY(API_KEY);
                session.update(usuari); 
                tx.commit();
                logger.info("API_KEY {} per al usuari amb telefon {} generada correctament", API_KEY, telefon);
            }
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            logger.error("Error al validar l'usuari", e);
        } finally {
            session.close();
        }
        return API_KEY;
    }


    public static String loginUsuari(String email, String password) {
        Session session = SessionFactoryManager.getSessionFactory().openSession();
        Transaction tx = null;
        Usuaris usuari = null;
        String API_KEY = null;
        try {
            tx = session.beginTransaction();
            // Intenta trobar una configuració existent amb el nom donat
            Query<Usuaris> query = session.createQuery("FROM Usuaris WHERE email = :email AND password = :password", Usuaris.class);
            query.setParameter("email", email);
            query.setParameter("password", password);
            usuari = query.uniqueResult();
            // Si no es troba, crea una nova configuració
            if (usuari == null) {
                logger.info("Usuari amb email: {} no existeix o la contrasenya es incorrecte", email);
                return null;
            } else {
                API_KEY = usuari.getAPI_KEY();
                logger.info("Usuari amb email: validat correctament", email);
            }
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            logger.error("Error al validar l'usuari", e);
        } finally {
            session.close();
        }
        return API_KEY;
    }


    public static void consumirQuota(Usuaris usuari, Integer unitats) {
        Session session = SessionFactoryManager.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            usuari.setQuota(usuari.getQuota() - unitats);
            session.update(usuari); 
            tx.commit();
            logger.info("Quota de l'usuari actualitzada correctament. Quota restant: {}", usuari.getQuota());
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            logger.error("Error al restar quota al usuari", e);
        } finally {
            session.close();
        }
    }


    public static Usuaris[] getUsuarisList() {
        Session session = SessionFactoryManager.getSessionFactory().openSession();
        Transaction tx = null;
        List<Usuaris> userList = null;
    
        try {
            tx = session.beginTransaction();
    
            // Retrieve all users from the Usuaris table
            Query<Usuaris> query = session.createQuery("FROM Usuaris", Usuaris.class);
            userList = query.list();
    
            tx.commit();
            logger.info("Retrieved all users successfully");
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            logger.error("Error retrieving users", e);
        } finally {
            session.close();
        }
    
        return userList.toArray(new Usuaris[0]);
    }

}