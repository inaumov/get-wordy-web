package get.wordy.config;

import get.wordy.core.DictionaryService;
import get.wordy.core.ServerInfo;
import get.wordy.core.api.IDictionaryService;
import get.wordy.core.dao.impl.DaoFactory;
import get.wordy.core.db.ConnectionWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.sql.*;
import java.util.Properties;

@Configuration
public class GetWordyConfiguration implements WebMvcConfigurer {
    private static final Logger log = LoggerFactory.getLogger(GetWordyConfiguration.class);

    @Autowired
    private Environment environment;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*");
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix("api/v1/", HandlerTypePredicate.forAnnotation(RestController.class));
    }

    @Bean
    public IDictionaryService discoveryService() {
        String host = environment.getProperty("DB_HOST");
        String port = environment.getProperty("DB_PORT");
        String dbName = environment.getProperty("DB_SCHEMA");
        String dbUser = environment.getProperty("DB_USER");
        String dbPassword = environment.getProperty("DB_PASSWORD");
        String url = String.format("""
                        jdbc:mysql://%s:%s/%s?useUnicode=yes,
                        characterEncoding=utf8,
                        connectionCollation=utf8_general_ci,
                        sql_mode=STRICT_TRANS_TABLES
                        """,
                host, port, dbName).replaceAll("\n", "");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, dbUser, dbPassword);
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT 1");
            rs.next();
            String resultChecked = rs.getString(1);
            if ("1".equals(resultChecked)) {
                log.info("Connection established. AutoCommit enabled: {}", conn.getAutoCommit());
            }
        } catch (ClassNotFoundException | SQLException e) {
            log.error(e.getCause().getMessage());
        }
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setHost(url);
        serverInfo.setDatabase(dbName);
        Properties account = new Properties();
        account.setProperty("user", dbUser);
        account.setProperty("password", dbPassword);
        serverInfo.setAccount(account);
        ConnectionWrapper connectionWrapper = ConnectionWrapper.getInstance();
        connectionWrapper.setServerInfo(serverInfo);
        DaoFactory factory = DaoFactory.getFactory();

        return new DictionaryService(
                factory.getDictionaryDao(),
                factory.getWordDao(),
                factory.getCardDao(),
                connectionWrapper
        );
    }

}