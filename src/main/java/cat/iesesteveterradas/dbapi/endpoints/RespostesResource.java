package cat.iesesteveterradas.dbapi.endpoints;

import cat.iesesteveterradas.dbapi.persistencia.GenericDAO;
import cat.iesesteveterradas.dbapi.persistencia.Peticions;
import cat.iesesteveterradas.dbapi.persistencia.PeticionsDAO;
import cat.iesesteveterradas.dbapi.persistencia.Respostes;
import cat.iesesteveterradas.dbapi.persistencia.RespostesDAO;
import cat.iesesteveterradas.dbapi.persistencia.Usuaris;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

@Path("/respostes")
public class RespostesResource {

    @POST
    @Path("/afegir")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response afegirPeticio(@HeaderParam("Authorization") String authHeader, String jsonInput) {
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
            int id_peticio = input.optInt("id_peticio", 0);
            String text_generat = input.optString("text_generat", null);

            if (id_peticio == 0 || text_generat == null || text_generat.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("{\"status\":\"ERROR\",\"message\":\"Un valor introduit is invalid o buit.\"}").build();
            }

            Peticions peticio = PeticionsDAO.getPeticio(id_peticio);

            Respostes novaResposta = RespostesDAO.trobaOCreaRespostes(text_generat, peticio, usuari);

            // Prepare the response with the new configuration
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("status", "OK");
            jsonResponse.put("message", "Resposta registrada correctament");
            JSONObject jsonData = new JSONObject();
            jsonData.put("id", novaResposta.getId());
            jsonData.put("id_peticio", novaResposta.getPeticio().getId());
            jsonResponse.put("data", jsonData);

            // Return the response
            String prettyJsonResponse = jsonResponse.toString(4); // 4 espais per indentar
            return Response.ok(prettyJsonResponse).build();
        } catch (Exception e) {
            return Response.serverError().entity("{\"status\":\"ERROR\",\"message\":\"Error en afegir la resposta a la base de dades\"}").build();
        }
    }
}
