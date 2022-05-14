package tn.example.samplews.boundaries;

import com.sun.net.httpserver.Headers;
import jakarta.ejb.EJB;
import jakarta.ejb.EJBException;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import tn.example.samplews.controllers.UserManager;
import tn.example.samplews.entities.User;
import tn.example.samplews.util.JwtManager;

import java.util.logging.Logger;

import static jakarta.ws.rs.core.HttpHeaders.WWW_AUTHENTICATE;

@Path("/")
@RequestScoped
public class AuthenticationEndpoint {
    @Inject
    private Logger log;
    @EJB
    private UserManager userManager;
    @EJB
    private JwtManager jwtManager;

    @POST
    @Path("/oauth/token")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response authenticate(@FormParam("username") String username, @FormParam("password") String password){
        log.info("Authenticating user with username: " + username + " ...");
        try{
            User user = userManager.authenticate(username, password);
            if(user != null){
                String token = jwtManager.generateJWT(username, userManager.getRoles(user));
                return Response.ok().entity("{\"accessToken\":\"" + token + "\"}").build();
            }
        } catch (EJBException e) {
            log.warning(e.getMessage());
        }
        return Response.status(Response.Status.UNAUTHORIZED).header(WWW_AUTHENTICATE, "Bearer realm=samplews").build();
    }
}
