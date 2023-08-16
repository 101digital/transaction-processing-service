package io.marketplace.services.transaction.processing.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class OpenApiConfig extends WebMvcConfigurerAdapter {

        @Override
        public void addResourceHandlers(final ResourceHandlerRegistry registry) {

            registry.addResourceHandler("/**")
                    .addResourceLocations(
                            ResourceUtils.CLASSPATH_URL_PREFIX + "/META-INF/resources/")
                    .resourceChain(false);

            registry.addResourceHandler("/swagger-ui/**")
                    .addResourceLocations(
                            ResourceUtils.CLASSPATH_URL_PREFIX
                                    + "/META-INF/resources/webjars/swagger-ui/3.25.0/")
                    .resourceChain(false);

            super.addResourceHandlers(registry);
        }
}
