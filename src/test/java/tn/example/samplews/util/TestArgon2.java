package tn.example.samplews.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestArgon2 {
    @Test
    public void testHash(){
        assertTrue(Argon2Utility.check(Argon2Utility.hash("testpw".toCharArray()), "testpw".toCharArray()));
        assertFalse(Argon2Utility.check(Argon2Utility.hash("testpw".toCharArray()), "testpkw".toCharArray()));
    }
}
