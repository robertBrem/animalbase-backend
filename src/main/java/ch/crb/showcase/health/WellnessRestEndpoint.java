package ch.crb.showcase.health;


import javax.annotation.security.PermitAll;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@ApplicationScoped
@Path("/health/wellness")
public class WellnessRestEndpoint {

  @PermitAll
  @GET
  public String wellness() {
    return "all well - thanks for asking";
  }
}
