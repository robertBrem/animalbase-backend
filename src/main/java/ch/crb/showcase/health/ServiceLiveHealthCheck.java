package ch.crb.showcase.health;


import javax.annotation.security.PermitAll;
import javax.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

@PermitAll
@Liveness
@ApplicationScoped
public class ServiceLiveHealthCheck implements HealthCheck {

  @Override
  public HealthCheckResponse call() {
    return HealthCheckResponse.named(ServiceLiveHealthCheck.class.getSimpleName()).withData("live", true).up().build();
  }
}
