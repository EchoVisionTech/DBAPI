package cat.iesesteveterradas.dbapi.endpoints;

import cat.iesesteveterradas.dbapi.persistencia.GenericDAO;
import cat.iesesteveterradas.dbapi.persistencia.Grup;
import cat.iesesteveterradas.dbapi.persistencia.Peticions;
import cat.iesesteveterradas.dbapi.persistencia.PeticionsDAO;
import cat.iesesteveterradas.dbapi.persistencia.Pla;
import cat.iesesteveterradas.dbapi.persistencia.Respostes;
import cat.iesesteveterradas.dbapi.persistencia.RespostesDAO;
import cat.iesesteveterradas.dbapi.persistencia.Usuaris;
import cat.iesesteveterradas.dbapi.persistencia.UsuarisDAO;
import cat.iesesteveterradas.dbapi.respostes.RespostaBasica;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Base64;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.mysql.cj.conf.PropertyKey.logger;

@Path("/usuaris")
public class UsuarisResource {

    private static final Logger logger = LoggerFactory.getLogger(GenericDAO.class);

    @POST
    @Path("/registrar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrarUsuari(String jsonInput) {
        try {
            JSONObject input = new JSONObject(jsonInput);
            String telefon = input.optString("telefon", null);
            String nickname = input.optString("nickname", null);
            String email = input.optString("email", null);
            String codi_validacio = input.optString("codi_validacio", null);
            logger.info("json leido");

            if (telefon == null || telefon.trim().isEmpty() || nickname == null || nickname.trim().isEmpty() || email == null || email.trim().isEmpty() || codi_validacio == null || codi_validacio.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("{\"status\":\"ERROR\",\"message\":\"Un valor introduit is invalid o buit.\"}").build();
            }

            Pla pla = GenericDAO.getDefaultPla();
            Grup grup = GenericDAO.getDefaultGrup();
            Integer quota = pla.getQuota();
            

            Usuaris nouUsuari = UsuarisDAO.trobaORegistreUsuaris(telefon, nickname, email, codi_validacio, pla, grup, quota);

            // Prepara la resposta amb la nova configuració
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("status", "OK");
            jsonResponse.put("message", "L'usuari s'ha creat correctament");
            JSONObject jsonData = new JSONObject();
            jsonData.put("telefon", nouUsuari.getTelefon());
            jsonData.put("nickname", nouUsuari.getNickname());
            jsonData.put("email", nouUsuari.getEmail());
            jsonData.put("codi_validacio", nouUsuari.getCodi_validacio());
            jsonResponse.put("data", jsonData);
            logger.info("json respuesta creada");

            // Retorna la resposta
            String prettyJsonResponse = jsonResponse.toString(4); // 4 espais per indentar
            logger.info("Nou usuari creat amb el telefon: {}, nickname: {}, email: {}, codi_validacio: {}", telefon, nickname, email, codi_validacio);
            return Response.ok(prettyJsonResponse).build();
        } catch (Exception e) {
            logger.info("Error al crear el nou usuari");
            return Response.serverError().entity("{\"status\":\"ERROR\",\"message\":\"Error en afegir el usuari a la base de dades\"}").build();
        }
    }


    @POST
    @Path("/validar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response ValidarUsuari(String jsonInput) {
        try {
            JSONObject input = new JSONObject(jsonInput);
            String telefon = input.optString("telefon", null);
            String codi_validacio = input.optString("codi_validacio", null);

            if (telefon == null || telefon.trim().isEmpty() || codi_validacio == null || codi_validacio.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("{\"status\":\"ERROR\",\"message\":\"Un valor introduit is invalid o buit.\"}").build();
            }

            String API_KEY = UsuarisDAO.validarUsuari(telefon, codi_validacio);

            // Prepara la resposta amb la nova configuració
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("status", "OK");
            jsonResponse.put("message", "L'usuari s'ha validat correctament");
            JSONObject jsonData = new JSONObject();
            jsonData.put("api_key", API_KEY);
            jsonResponse.put("data", jsonData);

            // Retorna la resposta
            String prettyJsonResponse = jsonResponse.toString(4); // 4 espais per indentar
            logger.info("Usuari amb telefon: {} validat correctament", telefon);
            return Response.ok(prettyJsonResponse).build();
        } catch (Exception e) {
            logger.info("Error al validar l'usuari");
            return Response.serverError().entity("{\"status\":\"ERROR\",\"message\":\"Error en afegir el usuari a la base de dades\"}").build();
        }
    }


    @GET
    @Path("/consultar_quota")
    @Produces(MediaType.APPLICATION_JSON)
    public Response ConsultarQuota(@HeaderParam("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("{\"status\":\"ERROR\",\"message\":\"Clau API no vàlida.\"}").build();
        }
        String token = authHeader.substring(7);

        Usuaris usuari = GenericDAO.validateApiKey(token);
        if (usuari == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("{\"status\":\"ERROR\",\"message\":\"Clau API no vàlida.\"}").build();
        }

        try {
            // Crea l'objecte JSON principal que inclou la llista de configuracions
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("status", "OK");
            jsonResponse.put("message", "Quota consultada correctament");
            JSONObject jsonData = new JSONObject();
            jsonData.put("pla", usuari.getPla().getPlaName());
            JSONObject jsonQuota = new JSONObject();
            jsonQuota.put("total", usuari.getPla().getQuota());
            jsonQuota.put("consumida", usuari.getPla().getQuota() - usuari.getQuota());
            jsonQuota.put("disponible", usuari.getQuota());
            jsonData.put("quota", jsonQuota);
            jsonResponse.put("data", jsonData);

            // Converteix l'objecte JSON a una cadena amb pretty print (indentFactor > 0)
            String prettyJsonResponse = jsonResponse.toString(4); // 4 espais per indentarç
            logger.info("Informacio de la quota obtinguda correctament");
            return Response.ok(prettyJsonResponse).build(); // Retorna l'objecte JSON com a resposta
        } catch (Exception e) {
            logger.info("Error al obtenir la informacio de la quota");
            return Response.serverError().entity("Error al obtenir la informacio de la quota").build();
        }
    }


    @POST
    @Path("/consumir_quota")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response ConsumirQuotaUsuari(@HeaderParam("Authorization") String authHeader, String jsonInput) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("{\"status\":\"ERROR\",\"message\":\"Clau API no vàlida.\"}").build();
        }
        String token = authHeader.substring(7);

        Usuaris usuari = GenericDAO.validateApiKey(token);
        if (usuari == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("{\"status\":\"ERROR\",\"message\":\"Clau API no vàlida.\"}").build();
        }

        try {
            JSONObject input = new JSONObject(jsonInput);
            Integer unitats = input.optInt("unitats", 0);

            if (unitats == 0) {
                return Response.status(Response.Status.BAD_REQUEST).entity("{\"status\":\"ERROR\",\"message\":\"Un valor introduit is invalid o buit.\"}").build();
            }

            if (usuari.getQuota() <= 0) {
                return Response.status(Response.Status.BAD_REQUEST).entity("{\"status\":\"ERROR\",\"message\":\"429\"}").build();
            }

            UsuarisDAO.consumirQuota(usuari, unitats);

            // Prepare the response with the new configuration
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("status", "OK");
            jsonResponse.put("message", "Quota actualitzada correctament");
            JSONObject jsonData = new JSONObject();
            jsonData.put("pla", usuari.getPla().getPlaName());
            JSONObject jsonQuota = new JSONObject();
            jsonQuota.put("total", usuari.getPla().getQuota());
            jsonQuota.put("consumida", usuari.getPla().getQuota() - usuari.getQuota());
            jsonQuota.put("disponible", usuari.getQuota());
            jsonData.put("quota", jsonQuota);
            jsonResponse.put("data", jsonData);

            // Return the response
            String prettyJsonResponse = jsonResponse.toString(4); // 4 espais per indentar
            logger.info("Quota de l'usuari actualitzada correctament. Quota restant: {}", usuari.getQuota());
            return Response.ok(prettyJsonResponse).build();
        } catch (Exception e) {
            logger.info("Error al modificar la quota de l'usuari");
            return Response.serverError().entity("{\"status\":\"ERROR\",\"message\":\"Error al modificar la quota de l'usuari\"}").build();
        }
    }


    @POST
    @Path("/admin_obtenir_llista")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response AdminObtenirLlista(@HeaderParam("Authorization") String authHeader, String jsonInput) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("{\"status\":\"ERROR\",\"message\":\"Clau API no vàlida.\"}").build();
        }
        
        String token = authHeader.substring(7);
        Usuaris usuari = GenericDAO.validateApiKeyAdmin(token);
        logger.info("Despues guardar usuario");
        if (usuari == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("{\"status\":\"ERROR\",\"message\":\"Clau API no vàlida.\"}").build();
        }
        logger.info("Despues verificar usuario");

        try {
            logger.info("Antes de user list");
            Usuaris[] usuarisList = UsuarisDAO.getUsuarisList();
            logger.info("Despues de user list");
            // Crea l'objecte JSON principal que inclou la llista de configuracions
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("status", "OK");
            jsonResponse.put("message", "Llista de configuracions");
            JSONObject[] dataList = new JSONObject[usuarisList.length];

            for (int i = 0; i < usuarisList.length; i++) {
                Usuaris user = usuarisList[i];
                // Create a JSONObject to hold the details of each Usuaris object
                JSONObject userJson = new JSONObject();
                logger.info("Checkpoint 1");
                userJson.put("nickname", user.getNickname());
                userJson.put("email", user.getEmail());
                userJson.put("telefon", user.getTelefon());

                logger.info("Checkpoint 2");
                Boolean validat = user.getAPI_KEY().isEmpty();
                userJson.put("validat", validat);
                userJson.put("pla", user.getPla().getPlaName());
                userJson.put("grups", user.getGrup().getgrupName());
                
                logger.info("Checkpoint 3");
                JSONObject quotaJson = new JSONObject();

                logger.info("Checkpoint 4");
                quotaJson.put("total", user.getPla().getQuota());
                quotaJson.put("consumida", user.getPla().getQuota() - user.getQuota());
                quotaJson.put("disponible", user.getQuota());
                
                logger.info("Checkpoint 5");
                userJson.put("quota", quotaJson);
            
                // Add the userJson object to the dataList array
                dataList[i] = userJson;
                System.out.println(dataList);
            }
            jsonResponse.put("data", dataList);

            // Converteix l'objecte JSON a una cadena amb pretty print (indentFactor > 0)
            String prettyJsonResponse = jsonResponse.toString(4); // 4 espais per indentarç
            logger.info("Llista de usuaris obtinguda correctament");
            return Response.ok(prettyJsonResponse).build(); // Retorna l'objecte JSON com a resposta
        } catch (Exception e) {
            logger.info("Error al obtenir la llista de usuaris");
            return Response.serverError().entity("Error en obtenir la llista de usuaris").build();
        }
    }


    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response LoginUsuari(String jsonInput) {
        try {
            JSONObject input = new JSONObject(jsonInput);
            String email = input.optString("email", null);
            String password = input.optString("password", null);

            if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("{\"status\":\"ERROR\",\"message\":\"Un valor introduit is invalid o buit.\"}").build();
            }
            String API_KEY = UsuarisDAO.loginUsuari(email, password);

            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("status", "OK");
            jsonResponse.put("message", "Usuari autenticat correctament");
            JSONObject jsonData = new JSONObject();
            jsonData.put("api_key", API_KEY);
            jsonResponse.put("data", jsonData);

            // Retorna la resposta
            String prettyJsonResponse = jsonResponse.toString(4); // 4 espais per indentar
            logger.info("Usuari amb email: {} autenticat correctament", email);
            return Response.ok(prettyJsonResponse).build();
        } catch (Exception e) {
            logger.info("Error al autenticar l'usuari");
            return Response.serverError().entity("{\"status\":\"ERROR\",\"message\":\"Error en autenticar el usuari\"}").build();
        }
    }
}

