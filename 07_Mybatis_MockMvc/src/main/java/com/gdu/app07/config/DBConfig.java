package com.gdu.app07.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

// 설정 파일을 만들기 위한 or Bean을 등록하기 위한 애너테이션이다.
@Configuration
// value 속성으로 application.properties의 경로를 넣어주면 Environment 객체에 프로퍼티 값이 자동으로 주입되는 애너테이션이다.
@PropertySource(value={"classpath:application.properties"})
// @Transactional 애너테이션을 찾거나, 클래스 내부에 TransactionManager가 사용되었을 경우 트랜잭션을 허용시켜주는 애너테이션이다.
@EnableTransactionManagement
public class DBConfig {
	
	@Autowired
	// Environment : 스프링 환경이자 설정과 관련된 인터페이스이다.
	private Environment env;

	// HikaryConfig Bean
	/*
		HikariConfig : DataSource를 초기화 하는 데에 사용되는 클래스이다.
					   필수 구성 요소 4개를 application.properties에서 로드한다
					   DriverClass : oracle.jdbc.OracleDriver
					   URL  	   : jdbc:oracle:thin:@localhost:1521:xe
					   Username    : GDJ61
					   Passward    : 1111
	*/
	@Bean
	public HikariConfig hikariConfig() {
		HikariConfig hikariConfig = new HikariConfig();
		// Oracle DB 드라이버 로드(ojdbc8.jar).
		hikariConfig.setDriverClassName(env.getProperty("spring.datasource.hikari.driver-class-name"));
		// Oravle DB와 연결(접속).
		hikariConfig.setJdbcUrl(env.getProperty("spring.datasource.hikari.jdbc-url"));
		hikariConfig.setUsername(env.getProperty("spring.datasource.hikari.username"));
		hikariConfig.setPassword(env.getProperty("spring.datasource.hikari.password"));
		return hikariConfig;
	}
	
	// HikariDataSource Bean            
	// destroyMethod = "close" : Bean 객체의 스코프가 끝날 경우 Connection을 close 한다.
	@Bean(destroyMethod="close")
	public HikariDataSource hikariDataSource() {
		return new HikariDataSource(hikariConfig());
	}
	
	// SqlSessionFactory Bean
	@Bean
	// Spring과 mybatis의 연동이 실패할 경우, DataAccessException 예외가 발생하기 때문에 반드시 예외처리를 해준다.
	public SqlSessionFactory sqlSessionFactory() throws Exception {
		/*
			myBatis만 사용하면, SqlSessionFactory는 SqlSessionFactoryBuilder를 사용해서 생성한다. 
			Spring과 myBatis 연동시에는 Spring Container에 Bean 객체로 저장하기 위해 SqlSessionFactoryBean이 대신 사용된다.
		*/
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
		bean.setDataSource(hikariDataSource());
		bean.setConfigLocation(new PathMatchingResourcePatternResolver().getResource(env.getProperty("mybatis.config-location")));
		bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(env.getProperty("mybatis.mapper-locations")));
		return bean.getObject();
	}
	
	// SqlSessionTemplate Bean (기존의 SqlSession)
	@Bean
	public SqlSessionTemplate sqlSessionTemplate() throws Exception {
		return new SqlSessionTemplate(sqlSessionFactory());
	}
	
	// TransactionManager Bean
	// Transaction : 성공(Commit)과 실패(Rollback)가 분명하여 일관되고 믿을 수 있는 시스템을 의미한다. (데이터의 정합성을 보장하기 위해 고안된 방법이다.)
	// Service에서 예외 발생 시 예외 발생 전에 실행된 모든 DAO 메소드 명령을 모두 Rollback 처리 해준다. 
	@Bean
	public TransactionManager transactionManager() {
		/*		
			DataSourceTransactionManager : 스프링에서 JDBC 및 mybatis 등의 JDBC 기반 라이브러리로 데이터베이스로 접근하는 경우 사용하는 인터페이스이다.
										   myBatis에 종속되는 새로운 트랜잭션 관리를 만드는 것보다는, 
										   Spring - myBatis 연동모듈(hikariDataSource)이 Spring의 DataSourceTransactionManager과 융합되는 것이 좋다.
		*/
		return new DataSourceTransactionManager(hikariDataSource());
	}
	
}
