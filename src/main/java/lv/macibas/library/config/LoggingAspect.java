package lv.macibas.library.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * AOP (Aspect-Oriented Programming) — "cross-cutting concerns": lietas, kas vajadzīgas
 * DAUDZĀS vietās (logošana, laika mērīšana, drošība, transakcijas), bet nav biznesa loģika.
 *
 * Termini:
 *  - Aspect:    klase ar papildus loģiku (šī klase)
 *  - Advice:    KAD izpildīt (@Before, @After, @AfterReturning, @AfterThrowing, @Around)
 *  - Pointcut:  KUR izpildīt (izteiksme, kuras metodes)
 *  - JoinPoint: konkrētā vieta (metodes izsaukums)
 *
 * Spring AOP strādā caur PROXY — tāpēc tas pats ierobežojums kā @Transactional:
 * iekšējie this.method() izsaukumi netiek pārtverti.
 */
@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    // Visas publiskās metodes service pakotnē
    @Around("execution(public * lv.macibas.library.service..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.nanoTime();
        try {
            return joinPoint.proceed(); // izsauc īsto metodi
        } finally {
            long ms = (System.nanoTime() - start) / 1_000_000;
            log.debug("⏱ {} izpildījās {} ms", joinPoint.getSignature().toShortString(), ms);
        }
    }
}
