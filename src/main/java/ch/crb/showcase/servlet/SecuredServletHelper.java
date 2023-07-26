package ch.crb.showcase.servlet;

import ch.crb.showcase.service.DatabaseService;
import java.io.PrintWriter;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wildfly.security.http.oidc.AccessToken;
import org.wildfly.security.http.oidc.OidcPrincipal;
import org.wildfly.security.http.oidc.OidcSecurityContext;

@ApplicationScoped
public class SecuredServletHelper {

  private static Logger log = LoggerFactory.getLogger(SecuredServletHelper.class);

  @Inject
  private DatabaseService databaseService;

  public void writeTokenInformation(HttpServletRequest req, PrintWriter writer) {
    Principal user = req.getUserPrincipal();
    String username = user != null ? user.getName() : "NO AUTHENTICATED USER";
    writer.print(" Current Principal " + username + "");
    writer.println("    <p>");
    writer.println("Prinicpal Type = " + req.getUserPrincipal().getClass());
    writer.println("    </p>");

    OidcPrincipal<OidcSecurityContext> principal = (OidcPrincipal<OidcSecurityContext>) req.getUserPrincipal();
    AccessToken token = principal.getOidcSecurityContext().getToken();

    String customerId = token.getClaimValue("customerId", String.class);
    writer.println(" CustomerId: " + customerId);
    writer.println("    <p>");
    Map<String, String> company = token.getClaimValue("company", Map.class);
    writer.println(" Company: " + company.get("name"));

    ArrayList<String> roles = token.getRealmAccessClaim().getRoles();
    writer.println("    <p>");
    writer.println(" Roles: " + roles);

    writer.println("    <p>");
    writer.println(" Family Name: " + token.getFamilyName());
    writer.println("    <p>");
    writer.println(" Name: " + token.getName());
    writer.println("    <p>");
    writer.println(" Given Name: " + token.getGivenName());
    writer.println("    <p>");
    writer.println(" Email: " + token.getEmail());
    writer.println("    <p>");
    writer.println(" Username: " + token.getPreferredUsername());
    writer.println("    <p>");
    writer.println(" Token Typ: " + token.getClaimValue("typ", String.class));
    writer.println("    <p>");
    writer.println(" Token ID: " + token.getID());
    writer.println("    <p>");
    writer.println("    <p>");
  }

  public void writeDatabaseInformation(PrintWriter writer) {
    writer.println("    </p>");
    writer.println("    </p>");
    writer.print(" DB Connection ");
    if (databaseService.isDbConnected()) {
      writer.print("Connected");
    } else {
      writer.print("Not connected");
    }
    writer.print("");
    writer.println("    </p>");
    writer.println("  </body>");
    writer.println("</html>");
  }

  public void writeProfilePageLink(PrintWriter writer) {
    String oidcProviderUrl = System.getenv("OIDC_PROVIDER_URL");
    String realm = System.getenv("OIDC_REALM");
    String url = oidcProviderUrl+ "/realms/" + realm + "/account/";
    writer.println("    <a href =\"" + url + "\">Access Keycloak Account Page</a>");
  }
}
