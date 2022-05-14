package tn.example.samplews.controllers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestRoles {
    @Test
    public void testRoles(){
        System.out.println(Role.Administrator.getValue() | Role.Surfer.getValue());
        assertEquals(17L, Role.Administrator.getValue() | Role.Surfer.getValue());
    }
}
