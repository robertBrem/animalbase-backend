package ch.crb.showcase.health;

import ch.crb.showcase.service.DatabaseService;
import javax.annotation.security.PermitAll;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;

@PermitAll
@Readiness
@ApplicationScoped
public class ServiceReadyHealthCheck implements HealthCheck {

  @Inject
  private DatabaseService databaseService;

  @Override
  public HealthCheckResponse call() {
    databaseService.isDbConnected();

    HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named("Database connection health check");

    if (databaseService.isDbConnected()) {
      responseBuilder.up();
    } else {
      responseBuilder.down();
    }

    return responseBuilder.build();
  }
}
