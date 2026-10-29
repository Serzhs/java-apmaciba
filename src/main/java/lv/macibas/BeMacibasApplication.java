package lv.macibas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Spring Boot ieejas punkts.
 *
 * @SpringBootApplication = 3 anotācijas vienā:
 *   @Configuration            — šī klase var definēt @Bean
 *   @EnableAutoConfiguration  — Boot automātiski konfigurē lietas pēc classpath (redz H2 -> izveido DataSource)
 *   @ComponentScan            — meklē @Component/@Service/@Repository/@RestController šajā pakotnē un apakšpakotnēs
 *
 * Palaid: IntelliJ -> zaļā bultiņa, vai terminālī `mvn spring-boot:run`
 * Tad: http://localhost:8080/api/books
 */
@SpringBootApplication
@ConfigurationPropertiesScan // atrod @ConfigurationProperties klases (LibraryProperties)
public class BeMacibasApplication {

    public static void main(String[] args) {
        SpringApplication.run(BeMacibasApplication.class, args);
    }
}
