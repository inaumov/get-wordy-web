package get.wordy.config;

import get.wordy.core.ClassAccessService;
import get.wordy.core.ClassService;
import get.wordy.core.GetWordyService;
import get.wordy.core.ThemeService;
import get.wordy.core.WordsExplanationService;
import get.wordy.core.api.*;
import get.wordy.core.dao.impl.*;
import get.wordy.core.db.LocalTxManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;

@Configuration
public class GetWordyConfiguration implements WebMvcConfigurer {
    private static final Logger LOG = LoggerFactory.getLogger(GetWordyConfiguration.class);

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
    public GetWordyService coreService(DataSource dataSource, NamedParameterJdbcTemplate jdbcTemplate) {
        LocalTxManager txManager = LocalTxManager.withDataSource(dataSource);
        LOG.info("Creating vocabulary and user cards service for data source = {}", dataSource);
        return new GetWordyService(
                new VocabularyDao(jdbcTemplate),
                new WordDao(jdbcTemplate.getJdbcTemplate()),
                new ProgressDao(jdbcTemplate.getJdbcTemplate()),
                new CardHeadlineDao(jdbcTemplate),
                txManager
        );
    }

    @Bean
    public IVocabularyService vocabularyService(GetWordyService coreService) {
        return coreService;
    }

    @Bean
    public IUserCardsService userCardsService(GetWordyService coreService) {
        return coreService;
    }

    @Bean
    public IClassService classService(DataSource dataSource, NamedParameterJdbcTemplate jdbcTemplate) {
        LocalTxManager txManager = LocalTxManager.withDataSource(dataSource);
        LOG.info("Creating classes service for data source = {}", dataSource);
        return new ClassService(
                new ClassesDao(jdbcTemplate),
                txManager
        );
    }

    @Bean
    public IClassAccessService classAccessService(DataSource dataSource, NamedParameterJdbcTemplate jdbcTemplate,
                                                  IClassService classService) {
        LocalTxManager txManager = LocalTxManager.withDataSource(dataSource);
        LOG.info("Creating class access service for data source = {}", dataSource);
        return new ClassAccessService(
                new ClassAccessDao(jdbcTemplate),
                txManager,
                classService
        );
    }

    @Bean
    public IWordExplanationService wordExplanationService(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        LocalTxManager txManager = LocalTxManager.withDataSource(dataSource);
        LOG.info("Creating words service for data source = {}", dataSource);
        return new WordsExplanationService(
                new WordDao(jdbcTemplate),
                txManager
        );
    }

    @Bean
    public ThemeService themeService(DataSource dataSource, NamedParameterJdbcTemplate jdbcTemplate) {
        LocalTxManager txManager = LocalTxManager.withDataSource(dataSource);
        LOG.info("Creating theme service for data source = {}", dataSource);
        return new ThemeService(
                new ThemeDao(jdbcTemplate),
                new WordDao(jdbcTemplate.getJdbcTemplate()),
                txManager
        );
    }

}
