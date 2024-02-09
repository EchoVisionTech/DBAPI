package cat.iesesteveterradas.dbapi.endpoints;

import cat.iesesteveterradas.dbapi.respostes.RespostaBasica;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;


@Path("/exemples")
public class ExemplesResource {
    @GET
    @Path("/generar_objecte_a_json_automatic")
    @Produces(MediaType.APPLICATION_JSON)
    public RespostaBasica generarObjecteAJSONAutomatic() {
        // Crea un nou objecte RespostaBasica que volem retornar com a JSON
        RespostaBasica resposta = new RespostaBasica();
        resposta.setStatus("OK");
        resposta.setMessage("Missatge de prova en format JSON");
        return resposta; // JAX-RS s'encarrega de la conversió a JSON
    }

    @GET
    @Path("/generar_json_amb_jsonobject")
    @Produces(MediaType.APPLICATION_JSON)
    public Response generarJSONAmbJSONObject() {
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("status", "OK");
        jsonResponse.put("message", "Missatge de prova en format JSON");
        return Response.ok(jsonResponse.toString(), MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/generar_json_amb_jackson")
    @Produces(MediaType.APPLICATION_JSON)
    public Response generarJSONAmbJackson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonResponse = mapper.createObjectNode();
        jsonResponse.put("status", "OK");
        jsonResponse.put("message", "Missatge de prova en format JSON");
        String json = mapper.writeValueAsString(jsonResponse);
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/obtenir_imatge")
    @Produces("image/jpg") // Assegura't que el tipus MIME coincideix amb el format de la teva imatge
    public Response obtenirImatge() {
        // Obté el camí al fitxer en el directori de treball de l'usuari
        String rutaFitxer = System.getProperty("user.dir") + "/data/mario_base64";
        try {
            // Llegeix tot el contingut del fitxer com a string
            String imatgeBase64 = new String(Files.readAllBytes(Paths.get(rutaFitxer)));
            // Descodifica el string Base64 a dades binàries
            byte[] imatgeBytes = Base64.getDecoder().decode(imatgeBase64);
            // Retorna les dades de la imatge en la resposta
            return Response.ok(imatgeBytes).build();
        } catch (IOException e) {
            // En cas d'error, retorna un codi d'estat 500
            return Response.serverError().entity("Error llegint el fitxer de la imatge").build();
        }
    }
}
