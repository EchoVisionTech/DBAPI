package cat.iesesteveterradas.dbapi.endpoints;

import cat.iesesteveterradas.dbapi.persistencia.Configuracio;
import cat.iesesteveterradas.dbapi.persistencia.ConfiguracioDAO;
import cat.iesesteveterradas.dbapi.persistencia.GenericDAO;
import cat.iesesteveterradas.dbapi.persistencia.Propietat;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

@Path("/exemples/configuracions")
public class ExemplesConfiguracioResource {
    @GET
    @Path("/llistar_configuracions")
    @Produces(MediaType.APPLICATION_JSON)
    public Response llistarConfiguracions() {
        try {
            // Utilitza el mètode listCollection de Manager per obtenir totes les configuracions
            List<?> configuracions = (List<?>) GenericDAO.listCollection(Configuracio.class);
            JSONArray jsonConfiguracions = new JSONArray();

            for (Object obj : configuracions) {
                Configuracio configuracio = (Configuracio) obj;
                JSONObject jsonConfiguracio = new JSONObject();
                jsonConfiguracio.put("id", configuracio.getId());
                jsonConfiguracio.put("nom", configuracio.getNom());

                JSONArray jsonPropietats = new JSONArray();
                for (Propietat propietat : configuracio.getPropietats()) {
                    JSONObject jsonPropietat = new JSONObject();
                    jsonPropietat.put("clau", propietat.getClau());
                    jsonPropietat.put("valor", propietat.getValor());
                    jsonPropietats.put(jsonPropietat);
                }

                jsonConfiguracio.put("propietats", jsonPropietats);
                jsonConfiguracions.put(jsonConfiguracio);
            }

            // Crea l'objecte JSON principal que inclou la llista de configuracions
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("status", "OK");
            jsonResponse.put("message", "Llista de configuracions");
            jsonResponse.put("data", jsonConfiguracions);

            // Converteix l'objecte JSON a una cadena amb pretty print (indentFactor > 0)
            String prettyJsonResponse = jsonResponse.toString(4); // 4 espais per indentar
            return Response.ok(prettyJsonResponse).build(); // Retorna l'objecte JSON com a resposta
        } catch (Exception e) {
            return Response.serverError().entity("Error en obtenir la llista de configuracions").build();
        }
    }

    @POST
    @Path("/afegir_configuracio")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response afegirConfiguracio(String jsonInput) {
        try {
            JSONObject input = new JSONObject(jsonInput);
            String nom = input.optString("nom", null);

            if (nom == null || nom.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("{\"status\":\"ERROR\",\"message\":\"Nom de configuració requerit\"}").build();
            }

            // Utilitza el mètode trobaOCreaConfiguracioPerNom de Manager per crear o trobar la configuració
            Configuracio novaConfiguracio = ConfiguracioDAO.trobaOCreaConfiguracioPerNom(nom);

            // Prepara la resposta amb la nova configuració
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("status", "OK");
            jsonResponse.put("message", "Configuració afegida o trobada amb èxit");
            JSONObject jsonData = new JSONObject();
            jsonData.put("id", novaConfiguracio.getId());
            jsonData.put("nom", novaConfiguracio.getNom());
            jsonResponse.put("data", jsonData);

            // Retorna la resposta
            String prettyJsonResponse = jsonResponse.toString(4); // 4 espais per indentar
            return Response.ok(prettyJsonResponse).build();
        } catch (Exception e) {
            return Response.serverError().entity("{\"status\":\"ERROR\",\"message\":\"Error en afegir la configuració\"}").build();
        }
    }

    @PUT
    @Path("/afegir_propietat_a_configuracio/{configuracioId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response afegirPropietatAConfiguracio(@PathParam("configuracioId") Long configuracioId, String jsonInput) {
        try {
            JSONObject input = new JSONObject(jsonInput);
            String clau = input.optString("clau", null);
            String valor = input.optString("valor", null);

            if (clau == null || clau.trim().isEmpty() || valor == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("{\"status\":\"ERROR\",\"message\":\"Clau i valor de la propietat requerits\"}").build();
            }

            Configuracio configuracio = GenericDAO.getById(Configuracio.class, configuracioId);
            if (configuracio == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("{\"status\":\"ERROR\",\"message\":\"Configuració no trobada\"}").build();
            }

            Propietat propietat = new Propietat(clau, valor);
            ConfiguracioDAO.afegeixPropietatAConfiguracio(propietat, configuracio);

            // Construeix resposta amb la configuració i les seves propietats
            JSONObject jsonConfiguracio = new JSONObject();
            jsonConfiguracio.put("id", configuracio.getId());
            jsonConfiguracio.put("nom", configuracio.getNom());

            JSONArray jsonPropietats = new JSONArray();
            for (Propietat p : configuracio.getPropietats()) {
                JSONObject jsonPropietat = new JSONObject();
                jsonPropietat.put("clau", p.getClau());
                jsonPropietat.put("valor", p.getValor());
                jsonPropietats.put(jsonPropietat);
            }

            jsonConfiguracio.put("propietats", jsonPropietats);

            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("status", "OK");
            jsonResponse.put("message", "Propietat afegida amb èxit a la configuració");
            jsonResponse.put("data", jsonConfiguracio);

            return Response.ok(jsonResponse.toString(4)).build();
        } catch (Exception e) {
            return Response.serverError().entity("{\"status\":\"ERROR\",\"message\":\"Error en afegir la propietat a la configuració\"}").build();
        }
    }
    
    @DELETE
    @Path("/eliminar_propietat_de_configuracio/{configuracioId}/{clau}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response eliminarPropietatDeConfiguracio(@PathParam("configuracioId") Long configuracioId, @PathParam("clau") String clau) {
        try {
            Configuracio configuracio = GenericDAO.getById(Configuracio.class, configuracioId);
            if (configuracio == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("{\"status\":\"ERROR\",\"message\":\"Configuració no trobada\"}").build();
            }

            boolean eliminat = ConfiguracioDAO.eliminarPropietatDeConfiguracio(clau, configuracio);
            if (!eliminat) {
                return Response.status(Response.Status.NOT_FOUND).entity("{\"status\":\"ERROR\",\"message\":\"Propietat no trobada o no es pot eliminar\"}").build();
            }

            // Construeix resposta confirmant l'eliminació
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("status", "OK");
            jsonResponse.put("message", "Propietat eliminada amb èxit de la configuració");

            return Response.ok(jsonResponse.toString(4)).build();
        } catch (Exception e) {
            return Response.serverError().entity("{\"status\":\"ERROR\",\"message\":\"Error en eliminar la propietat de la configuració\"}").build();
        }
    }    
}
