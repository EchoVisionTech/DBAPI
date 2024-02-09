package cat.iesesteveterradas.dbapi.endpoints;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/")
public class IniciResource {
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String inici() {
        return "Pàgina inicial de l'API (missatge en text pla)";
    }
}
