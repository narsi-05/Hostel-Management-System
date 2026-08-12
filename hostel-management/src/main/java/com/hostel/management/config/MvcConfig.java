package com.hostel.management.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * MVC Configuration to serve uploaded hostel photo files.
 *
 * IMPORTANT: hostel.upload.dir is resolved to an ABSOLUTE path here and this
 * exact same resolution must be used by HostelService when saving files,
 * otherwise photos saved by an Owner can end up in a folder that this
 * resource handler is not pointing at — which looks like "photos uploaded
 * fine but visitors never see them".
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(MvcConfig.class);

    @Value("${hostel.upload.dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path absolute = resolveUploadPath(uploadDir);
        String resourceLocation = absolute.toUri().toString();

        log.info("Serving uploaded hostel photos from: {}", absolute);

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(resourceLocation);
    }

    /**
     * Resolves the configured upload directory to an absolute path and
     * ensures it exists. Shared logic so save-time and serve-time paths
     * can never diverge.
     */
    public static Path resolveUploadPath(String configuredDir) {
        Path path = Paths.get(configuredDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(path);
        } catch (Exception e) {
            log.error("Could not create upload directory at {}: {}", path, e.getMessage());
        }
        return path;
    }
}
