package cat.iesesteveterradas.dbapi.endpoints;

import cat.iesesteveterradas.dbapi.persistencia.Peticions;
import cat.iesesteveterradas.dbapi.persistencia.PeticionsDAO;
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

@Path("/api/peticions")
public class PeticionsResource {

    @POST
    @Path("/afegir")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response afegirPeticio(String jsonInput) {
        try {
            JSONObject input = new JSONObject(jsonInput);
            String model = input.optString("model", null);
            String prompt = input.optString("prompt", null);
            String imatges = input.optString("imatges", null);

            if (model == null || model.trim().isEmpty() || prompt == null || prompt.trim().isEmpty() || imatges == null || imatges.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("{\"status\":\"ERROR\",\"message\":\"Un valor introduit is invalid o buit.\"}").build();
            }

            Peticions novaPeticio = PeticionsDAO.trobaOCreaPeticions(model, prompt, imatges);

            // Prepara la resposta amb la nova configuració
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("status", "OK");
            jsonResponse.put("message", "Peticio afegida o trobada amb èxit");
            JSONObject jsonData = new JSONObject();
            jsonData.put("id", novaPeticio.getId());
            jsonData.put("model", novaPeticio.getModel());
            jsonData.put("prompt", novaPeticio.getPrompts());
            jsonData.put("imatges", novaPeticio.getImatges());
            jsonResponse.put("data", jsonData);

            // Retorna la resposta
            String prettyJsonResponse = jsonResponse.toString(4); // 4 espais per indentar
            return Response.ok(prettyJsonResponse).build();
        } catch (Exception e) {
            return Response.serverError().entity("{\"status\":\"ERROR\",\"message\":\"Error en afegir la peticio a la base de dades\"}").build();
        }
    }
}

