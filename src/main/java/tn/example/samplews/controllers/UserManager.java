package tn.example.samplews.controllers;

import jakarta.ejb.EJBException;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import tn.example.samplews.entities.User;
import tn.example.samplews.util.Argon2Utility;

import java.util.HashSet;
import java.util.Set;

@Stateless
@LocalBean
public class UserManager implements GenericDAO<User, Long>{
    @Inject
    private EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public Class<User> getEntityClass() {
        return User.class;
    }

    public User findByUsername(String username){
        TypedQuery<User> query = entityManager.createQuery("select u from User u where u.username = :username", User.class);
        query.setParameter("username", username);
        return query.getSingleResult();
    }

    public User authenticate(final String username, final String password) throws EJBException {
        User user = findByUsername(username);
        if(user != null && Argon2Utility.check(user.getPassword(), password.toCharArray())){
            return user;
        }
        throw new EJBException("Failed sign in with username: " + username + " [Unknown username or wrong password]");

    }

    public Role[] getRoles(User user) {
        Set<Role> roles = new HashSet<>();
        for(Role role: Role.values()){
            if((user.getPermissionLevel() & role.getValue()) != 0L){
                roles.add(role);
            }
        }
        return roles.toArray(new Role[0]);
    }

    //TODO: hasRole(), addRole(), removeRole()
}
