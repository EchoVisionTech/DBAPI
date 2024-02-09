package cat.iesesteveterradas.dbapi.persistencia;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.SessionFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionFactoryManager {
    private static final Logger logger = LoggerFactory.getLogger(SessionFactoryManager.class);
    private static SessionFactory factory;

    static {
        try {
            // 1. Create a StandardServiceRegistry with Hibernate configuration properties
            StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                    .configure() // Auto-detects entities and applies settings from annotations
                    .build();
            // 2. Create a Metadata object from the registry
            Metadata metadata = new MetadataSources(registry).buildMetadata();
            // 3. Build the SessionFactory from the metadata
            factory = metadata.getSessionFactoryBuilder().build();
        } catch (Throwable e) {
            logger.error("Error en crear un objecte de classe sessionFactory.", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return factory;
    }

    public static void close() {
        if (factory != null) {
            factory.close();
        }
    }
}