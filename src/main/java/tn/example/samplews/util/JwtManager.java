package tn.example.samplews.util;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import tn.example.samplews.controllers.Role;

@Singleton
@LocalBean
@Startup
public class JwtManager {

    public String generateJWT(String username, Role[] roles) {

    }
}
