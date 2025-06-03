package com.example.ecommerce.other;

import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Optional;

@UtilityClass
public class Utils {

    public Optional<String> getCurrentUserId() {
        return getClaim("id"); //come fare il getTokenNode ma non prende le eccezioni in caso null
    }

    public Optional<String> getEmail() {
        return getClaim("email");
    }

    public List<String> getRoles() {
        Jwt jwt = getJwt();
        return jwt.getClaimAsStringList("realm_access.roles");
    }

    public Optional<String> getClaim(String claimName) {
        Jwt jwt = getJwt();
        if (jwt != null && jwt.hasClaim(claimName)) {
            return Optional.ofNullable(jwt.getClaimAsString(claimName));
        }
        return Optional.empty();
    }

    private Jwt getJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt;
        }
        return null;
    }
}
