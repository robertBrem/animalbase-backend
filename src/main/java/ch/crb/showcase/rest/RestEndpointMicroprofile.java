package ch.crb.showcase.rest;


import java.security.Principal;
import java.util.Optional;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.wildfly.security.http.oidc.OidcPrincipal;
import org.wildfly.security.http.oidc.OidcSecurityContext;

//@ApplicationScoped
//@Path("/example")
public class RestEndpointMicroprofile {

//  @Inject
//  private Principal principal;
//
//// Microprofile Option, can be used with authMethod = "MP-JWT"
//  @Inject
//  private JsonWebToken jsonWebToken;
//
//  @Inject
//  @Claim("customerId")
//  private Optional<String> customerId;
//
//  @PermitAll
//  @GET
//  public String unprotectedEndpoint(@Context SecurityContext securityContext) {
//    return "Non Protected Endpoint, access allowed";
//  }
//
//  @GET
//  @Path("/user")
//  @RolesAllowed({"crb-role-user"})
//  public String userEndpoint(@Context SecurityContext securityContext) {
//
//    return "Secured Endpoint for role 'crb-role-user' access allowed for " + securityContext.getUserPrincipal() + " with customerId: " + customerId;
//  }
//
//  @GET
//  @Path("/admin")
//  @RolesAllowed({"crb-role-admin"})
//  public String adminEndpoint(@Context SecurityContext securityContext) {
//    return "Secured Endpoint for role 'crb-role-admin' access allowed for " + securityContext.getUserPrincipal();
//  }
}
