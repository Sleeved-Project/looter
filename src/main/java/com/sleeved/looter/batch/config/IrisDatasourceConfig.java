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
@EnableJpaRepositories(basePackages = "com.sleeved.looter.domain.repository.iris", entityManagerFactoryRef = "irisEntityManagerFactory", transactionManagerRef = "irisTransactionManager")
public class IrisDatasourceConfig {
  @Bean(name = "irisDataSource")
  @ConfigurationProperties(prefix = "iris.datasource")
  public DataSource irisDataSource() {
    return DataSourceBuilder.create().build();
  }

  @Bean(name = "irisJdbcTemplate")
  public JdbcTemplate irisJdbcTemplate(@Qualifier("irisDataSource") DataSource irisDataSource) {
    return new JdbcTemplate(irisDataSource);
  }

  @Bean(name = "irisEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean irisEntityManagerFactory(
      EntityManagerFactoryBuilder builder, @Qualifier("irisDataSource") DataSource dataSource) {

    return builder
        .dataSource(dataSource)
        .packages("com.sleeved.looter.domain.entity.iris")
        .persistenceUnit("iris")
        .build();
  }

  @Bean(name = "irisTransactionManager")
  public PlatformTransactionManager irisTransactionManager(
      @Qualifier("irisEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
    return new JpaTransactionManager(entityManagerFactory);
  }

}
