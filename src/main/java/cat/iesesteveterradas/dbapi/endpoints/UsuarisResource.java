package cat.iesesteveterradas.dbapi.endpoints;

import cat.iesesteveterradas.dbapi.persistencia.Peticions;
import cat.iesesteveterradas.dbapi.persistencia.PeticionsDAO;
import cat.iesesteveterradas.dbapi.persistencia.Usuaris;
import cat.iesesteveterradas.dbapi.persistencia.UsuarisDAO;
import cat.iesesteveterradas.dbapi.respostes.RespostaBasica;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Base64;

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

            // Prepara la resposta amb la nova configuració
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
            return Response.serverError().entity("{\"status\":\"ERROR\",\"message\":\"Error en afegir el usuari a la base de dades\"}").build();
        }
    }
}

