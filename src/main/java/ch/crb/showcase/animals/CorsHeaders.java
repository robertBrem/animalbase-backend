package ch.crb.showcase.animals;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.List;

@Provider
public class CorsHeaders implements ContainerResponseFilter {
    public static final String ALLOWED_METHODS = "GET, POST, PUT, DELETE, OPTIONS, HEAD";
    public static final int MAX_AGE = 151200;
    public static final String DEFAULT_ALLOWED_HEADERS = "origin,accept,content-type";

    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        MultivaluedMap<String, Object> headers = responseContext.getHeaders();
        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Headers", this.getRequestedHeaders(requestContext));
        headers.add("Access-Control-Expose-Headers", "*");
        headers.add("Access-Control-Allow-Credentials", "true");
        headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        headers.add("Access-Control-Max-Age", 151200);
        headers.add("x-responded-by", "cors-response-filter");
    }

    String getRequestedHeaders(ContainerRequestContext responseContext) {
        List<String> headers = (List) responseContext.getHeaders().get("Access-Control-Request-Headers");
        return this.createHeaderList(headers);
    }

    String createHeaderList(List<String> headers) {
        if (headers != null && !headers.isEmpty()) {
            StringBuilder retVal = new StringBuilder();

            for (int i = 0; i < headers.size(); ++i) {
                String header = (String) headers.get(i);
                retVal.append(header);
                retVal.append(',');
            }

            retVal.append("origin,accept,content-type");
            return retVal.toString();
        } else {
            return "origin,accept,content-type";
        }
    }
}

