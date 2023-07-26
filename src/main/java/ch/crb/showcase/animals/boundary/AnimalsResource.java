package ch.crb.showcase.animals.boundary;


import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.stream.Collectors;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.stream.JsonCollectors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import ch.crb.showcase.animals.entity.Animal;
import ch.crb.showcase.animals.entity.AnimalImage;
import ch.crb.showcase.animals.entity.AnimalName;
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
  @PermitAll
  public Response setAnimal(JsonObject json) throws URISyntaxException {
    final Animal animal = em.merge(new Animal(json));
    animal.names = json.getJsonArray(Animal.JSON_FIELD_NAMES.NAMES)
            .stream()
            .map(name -> {
              AnimalName animalName = new AnimalName((JsonObject) name);
              animalName.animal = animal;
              return em.merge(animalName);
            })
            .collect(Collectors.toList());
    animal.imagePaths = json.getJsonArray(Animal.JSON_FIELD_NAMES.IMAGE_PATHS)
            .stream()
            .map(name -> {
              AnimalImage animalImage = new AnimalImage((JsonObject) name);
              animalImage.animal = animal;
              return em.merge(animalImage);
            })
            .collect(Collectors.toList());
    em.merge(animal);
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
