package ua.pp.condor.searchengine;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import ua.pp.condor.searchengine.dto.Document;
import ua.pp.condor.searchengine.index.ISearchService;
import ua.pp.condor.searchengine.index.LogicType;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.Set;

@Singleton
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Controller {

    private static final Logger log = LoggerFactory.getLogger(Controller.class);

    @Inject
    private ISearchService searchService;

    @GET
    @Path("search")
    public Set<Integer> search(
            @QueryParam("query") String query,
            @DefaultValue("or") @QueryParam("logic") LogicType logic,
            @DefaultValue("10") @QueryParam("count") int count) {

        if (StringUtils.isBlank(query) || count < 1 || searchService.isEmpty()) {
            return Collections.emptySet();
        }
        MDC.put("query", query);
        MDC.put("logic", logic.name());
        MDC.put("count", String.valueOf(count));
        try {
            return searchService.search(query, logic, count);
        } catch (Exception e) {
            log.error("Unexpected error", e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    @POST
    @Path("index")
    public Response index(Document doc) {
        MDC.put("docId", String.valueOf(doc.getId()));
        log.debug(doc.toString());
        try {
            searchService.addDocument(doc);
            return Response.ok().build();
        } catch (Exception e) {
            log.error("Unexpected error", e);
            throw e;
        } finally {
            MDC.clear();
        }
    }
}
