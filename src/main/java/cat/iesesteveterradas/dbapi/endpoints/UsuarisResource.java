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

            if (telefon == null || telefon.trim().isEmpty() || nickname == null || nickname.trim().isEmpty() || email == null || email.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("{\"status\":\"ERROR\",\"message\":\"Un valor introduit is invalid o buit.\"}").build();
            }

            Usuaris nouUsuari = UsuarisDAO.trobaORegistreUsuaris(telefon, nickname, email);

            // Prepara la resposta amb la nova configuració
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("status", "OK");
            jsonResponse.put("message", "Usuari registrat o trobat amb èxit");
            JSONObject jsonData = new JSONObject();
            jsonData.put("id", nouUsuari.getId());
            jsonData.put("telefon", nouUsuari.getTelefon());
            jsonData.put("nickname", nouUsuari.getNickname());
            jsonData.put("email", nouUsuari.getEmail());
            jsonResponse.put("data", jsonData);

            // Retorna la resposta
            String prettyJsonResponse = jsonResponse.toString(4); // 4 espais per indentar
            return Response.ok(prettyJsonResponse).build();
        } catch (Exception e) {
            return Response.serverError().entity("{\"status\":\"ERROR\",\"message\":\"Error en afegir el usuari a la base de dades\"}").build();
        }
    }
}

