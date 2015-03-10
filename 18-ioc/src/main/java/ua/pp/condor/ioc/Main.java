package ua.pp.condor.ioc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ua.pp.condor.ioc.config.ProdConfiguration;

public final class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("work");
        final ApplicationContext context = new AnnotationConfigApplicationContext(ProdConfiguration.class);
        for (String beanName : context.getBeanDefinitionNames()) {
            log.debug("Configured bean: {}", beanName);
        }
    }
}
