package fr.miage.choquert.security;

import fr.miage.choquert.entities.account.Account;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.springframework.security.core.Authentication;

import java.util.Map;

public class AccountMatcher {

    public static boolean isAccountOwner(Account account, Authentication authentication) {
        try {
            if (authentication.getPrincipal() instanceof KeycloakPrincipal) {
                KeycloakPrincipal<KeycloakSecurityContext> kp = (KeycloakPrincipal<KeycloakSecurityContext>) authentication.getPrincipal();
                AccessToken token = kp.getKeycloakSecurityContext().getToken();
                Map<String, Object> otherClaims = token.getOtherClaims();
                if (otherClaims.containsKey("secret")) {
                    return account.getSecret().equals(otherClaims.get("secret"));
                }
            }
        } catch (Exception ignored){}

        return false;
    }

}
