package lv.macibas.oop;

import java.util.ArrayList;
import java.util.List;

/**
 * 7. NEMAINĪGI OBJEKTI (Immutability)
 *
 * Immutable objekts = pēc izveides tā stāvokli NEVAR mainīt. Piemēri: String, Integer, LocalDate, BigDecimal.
 *
 * Kā uztaisīt immutable klasi (bieži jautā!):
 *  1. Klase final (lai nevar mantot un "salauzt")
 *  2. Visi lauki private final
 *  3. Nav setteru
 *  4. Mainīgos laukus (List, Date) — DEFENSIVE COPY konstruktorā un getterī
 *  5. "Izmaiņas" atgriež JAUNU objektu (withX metodes)
 *
 * Kāpēc tas labi?
 *  - Thread-safe bez sinhronizācijas (nav ko sacensties mainīt)
 *  - Droši lietot kā HashMap atslēgu
 *  - Vieglāk saprast kodu — nav "kas to nomainīja?" problēmu
 *
 * Kāpēc String ir immutable? -> String pool (droši dalīt), drošība (paroles, URL),
 * hashCode var kešot, thread-safety.
 */
public class Immutability {

    // ❌ Izskatās immutable, bet nav
    static final class LeakyOrder {
        private final List<String> items;

        LeakyOrder(List<String> items) {
            this.items = items; // saglabā atsauci uz ārējo sarakstu!
        }

        List<String> getItems() {
            return items; // atdod iekšējo sarakstu!
        }
    }

    // ✅ Īsti immutable
    static final class Order {
        private final String id;
        private final List<String> items;

        Order(String id, List<String> items) {
            this.id = id;
            this.items = List.copyOf(items); // defensive copy + nemaināms saraksts
        }

        String getId() { return id; }

        List<String> getItems() { return items; } // List.copyOf jau ir unmodifiable

        Order withItem(String item) {
            List<String> newItems = new ArrayList<>(items);
            newItems.add(item);
            return new Order(id, newItems); // atgriežam JAUNU objektu
        }

        @Override
        public String toString() { return "Order[" + id + ", " + items + "]"; }
    }

    // ✅ Java 16+: record — īss veids immutable datu klasei (bet List lauku tik un tā jākopē!)
    record OrderRecord(String id, List<String> items) {
        OrderRecord { // "compact constructor"
            items = List.copyOf(items);
        }
    }

    public static void main(String[] args) {
        List<String> source = new ArrayList<>(List.of("maize"));

        LeakyOrder leaky = new LeakyOrder(source);
        source.add("ŠĪS NEBIJA PASŪTĪTS");
        System.out.println("Leaky: " + leaky.getItems()); // mainījās!

        Order order = new Order("o-1", source);
        source.add("vēl kaut kas");
        System.out.println("Order: " + order); // nemainījās ✔

        Order order2 = order.withItem("piens");
        System.out.println("Oriģināls: " + order);
        System.out.println("Jauns:     " + order2);

        String s = "abc";
        s.toUpperCase(); // nemaina s! atgriež jaunu stringu
        System.out.println("String pēc toUpperCase(): " + s);
    }
}
