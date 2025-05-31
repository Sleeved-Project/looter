package com.sleeved.looter.batch.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;

@Configuration
@EnableJpaRepositories(basePackages = "com.sleeved.looter.domain.repository.primary", entityManagerFactoryRef = "entityManagerFactory", transactionManagerRef = "transactionManager")
public class BatchDatasourceConfig {
  @Bean(name = "datasource")
  @Primary
  @ConfigurationProperties(prefix = "spring.datasource.hikari")
  public DataSource dataSource() {
    return DataSourceBuilder.create().build();
  }

  @Primary
  @Bean(name = "entityManagerFactory")
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(
      EntityManagerFactoryBuilder builder, @Qualifier("datasource") DataSource dataSource) {

    Map<String, Object> properties = new HashMap<>();
    properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");

    return builder
        .dataSource(dataSource)
        .packages("com.sleeved.looter.domain.entity.primary")
        .persistenceUnit("primary")
        .properties(properties)
        .build();
  }

  @Primary
  @Bean(name = "transactionManager")
  public PlatformTransactionManager transactionManager(
      @Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {
    return new JpaTransactionManager(entityManagerFactory);
  }
}
