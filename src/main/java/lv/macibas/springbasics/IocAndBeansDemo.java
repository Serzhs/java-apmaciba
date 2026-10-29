package lv.macibas.springbasics;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import java.util.UUID;

/**
 * SPRING PAMATI BEZ SPRING BOOT — lai saprastu, kas notiek "zem pārsega".
 * Palaid main() un skaties konsolē.
 *
 * IoC (Inversion of Control): tu neraksti `new`, objektus veido un savieno Spring KONTEINERS
 * (ApplicationContext). Tu tikai apraksti, KAS vajadzīgs.
 *
 * Bean = objekts, ko pārvalda Spring konteiners.
 *
 * Bean SCOPES:
 *  - singleton (noklusējums) — viens eksemplārs uz konteineru
 *  - prototype — jauns eksemplārs katru reizi, kad pieprasa
 *  - request / session — viens uz HTTP pieprasījumu / sesiju (tikai web)
 *
 * Bean LIFECYCLE (vienkāršoti):
 *  1. Konstruktors (instantiate)
 *  2. Atkarību injicēšana (populate properties)
 *  3. @PostConstruct (init)
 *  4. ... bean tiek lietots ...
 *  5. @PreDestroy (kad konteiners aizveras; prototype beaniem NETIEK izsaukts!)
 *
 * ★ Klasisks jautājums: prototype bean injicēts singletonā -> tas kļūst praktiski singleton,
 *   jo injicēšana notiek tikai VIENREIZ. Risinājums: ObjectProvider<T> / @Lookup / scoped proxy.
 */
public class IocAndBeansDemo {

    static class ShoppingCart { // jābūt prototype — katram savs grozs
        final String id = UUID.randomUUID().toString().substring(0, 8);
    }

    static class AuditLogger {
        @PostConstruct
        void init() { System.out.println("  [lifecycle] AuditLogger @PostConstruct"); }

        @PreDestroy
        void shutdown() { System.out.println("  [lifecycle] AuditLogger @PreDestroy"); }

        void log(String msg) { System.out.println("  [audit] " + msg); }
    }

    static class CheckoutService {
        private final AuditLogger audit;
        private final ShoppingCart injectedOnce;                 // ❌ prototype injicēts vienreiz
        private final ObjectProvider<ShoppingCart> cartProvider; // ✅ dabū jaunu katru reizi

        CheckoutService(AuditLogger audit, ShoppingCart cart, ObjectProvider<ShoppingCart> cartProvider) {
            System.out.println("  [lifecycle] CheckoutService konstruktors (atkarības jau gatavas)");
            this.audit = audit;
            this.injectedOnce = cart;
            this.cartProvider = cartProvider;
        }

        String cartIdInjected() { return injectedOnce.id; }

        String cartIdFresh() { return cartProvider.getObject().id; }

        AuditLogger audit() { return audit; }
    }

    // Parasti šeit būtu @Configuration. Mēs to izlaižam TIKAI tāpēc, lai Spring Boot aplikācijas
    // @ComponentScan nepaņemtu šos demo beanus. AnnotationConfigApplicationContext pieņem arī šādu klasi.
    static class AppConfig {
        @Bean
        AuditLogger auditLogger() { return new AuditLogger(); }

        @Bean
        @Scope("prototype")
        ShoppingCart shoppingCart() { return new ShoppingCart(); }

        @Bean // parametri = atkarības, Spring tos atrisina pats
        CheckoutService checkoutService(AuditLogger audit, ShoppingCart cart, ObjectProvider<ShoppingCart> provider) {
            return new CheckoutService(audit, cart, provider);
        }
    }

    public static void main(String[] args) {
        System.out.println("Startējam konteineru...");
        try (var context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            CheckoutService s1 = context.getBean(CheckoutService.class);
            CheckoutService s2 = context.getBean(CheckoutService.class);
            System.out.println("Singleton — tas pats objekts? " + (s1 == s2));

            ShoppingCart c1 = context.getBean(ShoppingCart.class);
            ShoppingCart c2 = context.getBean(ShoppingCart.class);
            System.out.println("Prototype — tas pats objekts? " + (c1 == c2));

            System.out.println("Injicētais grozs: " + s1.cartIdInjected() + ", " + s1.cartIdInjected() + " (vienmēr tas pats!)");
            System.out.println("Provider grozs:   " + s1.cartIdFresh() + ", " + s1.cartIdFresh() + " (katru reizi jauns)");

            s1.audit().log("checkout pabeigts");
            System.out.println("Aizveram konteineru...");
        }
    }
}
