package tn.example.samplews.controllers;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import tn.example.samplews.entities.Address;

@Stateless
@LocalBean
public class AddressManager implements GenericDAO<Address, Long>{
    @Inject
    private EntityManager entityManager;
    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public Class<Address> getEntityClass() {
        return Address.class;
    }
}
