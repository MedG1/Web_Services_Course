package tn.example.samplews;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.logging.Logger;

@ApplicationPath("/rest-api")
public class JaxRsActivator extends Application {
    @ApplicationScoped
    public static final class CDIConfigurator{
        @Produces
        @PersistenceContext(unitName = "default")
        private EntityManager entityManager;

        public void disposeEntityManager(@Disposes EntityManager entityManager){
            entityManager.close();
        }

        @Produces
        @Dependent
        public Logger getLogger(InjectionPoint ip){
            return Logger.getLogger(ip.getBean().getBeanClass().getName());
        }

        public void disposeLogger(@Disposes Logger log){
            log.info("Logger disposed!");
        }
    }


}
