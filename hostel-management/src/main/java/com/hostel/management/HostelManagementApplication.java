package com.hostel.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Main entry point for the Hostel Management System.
 * Extends SpringBootServletInitializer for WAR deployment on Tomcat 10.
 */
@SpringBootApplication
public class HostelManagementApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(HostelManagementApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(HostelManagementApplication.class, args);
    }
}
