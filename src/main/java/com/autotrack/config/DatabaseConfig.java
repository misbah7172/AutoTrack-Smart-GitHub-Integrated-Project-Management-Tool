package com.autotrack.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * Database configuration to handle NeonDB PostgreSQL connection
 */
@Configuration
public class DatabaseConfig {

    @Bean
    @Primary
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://ep-sparkling-boat-a5ggt16d.us-east-2.aws.neon.tech/neondb");
        dataSource.setUsername("neondb_owner");
        dataSource.setPassword("npg_JchmopKk8Xr7");
        
        return dataSource;
    }
}