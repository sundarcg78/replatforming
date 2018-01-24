package org.superbiz.moviefun;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
public class Application {

    public static void main(String... args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ServletRegistrationBean actionServletRegistration(ActionServlet actionServlet) {
        return new ServletRegistrationBean(actionServlet, "/moviefun/*");
    }

    @Bean
    JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setGenerateDdl(Boolean.TRUE);
        hibernateJpaVendorAdapter.setShowSql(Boolean.FALSE);
        hibernateJpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        hibernateJpaVendorAdapter.setDatabase(Database.MYSQL);
        return hibernateJpaVendorAdapter;
    }

    @Bean(name="movies-EMF")
    LocalContainerEntityManagerFactoryBean moviesLocalContainerEntityManagerFactoryBean(@Qualifier(value = "movies-ds")DataSource moviesDataSource, JpaVendorAdapter jpaVendorAdapter) {
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(getHikariDataSource(moviesDataSource));
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        localContainerEntityManagerFactoryBean.setPackagesToScan("org.superbiz.moviefun.movies");
        localContainerEntityManagerFactoryBean.setPersistenceUnitName("movies-unit");
        return localContainerEntityManagerFactoryBean;
    }

    @Bean(name="albums-EMF")
    LocalContainerEntityManagerFactoryBean albumsLocalContainerEntityManagerFactoryBean(@Qualifier(value = "albums-ds")DataSource albumsDataSource, JpaVendorAdapter jpaVendorAdapter) {
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(getHikariDataSource(albumsDataSource));
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        localContainerEntityManagerFactoryBean.setPackagesToScan("org.superbiz.moviefun.albums");
        localContainerEntityManagerFactoryBean.setPersistenceUnitName("albums-unit");
        return localContainerEntityManagerFactoryBean;
    }

    @Bean
    PlatformTransactionManager moviesPlatformTransactionManager(@Qualifier(value = "movies-EMF") LocalContainerEntityManagerFactoryBean moviesLocalContainerEntityManagerFactoryBean) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(moviesLocalContainerEntityManagerFactoryBean.getObject());
        //jpaTransactionManager.setDataSource(moviesLocalContainerEntityManagerFactoryBean.getDataSource());
        return jpaTransactionManager;
    }

    @Bean
    PlatformTransactionManager albumsPlatformTransactionManager(@Qualifier(value = "albums-EMF")LocalContainerEntityManagerFactoryBean albumsLocalContainerEntityManagerFactoryBean) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(albumsLocalContainerEntityManagerFactoryBean.getObject());
        //jpaTransactionManager.setDataSource(albumsLocalContainerEntityManagerFactoryBean.getDataSource());
        return jpaTransactionManager;
    }

    public DataSource getHikariDataSource(DataSource dataSource) {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDataSource(dataSource);
        return  hikariDataSource;
    }
}
