package ch.crb.showcase.animals.boundary;


import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.stream.JsonCollectors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import ch.crb.showcase.animals.entity.Animal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wildfly.security.http.oidc.OidcPrincipal;
import org.wildfly.security.http.oidc.OidcSecurityContext;

@Stateless
@Path("animals")
public class AnimalsResource {
  private static Logger log = LoggerFactory.getLogger(AnimalsResource.class);

  @Inject
  private Principal principal;

  @PersistenceContext
  EntityManager em;

  @Context
  UriInfo uriInfo;


  @GET
  @PermitAll
  public JsonArray getAnimals() {
    return em.createNamedQuery(Animal.FIND_ALL, Animal.class)
            .getResultList()
            .stream()
            .map(Animal::toOverviewJson)
            .collect(JsonCollectors.toJsonArray());
  }

  @POST
  public Response setAnimal(JsonObject json) throws URISyntaxException {
    Animal animal = new Animal(json);
    animal = em.merge(animal);
    final URI uri = new URI(uriInfo.getPath() + animal.id);
    return Response.created(uri).build();
  }


  @GET
  @Path("/user")
  @RolesAllowed({"crb-role-user"})
  public String userEndpoint(@Context SecurityContext securityContext) {

    // Needed Dependency: 'org.wildfly.security', name: 'wildfly-elytron-http-oidc'
    OidcPrincipal<OidcSecurityContext> token = (OidcPrincipal<OidcSecurityContext>) securityContext.getUserPrincipal();
    String customerId = token.getOidcSecurityContext().getToken().getClaimValue("customerId", String.class);

    return "Secured Endpoint for role 'crb-role-user' access allowed for " + securityContext.getUserPrincipal() + " with customerId: " + customerId;
  }

  @GET
  @Path("/admin")
  @RolesAllowed({"crb-role-admin"})
  public String adminEndpoint(@Context SecurityContext securityContext) {
    return "Secured Endpoint for role 'crb-role-admin' access allowed for " + securityContext.getUserPrincipal();
  }
}
