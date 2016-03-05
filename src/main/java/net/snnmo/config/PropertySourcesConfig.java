package net.snnmo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;


/**
 * Created by cc on 16/3/2.
 */
@Configuration
public class PropertySourcesConfig {
    private static final Resource[] DEV_PROPERTIES = new ClassPathResource[]{
            new ClassPathResource("example-dev.properties"),
            new ClassPathResource("META-INF/properties/global.properties"),
            new ClassPathResource("META-INF/properties/global-dev.properties"),
    };
    private static final Resource[] LOCAL_PROPERTIES = new ClassPathResource[]{
            new ClassPathResource("example-local.properties"),
            new ClassPathResource("META-INF/properties/global.properties"),
            new ClassPathResource("META-INF/properties/global-local.properties"),
    };
    private static final Resource[] TEST_PROPERTIES = new ClassPathResource[]{
            new ClassPathResource("example-test.properties"),
            new ClassPathResource("META-INF/properties/global.properties"),
            new ClassPathResource("META-INF/properties/global-test.properties"),
    };
    private static final Resource[] STAGING_PROPERTIES = new ClassPathResource[]{
            new ClassPathResource("example-staging.properties"),
            new ClassPathResource("META-INF/properties/global.properties"),
            new ClassPathResource("META-INF/properties/global-staging.properties"),
    };
    private static final Resource[] PROD_PROPERTIES = new ClassPathResource[]{
            new ClassPathResource("example-prod.properties"),
            new ClassPathResource("META-INF/properties/global.properties"),
            new ClassPathResource("META-INF/properties/global-prod.properties"),
    };

    @Profile("local")
    public static class LocalConfig {
        @Bean
        public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
            PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
            pspc.setLocations(LOCAL_PROPERTIES);
            return pspc;
        }
    }

    @Profile("dev")
    public static class DevConfig {
        @Bean
        public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
            PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
            pspc.setLocations(DEV_PROPERTIES);
            return pspc;
        }
    }

    @Profile("test")
    public static class TestConfig {
        @Bean
        public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
            PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
            pspc.setLocations(TEST_PROPERTIES);
            return pspc;
        }
    }

    @Profile("prod")
    public static class ProdConfig {
        @Bean
        public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
            PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
            pspc.setLocations(PROD_PROPERTIES);
            return pspc;
        }
    }

    @Profile("staging")
    public static class StagingConfig {
        @Bean
        public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
            PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
            pspc.setLocations(STAGING_PROPERTIES);
            return pspc;
        }
    }
}
