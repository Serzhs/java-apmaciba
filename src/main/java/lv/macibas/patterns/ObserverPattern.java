package lv.macibas.patterns;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * OBSERVER — kad viens objekts mainās, visi "abonenti" tiek automātiski informēti.
 * Tips: Behavioral. Saukts arī Publish/Subscribe.
 *
 * Dabā: Spring ApplicationEvent + @EventListener, UI event listeneri,
 * message brokeri (Kafka, RabbitMQ) — tā pati ideja, tikai starp servisiem.
 *
 * Ieguvums: izdevējs (publisher) NEZINA, kas klausās -> vāja sasaiste.
 */
public class ObserverPattern {

    record OrderPlaced(String orderId, double amount) {}

    static class EventBus {
        private final List<Consumer<OrderPlaced>> listeners = new ArrayList<>();

        void subscribe(Consumer<OrderPlaced> listener) { listeners.add(listener); }

        void publish(OrderPlaced event) { listeners.forEach(l -> l.accept(event)); }
    }

    static class OrderService {
        private final EventBus bus;

        OrderService(EventBus bus) { this.bus = bus; }

        void placeOrder(String id, double amount) {
            System.out.println("Pasūtījums " + id + " saglabāts");
            bus.publish(new OrderPlaced(id, amount)); // nezina, kas notiks tālāk
        }
    }

    public static void main(String[] args) {
        EventBus bus = new EventBus();
        bus.subscribe(e -> System.out.println("  📧 E-pasts: paldies par pasūtījumu " + e.orderId()));
        bus.subscribe(e -> System.out.println("  📦 Noliktava: rezervē preces " + e.orderId()));
        bus.subscribe(e -> System.out.println("  📊 Analītika: ieņēmumi +" + e.amount()));

        new OrderService(bus).placeOrder("A-42", 59.90);
        // Jauns abonents = jauna rinda, OrderService netiek mainīts.
    }
}
