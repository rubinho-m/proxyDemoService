package com.rubinho.vkproxy.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.rubinho.vkproxy.dto.UserDto;
import com.rubinho.vkproxy.services.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

@RequiredArgsConstructor
@Component
public class UserAuthProvider {
    @Value("${security.jwt.token.secret-key:secret-value}")
    private String secretKey;

    private final UserService userService;

    @PostConstruct
    protected void init(){
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(String email){
        Date now = new Date();
        Date validity = new Date(now.getTime() + 144_000_000);

        return JWT.create()
                .withIssuer(email)
                .withIssuedAt(now)
                .withExpiresAt(validity)
                .sign(Algorithm.HMAC256(secretKey));
    }

    public String getUsernameFromJwt(String token){
        return JWT.decode(token).getIssuer();
    }

    public Authentication validateToken(String token){
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secretKey))
                .build();
        DecodedJWT decoded = verifier.verify(token);

        UserDto user = userService.findByEmail(decoded.getIssuer());
        String email = user.getEmail();
        UserDetails userDetails = userService.getUserDetails(email);



        return new UsernamePasswordAuthenticationToken(user, null, userDetails.getAuthorities());
    }
}
