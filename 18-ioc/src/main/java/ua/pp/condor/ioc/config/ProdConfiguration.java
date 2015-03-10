package ua.pp.condor.ioc.config;

import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import javax.inject.Inject;
import javax.sql.DataSource;

@Configuration
@PropertySource("classpath:db.properties")
@Import(ApplicationConfiguration.class)
public class ProdConfiguration {

    @Inject
    private Environment env;

    @Bean
    DataSource dataSource() {
        final PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setUrl(env.getProperty("jdbc.url"));
        ds.setUser(env.getProperty("jdbc.user"));
        ds.setPassword(env.getProperty("jdbc.password"));
        return ds;
    }
}
