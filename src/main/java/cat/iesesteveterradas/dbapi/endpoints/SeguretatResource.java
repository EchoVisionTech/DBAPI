package cat.iesesteveterradas.dbapi.endpoints;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/exemples")
public class SeguretatResource {

    @GET
    @Path("/validar-token")
    @Produces(MediaType.APPLICATION_JSON)
    public Response validarTokenBearer(@HeaderParam("Authorization") String authHeader) {
        // Comprova si l'header d'autorització és nul o no comença amb "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Token d'autorització absent o mal format").build();
        }

        // Extreu el token sense el prefix "Bearer "
        String token = authHeader.substring(7); // Elimina "Bearer " del principi

        // Aquí pots afegir la teva lògica per validar el token
        boolean isValidToken = validarToken(token); // Implementa aquest mètode segons la teva lògica de validació

        if (!isValidToken) {
            // Si el token no és vàlid, retorna un codi d'estat 401 Unauthorized
            return Response.status(Response.Status.UNAUTHORIZED).entity("Token no vàlid").build();
        }

        // Si el token és vàlid, retorna una resposta positiva
        return Response.ok().entity("Token vàlid! Accés concedit.").build();
    }

    private boolean validarToken(String token) {
        // Aquesta és una funció fictícia. Hauries de substituir-la amb la teva pròpia lògica de validació del token.
        return "elTeuTokenSecret".equals(token); // Aquesta comparació és només un exemple
    }
}
