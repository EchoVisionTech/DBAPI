package cat.iesesteveterradas.dbapi.persistencia;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericDAO {
    private static final Logger logger = LoggerFactory.getLogger(GenericDAO.class);

   public static <T> T getById(Class<? extends T> clazz, long id){
        Session session = SessionFactoryManager.getSessionFactory().openSession();
        Transaction tx = null;
        T obj = null;
        try {
            tx = session.beginTransaction();
            obj = clazz.cast(session.get(clazz, id)); 
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            logger.error("Error en obtenir l'element per id ", e);
        } finally {
            session.close(); 
        }
        return obj;
    }

    public static <T> void delete(Class<? extends T> clazz, Serializable id) {
        Session session = SessionFactoryManager.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            T obj = clazz.cast(session.get(clazz, id)); 
            session.delete(obj); 
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            //e.printStackTrace();
            logger.error("Error borrar la taula ", e);
        } finally {
            session.close(); 
        }
    }

    public static <T> Collection<?> listCollection(Class<? extends T> clazz) {
        return listCollection(clazz, "");
    }

    public static <T> Collection<?> listCollection(Class<? extends T> clazz, String where) {
        Session session = SessionFactoryManager.getSessionFactory().openSession();
        Transaction tx = null;
        Collection<?> result = null;
        try {
            tx = session.beginTransaction();
            if (where.length() == 0) {
                result = session.createQuery("FROM " + clazz.getName()).list(); 
            } else {
                result = session.createQuery("FROM " + clazz.getName() + " WHERE " + where).list();
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            //e.printStackTrace();
            logger.error("Error al buscar la lista ", e);
        } finally {
            session.close(); 
        }
        return result;
    }

    public static <T> String collectionToString(Class<? extends T> clazz, Collection<?> collection){
        String txt = "";
        for(Object obj: collection) {
            T cObj = clazz.cast(obj);
            txt += "\n" + cObj.toString();
        }
        if (txt.substring(0, 1).compareTo("\n") == 0) {
            txt = txt.substring(1);
        }
        return txt;
    }
    
    public static Usuaris validateApiKey(String token) {
        Session session = SessionFactoryManager.getSessionFactory().openSession();
        Transaction tx = null;
        Usuaris usuari = null;
        try {
            tx = session.beginTransaction();
            // Intenta trobar una configuració existent amb el nom donat
            Query<Usuaris> query = session.createQuery("FROM Usuaris WHERE API_KEY = :token", Usuaris.class);
            query.setParameter("token", token);
            usuari = query.uniqueResult();
            // Si no es troba, crea una nova configuració
            if (usuari == null) {
                logger.info("Usuari amb la API {} no existeix.", token);
                return null;
            } else {
                logger.info("Usuari amb la API {} existeix.", token);
            }
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            logger.error("Error al crear o trobar la petició", e);
        } finally {
            session.close();
        }
        return usuari;
    }

    public static Usuaris validateApiKeyAdmin(String token) {
        Session session = SessionFactoryManager.getSessionFactory().openSession();
        Transaction tx = null;
        Usuaris usuari = null;
        try {
            tx = session.beginTransaction();
            // Intenta trobar una configuració existent amb el nom donat
            Query<Usuaris> query = session.createQuery("FROM Usuaris WHERE API_KEY = :token", Usuaris.class);
            query.setParameter("token", token);
            usuari = query.uniqueResult();
            // Si no es troba, crea una nova configuració
            if (usuari == null) {
                logger.info("Usuari amb la API {} no existeix.", token);
                return null;
            } else {
                Long grupUsuari = usuari.getGrup().getId();
                if (grupUsuari == 2) {
                    logger.info("Usuari amb la API {} existeix i es administrador.", token);
                } else {
                    logger.info("Usuari amb la API {} no es administrador.", token);
                    return null;
                }   
            }
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            logger.error("Error al crear o trobar la petició", e);
        } finally {
            session.close();
        }
        return usuari;
    }


    public static Pla getDefaultPla() {
        Session session = SessionFactoryManager.getSessionFactory().openSession();
        Transaction tx = null;
        Pla pla = null;
        try {
            tx = session.beginTransaction();
            // Intenta trobar una configuració existent amb el nom donat
            Query<Pla> query = session.createQuery("FROM Pla WHERE id = :id", Pla.class);
            query.setParameter("id", 1);
            pla = query.uniqueResult();
            // Si no es troba, crea una nova configuració
            if (pla == null) {
                logger.info("Pla invalid");
                return null;
            }
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            logger.error("Error al crear o trobar la petició", e);
        } finally {
            session.close();
        }
        return pla;
    }


    public static Grup getDefaultGrup() {
        Session session = SessionFactoryManager.getSessionFactory().openSession();
        Transaction tx = null;
        Grup grup = null;
        try {
            tx = session.beginTransaction();
            // Intenta trobar una configuració existent amb el nom donat
            Query<Grup> query = session.createQuery("FROM Grup WHERE id = :id", Grup.class);
            query.setParameter("id", 1);
            grup = query.uniqueResult();
            // Si no es troba, crea una nova configuració
            if (grup == null) {
                logger.info("Grup invalid");
                return null;
            }
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            logger.error("Error al crear o trobar la petició", e);
        } finally {
            session.close();
        }
        return grup;
    }



}
