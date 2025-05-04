package com.sleeved.looter.batch.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.support.JdbcTransactionManager;

@Configuration
public class BatchDatasourceConfig {
  @Bean(name = "datasource")
  @Primary
  @ConfigurationProperties(prefix = "spring.datasource.hikari") // Pour utiliser les propriétés de application.yml
  public DataSource dataSource() {
    return DataSourceBuilder.create().build();
  }

  @Bean(name = "transactionManager")
  public JdbcTransactionManager transactionManager(@Qualifier("datasource") DataSource dataSource) {
    return new JdbcTransactionManager(dataSource);
  }
}
