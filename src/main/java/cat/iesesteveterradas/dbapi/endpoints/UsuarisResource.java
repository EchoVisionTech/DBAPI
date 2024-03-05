package cat.iesesteveterradas.dbapi.endpoints;

import cat.iesesteveterradas.dbapi.persistencia.GenericDAO;
import cat.iesesteveterradas.dbapi.persistencia.Peticions;
import cat.iesesteveterradas.dbapi.persistencia.PeticionsDAO;
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

@Path("/usuaris")
public class UsuarisResource {

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

            if (telefon == null || telefon.trim().isEmpty() || nickname == null || nickname.trim().isEmpty() || email == null || email.trim().isEmpty() || codi_validacio == null || codi_validacio.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("{\"status\":\"ERROR\",\"message\":\"Un valor introduit is invalid o buit.\"}").build();
            }

            Usuaris nouUsuari = UsuarisDAO.trobaORegistreUsuaris(telefon, nickname, email, codi_validacio);

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

            // Retorna la resposta
            String prettyJsonResponse = jsonResponse.toString(4); // 4 espais per indentar
            return Response.ok(prettyJsonResponse).build();
        } catch (Exception e) {
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
            return Response.ok(prettyJsonResponse).build();
        } catch (Exception e) {
            return Response.serverError().entity("{\"status\":\"ERROR\",\"message\":\"Error en afegir el usuari a la base de dades\"}").build();
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
            return Response.ok(prettyJsonResponse).build();
        } catch (Exception e) {
            return Response.serverError().entity("{\"status\":\"ERROR\",\"message\":\"Error en afegir la resposta a la base de dades\"}").build();
        }
    }


    @GET
    @Path("/admin_obtenir_llista")
    @Produces(MediaType.APPLICATION_JSON)
    public Response AdminObtenirLlista(@HeaderParam("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("{\"status\":\"ERROR\",\"message\":\"Clau API no vàlida.\"}").build();
        }
        String token = authHeader.substring(7);

        Usuaris usuari = GenericDAO.validateApiKeyAdmin(token);
        if (usuari == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("{\"status\":\"ERROR\",\"message\":\"Clau API no vàlida.\"}").build();
        }

        try {
            Usuaris[] usuarisList = UsuarisDAO.getUsuarisList();


            // Crea l'objecte JSON principal que inclou la llista de configuracions
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("status", "OK");
            jsonResponse.put("message", "Llista de configuracions");
            JSONObject[] dataList = new JSONObject[usuarisList.length];

            for (int i = 0; i < usuarisList.length; i++) {
                Usuaris user = usuarisList[i];
                // Create a JSONObject to hold the details of each Usuaris object
                JSONObject userJson = new JSONObject();
            
                userJson.put("nickname", user.getNickname());
                userJson.put("email", user.getEmail());
                userJson.put("telefon", user.getTelefon());
                Boolean validat = user.getAPI_KEY().isEmpty();
                userJson.put("validat", validat);
                userJson.put("pla", user.getPla().getPlaName());
                userJson.put("grups", user.getGrup().getgrupName());
            
                JSONObject quotaJson = new JSONObject();
                quotaJson.put("total", user.getPla().getQuota());
                quotaJson.put("consumida", user.getPla().getQuota() - user.getQuota());
                quotaJson.put("disponible", user.getQuota());
                userJson.put("quota", quotaJson);
            
                // Add the userJson object to the dataList array
                dataList[i] = userJson;
            }
            jsonResponse.put("data", dataList);

            // Converteix l'objecte JSON a una cadena amb pretty print (indentFactor > 0)
            String prettyJsonResponse = jsonResponse.toString(4); // 4 espais per indentar
            return Response.ok(prettyJsonResponse).build(); // Retorna l'objecte JSON com a resposta
        } catch (Exception e) {
            return Response.serverError().entity("Error en obtenir la llista de configuracions").build();
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
            return Response.ok(prettyJsonResponse).build();
        } catch (Exception e) {
            return Response.serverError().entity("{\"status\":\"ERROR\",\"message\":\"Error en autenticar el usuari\"}").build();
        }
    }
}

