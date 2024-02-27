package cat.iesesteveterradas.dbapi.endpoints;

import cat.iesesteveterradas.dbapi.persistencia.GenericDAO;
import cat.iesesteveterradas.dbapi.persistencia.Peticions;
import cat.iesesteveterradas.dbapi.persistencia.PeticionsDAO;
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

@Path("/peticions")
public class PeticionsResource {

    private static final String DIRECTORY_PATH = "/var/DBAPI/imatges/";

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
            String model = input.optString("model", null);
            String prompt = input.optString("prompt", null);
            JSONArray imatgesArray = input.optJSONArray("imatges");
            String[] imatges = null;
            String[] imatgesPath = null; 

            if (imatgesArray != null) {
                imatges = new String[imatgesArray.length()];
                imatgesPath = new String[imatgesArray.length()]; 
                for (int i = 0; i < imatgesArray.length(); i++) {
                    imatges[i] = imatgesArray.optString(i);

                    // Decode the base64 string to binary data
                    byte[] imageBytes = Base64.getDecoder().decode(imatges[i]);

                    // Write the binary data to a JPG file
                    String fileName = "image" + i + ".jpg";
                    java.nio.file.Path imagePath = Paths.get(DIRECTORY_PATH + fileName); 
                    Files.write(imagePath, imageBytes);

                    // Store the image path
                    imatgesPath[i] = imagePath.toString();
                }
            }

            if (model == null || model.trim().isEmpty() || prompt == null || prompt.trim().isEmpty() || imatges == null || imatges.length == 0) {
                return Response.status(Response.Status.BAD_REQUEST).entity("{\"status\":\"ERROR\",\"message\":\"Un valor introduit is invalid o buit.\"}").build();
            }

            // ChatGPT code here

            Peticions novaPeticio = PeticionsDAO.trobaOCreaPeticions(model, prompt, imatgesPath, usuari);

            // Prepare the response with the new configuration
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("status", "OK");
            jsonResponse.put("message", "Peticio afegida o trobada amb èxit");
            JSONObject jsonData = new JSONObject();
            jsonData.put("id", novaPeticio.getId());
            jsonResponse.put("data", jsonData);

            // Return the response
            String prettyJsonResponse = jsonResponse.toString(4); // 4 espais per indentar
            return Response.ok(prettyJsonResponse).build();
        } catch (Exception e) {
            return Response.serverError().entity("{\"status\":\"ERROR\",\"message\":\"Error en afegir la peticio a la base de dades\"}").build();
        }
    }
}
