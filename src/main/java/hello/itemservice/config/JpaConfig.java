package hello.itemservice.config;


import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.jpa.JpaItemRepository;
import hello.itemservice.service.ItemService;
import hello.itemservice.service.ItemServiceV1;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

@Slf4j
@Configuration
public class JpaConfig {

    private final EntityManager entityManager;

    public JpaConfig(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    @Bean
    public ItemService itemService() {
        return new ItemServiceV1(itemRepository());
    }

    @Bean
    public ItemRepository itemRepository() {
        log.info("entityManager = {}",entityManager);
        return new JpaItemRepository(entityManager);
    }


}
