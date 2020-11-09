package com.kamilamalikova.help.jwt;

import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class Jwt {
    public static Claims decodeJWT(String jwt) {
        //This line will throw an exception if it is not a signed JWS (as expected)
        Claims claims = Jwts
                .parser()
                .setSigningKey("securesecuresecuresecuresecuresecure".getBytes())
                .parseClaimsJws(jwt).getBody();
        return claims;
    }

    public static Date getExpirationDate(String jwt){
        Claims claims = decodeJWT(jwt);
        return claims.getExpiration();
    }
}
