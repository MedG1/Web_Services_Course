package tn.example.samplews.boundaries;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/echoes")
public class EchoesEndpoint {

    @GET
    @Path("/{message}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response echo(@PathParam("message") String message){
        return Response.ok().entity("{\"echo\":\""+message+"\"}").build();
    }
}
