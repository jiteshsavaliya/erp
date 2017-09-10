package com.erp.boot;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {
		"com.erp.repository" }, entityManagerFactoryRef = "erpDataStoreEM", transactionManagerRef = "erpDataStoreTM")
public class PersistenceContext {

	protected static final String PROPERTY_NAME_DATABASE_DRIVER = "erp.db.driver";
	protected static final String PROPERTY_NAME_DATABASE_PASSWORD = "erp.db.password";
	protected static final String PROPERTY_NAME_DATABASE_URL = "erp.db.url";
	protected static final String PROPERTY_NAME_DATABASE_USERNAME = "erp.db.username";
	protected static final String PROPERTY_NAME_DATABASE_MAX_CONNECTIONS = "erp.db.maxconnections";
	protected static final String PROPERTY_NAME_DATABASE_MIN_CONNECTIONS = "erp.db.minconnections";
	protected static final String PROPERTY_NAME_DATABASE_MAX_PARTITIONS = "erp.db.maxpartitions";
	protected static final String PROPERTY_NAME_DATABASE_MAX_LIFETIME = "erp.db.maxlifetimeinmillis";
	protected static final String PROPERTY_NAME_DATABASE_CONNECTION_TIMEOUT = "erp.db.connectiontimeoutinmillis";

	private static final String PROPERTY_NAME_HIBERNATE_DIALECT = "hibernate.dialect";
	private static final String PROPERTY_NAME_HIBERNATE_FORMAT_SQL = "hibernate.format_sql";
	private static final String PROPERTY_NAME_HIBERNATE_HBM2DDL_AUTO = "hibernate.hbm2ddl.auto";
	private static final String PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY = "hibernate.ejb.naming_strategy";
	private static final String PROPERTY_NAME_HIBERNATE_SHOW_SQL = "hibernate.show_sql";
	private static final String PROPERTY_NAME_HIBERNATE_LAZY_LOAD = "hibernate.enable_lazy_load_no_trans";

	private static final String PROPERTY_ENTITY_PACKAGES_TO_SCAN = "com.erp.domain";

	@Autowired
	private Environment environment;

	@Bean(name = "erpDataStore")
	@Primary
	public DataSource dataSource() {
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setDriverClassName(environment.getRequiredProperty(PROPERTY_NAME_DATABASE_DRIVER));
		dataSource.setJdbcUrl(environment.getRequiredProperty(PROPERTY_NAME_DATABASE_URL));
		dataSource.setUsername(environment.getRequiredProperty(PROPERTY_NAME_DATABASE_USERNAME));
		dataSource.setPassword(environment.getRequiredProperty(PROPERTY_NAME_DATABASE_PASSWORD));
		dataSource.setConnectionTestQuery("SELECT 1");
		dataSource
				.setMaximumPoolSize(Integer.parseInt(environment.getProperty(PROPERTY_NAME_DATABASE_MAX_CONNECTIONS)));
		dataSource.setMaxLifetime(Long.parseLong(environment.getProperty(PROPERTY_NAME_DATABASE_MAX_LIFETIME)));
		dataSource.setConnectionTimeout(
				Long.parseLong(environment.getProperty(PROPERTY_NAME_DATABASE_CONNECTION_TIMEOUT)));
		return dataSource;
	}

	@Bean(name = "erpDataStoreTM")
	@DependsOn("erpDataStore")
	@Primary
	public JpaTransactionManager transactionManager() {
		JpaTransactionManager transactionManager = new JpaTransactionManager();

		transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());

		return transactionManager;
	}

	@Bean(name = "erpDataStoreEM")
	@DependsOn("erpDataStore")
	@Primary
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();

		entityManagerFactoryBean.setDataSource(dataSource());
		entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		entityManagerFactoryBean.setPackagesToScan(PROPERTY_ENTITY_PACKAGES_TO_SCAN);

		Properties jpaProperties = new Properties();
		jpaProperties.put(PROPERTY_NAME_HIBERNATE_DIALECT,
				environment.getRequiredProperty(PROPERTY_NAME_HIBERNATE_DIALECT));
		jpaProperties.put(PROPERTY_NAME_HIBERNATE_FORMAT_SQL,
				environment.getRequiredProperty(PROPERTY_NAME_HIBERNATE_FORMAT_SQL));
		jpaProperties.put(PROPERTY_NAME_HIBERNATE_HBM2DDL_AUTO,
				environment.getRequiredProperty(PROPERTY_NAME_HIBERNATE_HBM2DDL_AUTO));
		jpaProperties.put(PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY,
				environment.getRequiredProperty(PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY));
		jpaProperties.put(PROPERTY_NAME_HIBERNATE_SHOW_SQL,
				environment.getRequiredProperty(PROPERTY_NAME_HIBERNATE_SHOW_SQL));
		jpaProperties.put(PROPERTY_NAME_HIBERNATE_LAZY_LOAD,
				environment.getRequiredProperty(PROPERTY_NAME_HIBERNATE_LAZY_LOAD));

		entityManagerFactoryBean.setJpaProperties(jpaProperties);
		return entityManagerFactoryBean;
	}
}