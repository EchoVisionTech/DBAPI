package cat.iesesteveterradas.dbapi;

import cat.iesesteveterradas.dbapi.persistencia.Usuaris;
import cat.iesesteveterradas.dbapi.persistencia.UsuarisDAO;
import cat.iesesteveterradas.dbapi.persistencia.Peticions;
import cat.iesesteveterradas.dbapi.persistencia.PeticionsDAO;
import cat.iesesteveterradas.dbapi.persistencia.SessionFactoryManager;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

public class AppMain {
    private static final Logger logger = LoggerFactory.getLogger(AppMain.class);
    // URI base on el servidor HTTP de Grizzly escoltarà
    private static String baseURI;

    public static void iniciarHibernate(){
        Usuaris usuari = UsuarisDAO.trobaORegistreUsuaris("650293516", "Nick", "test@correo.com");
        Peticions peticio = PeticionsDAO.trobaOCreaPeticions("mistral", "Test from database", "Img base64");
        
    }

    public static HttpServer iniciarServidorAPI(String host, int port) {
        // Construeix la URI de base utilitzant els paràmetres host i port
        baseURI = String.format("http://%s:%d/api/", host, port);

        // Configura Jersey indicant quin paquet ha d'escanejar cercant recursos
        // per la API
        final ResourceConfig rc = new ResourceConfig()
                .packages("cat.iesesteveterradas.dbapi");

        // Crea i inicia una nova instància del servidor http de Grizzly
        // exposant l'aplicació de Jersey en la URI construïda
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(baseURI), rc);
    }

    public static void main(String[] args) {
        Options options = new Options();

        Option hostOption = new Option("h", "host", true, "Adreça de l'host. Valor per defecte: 127.0.0.1");
        hostOption.setRequired(false); // No requerit, ja que tenim un valor per defecte
        options.addOption(hostOption);

        Option portOption = new Option("p", "port", true, "Número del port. Valor per defecte: 8080");
        portOption.setRequired(false); // No requerit, ja que tenim un valor per defecte
        options.addOption(portOption);

        // Crea l'analitzador i el formatador d'ajuda
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            // Analitza les opcions passades a la línia de comandes
            cmd = parser.parse(options, args);

            // Assigna valors per defecte si les opcions no estan presents
            String host = cmd.hasOption("host") ? cmd.getOptionValue("host") : "127.0.0.1";
            int port = cmd.hasOption("port") ? Integer.parseInt(cmd.getOptionValue("port")) : 8080;

            logger.info("Iniciant hibernate...");
            iniciarHibernate();

            logger.info("Iniciant el servidor...");
            final HttpServer server = iniciarServidorAPI(host, port);

            logger.info(String.format("App Jersey iniciada amb WADL disponible a "
                    + "%sapplication.wadl\nPrèmer \"Enter\" per aturar-lo...", baseURI));
            System.in.read();

            SessionFactoryManager.close();
            server.shutdownNow();
        } catch (ParseException e) {
            logger.error("S'ha produit un error a l'hora de processar els paràmetres del programa", e);
            formatter.printHelp("utility-name", options);
            System.exit(1);
        }
        catch (Exception e) {
            logger.error("S'ha produit un error en executar el servidor", e);
            System.exit(1);
        }
    }
}
