package tn.example.samplews.controllers;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import tn.example.samplews.entities.City;

@Stateless
@LocalBean
public class CityManager implements GenericDAO<City, Integer> {
    @Inject
    private EntityManager entityManager;
    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public Class<City> getEntityClass() {
        return City.class;
    }
}
