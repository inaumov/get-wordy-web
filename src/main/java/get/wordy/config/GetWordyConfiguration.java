package get.wordy.config;

import get.wordy.core.DictionaryService;
import get.wordy.core.api.IDictionaryService;
import get.wordy.core.dao.impl.DaoFactory;
import get.wordy.core.db.LocalTxManager;
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

import javax.sql.DataSource;

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
    public IDictionaryService discoveryService(DataSource dataSource) {
        LocalTxManager txManager = LocalTxManager.withDataSource(dataSource);
        DaoFactory factory = DaoFactory.withTxManager(txManager);
        LOG.info("Creating dictionary service for data source = {}", dataSource);
        return new DictionaryService(
                factory.getDictionaryDao(),
                factory.getWordDao(),
                factory.getCardDao(),
                txManager
        );
    }

}