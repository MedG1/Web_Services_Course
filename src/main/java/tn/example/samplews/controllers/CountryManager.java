package tn.example.samplews.controllers;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import tn.example.samplews.entities.Country;

@Stateless
@LocalBean
public class CountryManager implements GenericDAO<Country, Short>{
    @Inject
    private EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public Class<Country> getEntityClass() {
        return Country.class;
    }
}
