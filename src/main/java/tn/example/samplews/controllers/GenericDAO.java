package tn.example.samplews.controllers;

import tn.example.samplews.entities.RootEntity;

import jakarta.persistence.EntityManager;
import java.io.Serializable;

public interface GenericDAO<E extends RootEntity<ID>, ID extends Serializable> {
    EntityManager getEntityManager();
    Class<E> getEntityClass();
}
