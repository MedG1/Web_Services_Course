package tn.example.samplews.util;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.Ed25519Signer;
import com.nimbusds.jose.crypto.Ed25519Verifier;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.jwk.gen.OctetKeyPairGenerator;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJBException;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import tn.example.samplews.controllers.Role;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Singleton
@LocalBean
@Startup
public class JwtManager {

    private final Config config = ConfigProvider.getConfig();
    private final Map<String, Long> keyPairExpirationTimes = new HashMap<>();
    private final Set<OctetKeyPair> cachedKeyPairs = new HashSet<>();
    private final Long keyPairLifetimeDuration = config.getValue("key.pair.lifetime.duration", Long.class);
    private final Short keyPairCacheSize = config.getValue("key.pair.cache.size", Short.class);
    private final Integer jwtLifetimeDuration = config.getValue("jwt.lifetime.duration", Integer.class);
    private final String issuer = config.getValue("jwt.issuer", String.class);
    private final List<String> audiences = config.getValues("jwt.audiences", String.class);
    private final String claimRoles = config.getValue("jwt.claim.roles", String.class);
    private final OctetKeyPairGenerator keyPairGenerator = new OctetKeyPairGenerator(Curve.Ed25519);

    @PostConstruct
    public void start(){
        while(cachedKeyPairs.size() < keyPairCacheSize){
            cachedKeyPairs.add(generateKeyPair());
        }
    }

    public String generateJWT(String username, Role[] roles) {
        try {
            OctetKeyPair kp = getKeyPair().orElseThrow(() -> new EJBException("Unable to retreive a valid Ed25519 key pair"));
            JWSSigner signer = new Ed25519Signer(kp);
            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.EdDSA)
                    .keyID(kp.getKeyID())
                    .type(JOSEObjectType.JWT)
                    .build();
            LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
            JWTClaimsSet payload = new JWTClaimsSet.Builder()
                    .issuer(issuer)
                    .audience(audiences)
                    .subject(username)
                    .claim("upn", username)
                    .claim(claimRoles, Arrays.deepToString(roles))
                    .jwtID(UUID.randomUUID().toString())
                    .issueTime(Date.from(now.toInstant(ZoneOffset.UTC)))
                    .notBeforeTime(Date.from(now.toInstant(ZoneOffset.UTC)))
                    .expirationTime(Date.from(now.toInstant(ZoneOffset.UTC).plus(jwtLifetimeDuration, ChronoUnit.SECONDS)))
                    .build();
            SignedJWT signedJwt = new SignedJWT(header, payload);
            signedJwt.sign(signer);
            return  signedJwt.serialize();
        } catch(JOSEException e) {
            throw new EJBException(e);
        }
    }

    public Optional<JWT> verifyJwt(String token){
        try {
            SignedJWT signedJwt = SignedJWT.parse(token);
            OctetKeyPair pk = cachedKeyPairs.stream().filter(kp -> kp.getKeyID().equals(signedJwt.getHeader().getKeyID()))
                    .findFirst()
                    .orElseThrow(() -> new EJBException("Unable to retreive the key pair associated with the given kid"))
                    .toPublicJWK();
            JWSVerifier verifier = new Ed25519Verifier(pk);
            if(signedJwt.verify(verifier)){
                JWT jwt = JWTParser.parse(token);
                // java.util.Date is legacy api
                if (jwt.getJWTClaimsSet().getExpirationTime().toInstant().isAfter(LocalDateTime.now(ZoneId.of("UTC")).toInstant(ZoneOffset.UTC))){
                    return Optional.empty();
                }
                return Optional.of(jwt);
            }
            return Optional.empty();
        } catch(ParseException | JOSEException e) {
            throw new EJBException(e);
        }
    }

    private OctetKeyPair generateKeyPair() {
        try{
            Long currentUTCSeconds = LocalDateTime.now(ZoneId.of("UTC")).toEpochSecond(ZoneOffset.UTC);
            String kid = UUID.randomUUID().toString();
            keyPairExpirationTimes.put(kid, currentUTCSeconds + keyPairLifetimeDuration);
            return keyPairGenerator.keyUse(KeyUse.SIGNATURE).keyID(kid).generate();
        } catch(JOSEException e) {
            throw new EJBException(e);
        }
    }

    private boolean isExpired(OctetKeyPair keyPair){
        Long currentUTCSeconds = LocalDateTime.now(ZoneId.of("UTC")).toEpochSecond(ZoneOffset.UTC);
        return currentUTCSeconds > keyPairExpirationTimes.get(keyPair.getKeyID());
    }

    private boolean isPublicKeyExpired(OctetKeyPair keyPair){
        Long currentUTCSeconds = LocalDateTime.now(ZoneId.of("UTC")).toEpochSecond(ZoneOffset.UTC);
        return currentUTCSeconds > ( keyPairExpirationTimes.get(keyPair.getKeyID()) + jwtLifetimeDuration);
    }

    private Optional<OctetKeyPair> getKeyPair(){
        cachedKeyPairs.removeIf(this::isPublicKeyExpired);
        while(cachedKeyPairs.stream().filter(kp -> !isExpired(kp)).count() < keyPairCacheSize){
            cachedKeyPairs.add(generateKeyPair());
        }
        return cachedKeyPairs.stream().filter(kp -> !isExpired(kp)).findAny();
    }

}
