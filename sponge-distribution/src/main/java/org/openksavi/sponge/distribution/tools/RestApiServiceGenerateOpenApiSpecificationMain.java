package org.openksavi.sponge.distribution.tools;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.camel.SpongeCamelConfiguration;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.logging.LoggingUtils;
import org.openksavi.sponge.restapi.RestApiConstants;
import org.openksavi.sponge.restapi.client.util.RestClientUtils;
import org.openksavi.sponge.restapi.server.RestApiServerPlugin;
import org.openksavi.sponge.spring.SpringSpongeEngine;

/**
 * Generates a REST API OpenAPI specification JSON.
 */
public class RestApiServiceGenerateOpenApiSpecificationMain {

    private static final Logger logger = LoggerFactory.getLogger(RestApiServiceGenerateOpenApiSpecificationMain.class);

    protected static final int PORT = RestApiConstants.DEFAULT_PORT;

    @Configuration
    public static class ToolConfig extends SpongeCamelConfiguration {

        @Bean
        public SpongeEngine spongeEngine() {
            return SpringSpongeEngine.builder().plugins(camelPlugin(), springPlugin(), spongeRestApiPlugin()).build();
        }

        @Bean
        public RestApiServerPlugin spongeRestApiPlugin() {
            RestApiServerPlugin plugin = new RestApiServerPlugin();

            plugin.getSettings().setPort(PORT);
            plugin.getSettings().setAllowAnonymous(true);
            plugin.getSettings().setPublishReload(true);
            plugin.getSettings().setPrettyPrint(true);

            plugin.getSettings().setName("Sponge REST API");
            plugin.getSettings().setDescription(plugin.getSettings().getName());

            return plugin;
        }
    }

    public void run(String filename) {
        LoggingUtils.initLoggingBridge();

        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ToolConfig.class);
        ctx.start();

        try {
            FileUtils.writeStringToFile(new File(filename), RestClientUtils.fetchOpenApiJson(String.format("http://localhost:%d", PORT)),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new SpongeException(e);
        } finally {
            ctx.close();
        }
    }

    public static void main(String... args) throws IOException {
        if (args.length < 1) {
            throw new SpongeException("Filename argument missing");
        }

        String file = args[0];

        logger.info("Writing to " + file);

        try {
            new RestApiServiceGenerateOpenApiSpecificationMain().run(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
