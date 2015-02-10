package ua.pp.condor.searchengine;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.moxy.json.MoxyJsonConfig;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.pp.condor.searchengine.index.ISearchService;
import ua.pp.condor.searchengine.index.SearchService;

import javax.inject.Singleton;
import javax.ws.rs.ext.ContextResolver;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public final class Main {

    public static final Logger log = LoggerFactory.getLogger(Main.class);

    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8017/";

    private Main() {
    }

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in ua.pp.condor.searchengine package
        final ResourceConfig rc = new ResourceConfig()
                .packages("ua.pp.condor.searchengine")
                .register(createMoxyJsonResolver())
                .register(new AbstractBinder() {
                    @Override
                    protected void configure() {
                        bind(SearchService.class).to(ISearchService.class).in(Singleton.class);
                    }
                });

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static ContextResolver<MoxyJsonConfig> createMoxyJsonResolver() {
        final MoxyJsonConfig moxyJsonConfig = new MoxyJsonConfig();
        Map<String, String> namespacePrefixMapper = new HashMap<>(1);
        namespacePrefixMapper.put("http://www.w3.org/2001/XMLSchema-instance", "xsi");
        moxyJsonConfig.setNamespacePrefixMapper(namespacePrefixMapper).setNamespaceSeparator(':');
        return moxyJsonConfig.resolver();
    }

    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();
        log.info("Jersey app started with WADL available at {}application.wadl\nHit enter to stop it...", BASE_URI);
        System.in.read();
        server.shutdownNow();
    }
}
