package com.sleeved.looter.batch.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.JdbcTransactionManager;

@Configuration
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

  @Bean(name = "atlasTransactionManager")
  public JdbcTransactionManager atlasTransactionManager(
      @Qualifier("atlasDataSource") DataSource atlasDataSource) {
    return new JdbcTransactionManager(atlasDataSource);
  }

}
