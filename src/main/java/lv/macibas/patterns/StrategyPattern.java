package lv.macibas.patterns;

import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * STRATEGY — algoritmu ģimene, katrs savā klasē, un tos var mainīt izpildes laikā.
 * Tips: Behavioral (uzvedības) šablons.
 *
 * Aizstāj garus if/else vai switch blokus. Tieši tas ir Open/Closed princips darbībā.
 * Java 8+ stratēģiju bieži var uzrakstīt kā lambda (jo tas ir funkcionāls interfeiss).
 *
 * Spring dabā: injicē List<Strategy> vai Map<String, Strategy> — Spring savāc VISUS beanus
 * ar šo interfeisu automātiski. Skat. library/notification pakotni Spring daļā.
 */
public class StrategyPattern {

    interface ShippingStrategy {
        double cost(double weightKg);
    }

    static class DpdShipping implements ShippingStrategy {
        public double cost(double weightKg) { return 3.99 + weightKg * 0.5; }
    }

    static class OmnivaShipping implements ShippingStrategy {
        public double cost(double weightKg) { return 2.49 + weightKg * 0.8; }
    }

    static class Checkout {
        private ShippingStrategy shipping;

        Checkout(ShippingStrategy shipping) { this.shipping = shipping; }

        void setShipping(ShippingStrategy shipping) { this.shipping = shipping; }

        double total(double itemsPrice, double weightKg) {
            return itemsPrice + shipping.cost(weightKg);
        }
    }

    public static void main(String[] args) {
        Checkout checkout = new Checkout(new DpdShipping());
        System.out.printf("DPD:    %.2f%n", checkout.total(20, 2));

        checkout.setShipping(new OmnivaShipping());
        System.out.printf("Omniva: %.2f%n", checkout.total(20, 2));

        checkout.setShipping(weight -> 0.0); // lambda kā stratēģija — bezmaksas piegāde
        System.out.printf("Akcija: %.2f%n", checkout.total(20, 2));

        // Stratēģiju reģistrs Map formā — vietā garam switch
        Map<String, UnaryOperator<String>> formatters = Map.of(
                "upper", String::toUpperCase,
                "lower", String::toLowerCase,
                "reverse", s -> new StringBuilder(s).reverse().toString()
        );
        System.out.println(formatters.get("reverse").apply("Spring"));
    }
}
