package get.wordy.config;

import get.wordy.core.DictionaryService;
import get.wordy.core.api.IDictionaryService;
import get.wordy.core.dao.impl.DaoFactory;
import get.wordy.core.db.LocalTxManager;
import get.wordy.core.db.ServerInfo;
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
    private static final Logger LOG = LoggerFactory.getLogger(GetWordyConfiguration.class);

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
        String dbUrl = environment.getProperty("DATABASE_URL");
        String dbUser = environment.getProperty("DATABASE_USERNAME");
        String dbPassword = environment.getProperty("DATABASE_PASSWORD");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT 1");
            rs.next();
            String resultChecked = rs.getString(1);
            if ("1".equals(resultChecked)) {
                LOG.info("Connection established. AutoCommit enabled: {}", conn.getAutoCommit());
            }
        } catch (ClassNotFoundException | SQLException e) {
            LOG.error(e.getCause().getMessage());
        }
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setUrl(dbUrl);
        Properties properties = new Properties();
        properties.setProperty("user", dbUser);
        properties.setProperty("password", dbPassword);
        serverInfo.setCredentials(properties);
        LocalTxManager txManager = LocalTxManager.getInstance();
        txManager.setServerInfo(serverInfo);
        DaoFactory factory = DaoFactory.getFactory();

        return new DictionaryService(
                factory.getDictionaryDao(),
                factory.getWordDao(),
                factory.getCardDao(),
                txManager
        );
    }

}