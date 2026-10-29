package lv.macibas.oop;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * 5. KOMPOZĪCIJA PRET MANTOŠANU (Composition over Inheritance)
 *
 * Mantošana = "IS-A" (Suns IR dzīvnieks)
 * Kompozīcija = "HAS-A" (Mašīnai IR dzinējs)
 *
 * Kāpēc bieži dod priekšroku kompozīcijai?
 *  - Vājāka sasaiste (loose coupling) — var nomainīt daļu izpildes laikā
 *  - Nav "fragile base class" problēmas
 *  - Vieglāk testēt (var ielikt mock)
 *  - Izvairās no klašu eksplozijas (FlyingSwimmingRobotDuck...)
 *
 * Klasisks piemērs no "Effective Java" (Joshua Bloch, Item 18): HashSet paplašināšana.
 */
public class CompositionOverInheritance {

    // ❌ Mantošana: gribam skaitīt, cik elementu pievienots
    static class CountingHashSet<E> extends HashSet<E> {
        int addCount = 0;

        @Override
        public boolean add(E e) {
            addCount++;
            return super.add(e);
        }

        @Override
        public boolean addAll(java.util.Collection<? extends E> c) {
            addCount += c.size();
            return super.addAll(c); // ⚠️ HashSet.addAll iekšēji izsauc add() -> skaitām divreiz!
        }
    }

    // ✅ Kompozīcija: iesaiņojam (wrap) setu, nevis mantojam no tā
    static class CountingSet<E> {
        private final java.util.Set<E> delegate = new HashSet<>();
        int addCount = 0;

        boolean add(E e) {
            addCount++;
            return delegate.add(e);
        }

        boolean addAll(java.util.Collection<? extends E> c) {
            addCount += c.size();
            return delegate.addAll(c); // delegate.add() neizsauc MŪSU add() -> pareizi
        }

        int size() {
            return delegate.size();
        }
    }

    // --- Vēl viens piemērs: uzvedība kā komponents ---
    interface Engine {
        String start();
    }

    static class PetrolEngine implements Engine {
        public String start() { return "Brrrm (benzīns)"; }
    }

    static class ElectricEngine implements Engine {
        public String start() { return "Bzzz (elektrība)"; }
    }

    static class Car {
        private Engine engine; // HAS-A

        Car(Engine engine) { this.engine = engine; }

        void swapEngine(Engine engine) { this.engine = engine; } // maināms runtime!

        void drive() { System.out.println("Auto brauc: " + engine.start()); }
    }

    public static void main(String[] args) {
        CountingHashSet<String> bad = new CountingHashSet<>();
        bad.addAll(List.of("a", "b", "c"));
        System.out.println("Mantošana — addCount: " + bad.addCount + " (gaidījām 3!)");

        CountingSet<String> good = new CountingSet<>();
        good.addAll(new ArrayList<>(List.of("a", "b", "c")));
        System.out.println("Kompozīcija — addCount: " + good.addCount + " ✔");

        Car car = new Car(new PetrolEngine());
        car.drive();
        car.swapEngine(new ElectricEngine());
        car.drive();
    }
}
