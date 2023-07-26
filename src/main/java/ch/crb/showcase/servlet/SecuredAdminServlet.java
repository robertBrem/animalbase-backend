package ch.crb.showcase.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple but secure http servlet with annotation based RBAC.
 */
@WebServlet("/secured-admin")
@ServletSecurity(@HttpConstraint(rolesAllowed = "crb-role-admin"))
public class SecuredAdminServlet extends HttpServlet {

  private static Logger log = LoggerFactory.getLogger(SecuredAdminServlet.class);

  @Inject
  private SecuredServletHelper securedServletHelper;

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    try (PrintWriter writer = resp.getWriter()) {
      writer.println("<html>");
      writer.println("  <head><title>Secured Servlet for ADMINS</title></head>");
      writer.println("  <body>");
      writer.println("    <h1>Secured Servlet for ADMINS</h1>");
      writer.println("    <p>");

      securedServletHelper.writeTokenInformation(req, writer);
      writer.println("    </p>");
      securedServletHelper.writeDatabaseInformation(writer);
      writer.println("    </p>");
      securedServletHelper.writeProfilePageLink(writer);
    }
  }
}
