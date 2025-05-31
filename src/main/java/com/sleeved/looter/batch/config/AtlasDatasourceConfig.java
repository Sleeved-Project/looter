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
@EnableJpaRepositories(basePackages = "com.sleeved.looter.domain.repository.atlas", entityManagerFactoryRef = "atlasEntityManagerFactory", transactionManagerRef = "atlasTransactionManager")
public class AtlasDatasourceConfig {
  @Bean(name = "atlasDataSource")
  @ConfigurationProperties(prefix = "atlas.datasource")
  public DataSource atlasDataSource() {
    return DataSourceBuilder.create().build();
  }

  @Bean(name = "atlasJdbcTemplate")
  public JdbcTemplate atlasJdbcTemplate(@Qualifier("atlasDataSource") DataSource atlasDataSource) {
    return new JdbcTemplate(atlasDataSource);
  }

  @Bean(name = "atlasEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean atlasEntityManagerFactory(
      EntityManagerFactoryBuilder builder, @Qualifier("atlasDataSource") DataSource dataSource) {

    return builder
        .dataSource(dataSource)
        .packages("com.sleeved.looter.domain.entity.atlas")
        .persistenceUnit("atlas")
        .build();
  }

  @Bean(name = "atlasTransactionManager")
  public PlatformTransactionManager atlasTransactionManager(
      @Qualifier("atlasEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
    return new JpaTransactionManager(entityManagerFactory);
  }

}
