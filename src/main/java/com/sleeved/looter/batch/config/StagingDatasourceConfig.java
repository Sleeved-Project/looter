package com.sleeved.looter.batch.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;

@Configuration
@EnableJpaRepositories(basePackages = "com.sleeved.looter.domain.repository.staging", entityManagerFactoryRef = "stagingEntityManagerFactory", transactionManagerRef = "stagingTransactionManager")
public class StagingDatasourceConfig {
  @Bean(name = "stagingDataSource")
  @ConfigurationProperties(prefix = "looter.staging.datasource")
  public DataSource stagingDataSource() {
    return DataSourceBuilder.create().build();
  }

  @Bean(name = "stagingJdbcTemplate")
  public JdbcTemplate stagingJdbcTemplate(@Qualifier("stagingDataSource") DataSource stagingDataSource) {
    return new JdbcTemplate(stagingDataSource);
  }

  @Bean(name = "stagingEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean stagingEntityManagerFactory(
      EntityManagerFactoryBuilder builder, @Qualifier("stagingDataSource") DataSource dataSource) {

    return builder
        .dataSource(dataSource)
        .packages("com.sleeved.looter.domain.entity.staging")
        .persistenceUnit("staging")
        .build();
  }

  @Bean(name = "stagingTransactionManager")
  public PlatformTransactionManager stagingTransactionManager(
      @Qualifier("stagingEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
    return new JpaTransactionManager(entityManagerFactory);
  }

}
