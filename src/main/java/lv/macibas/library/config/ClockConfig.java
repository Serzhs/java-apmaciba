package lv.macibas.library.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * @Configuration + @Bean — veids, kā reģistrēt Spring konteinerā objektu, kura klasi
 * tu nevari anotēt ar @Component (jo tā ir no bibliotēkas, piem. java.time.Clock, ObjectMapper, RestClient).
 *
 * Kāpēc Clock kā bean? Lai testos varētu ielikt Clock.fixed(...) un pārbaudīt datumus deterministiski.
 * LocalDate.now() kodā tieši = netestējams kods.
 */
@Configuration
public class ClockConfig {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
