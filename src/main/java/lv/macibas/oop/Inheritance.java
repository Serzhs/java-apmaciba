package lv.macibas.oop;

/**
 * 2. MANTOŠANA (Inheritance)
 *
 * Ideja: apakšklase pārņem virsklases laukus un metodes ("IS-A" attiecība).
 * Suns IR dzīvnieks. Java atbalsta tikai VIENKĀRŠO klašu mantošanu (extends viena klase),
 * bet klase var implementēt VAIRĀKUS interfeisus.
 *
 * Intervijas jautājumi:
 *  - Kāpēc Java nav multiple inheritance klasēm? -> "Diamond problem" (neskaidrs, kuru metodi mantot).
 *  - Kas ir super? -> atsauce uz virsklases konstruktoru/metodēm.
 *  - Konstruktoru izsaukšanas secība? -> vispirms virsklase, tad apakšklase.
 *  - Overloading vs Overriding? -> skat. Polymorphism.java
 *  - Ko nozīmē final klase/metode? -> nevar mantot / nevar pārrakstīt (piem. String ir final).
 */
public class Inheritance {

    static class Animal {
        protected final String name; // protected — redz apakšklases un tā pati pakotne

        Animal(String name) {
            this.name = name;
            System.out.println("  Animal konstruktors (" + name + ")");
        }

        String makeSound() {
            return "...";
        }

        // final — apakšklases NEVAR pārrakstīt šo metodi
        final String describe() {
            return name + " saka: " + makeSound();
        }
    }

    static class Dog extends Animal {
        Dog(String name) {
            super(name); // obligāti jāizsauc virsklases konstruktors (pirmā rinda!)
            System.out.println("  Dog konstruktors");
        }

        @Override // anotācija liek kompilatoram pārbaudīt, ka tiešām pārrakstām
        String makeSound() {
            return "Vau!";
        }

        void fetch() {
            System.out.println(name + " atnes kociņu");
        }
    }

    static class Puppy extends Dog {
        Puppy(String name) {
            super(name);
            System.out.println("  Puppy konstruktors");
        }

        @Override
        String makeSound() {
            return super.makeSound() + " (bet klusāk)"; // izmanto virsklases implementāciju
        }
    }

    public static void main(String[] args) {
        System.out.println("Veidojam Puppy — skaties konstruktoru secību:");
        Puppy puppy = new Puppy("Reksis");

        System.out.println(puppy.describe());
        puppy.fetch(); // mantota no Dog

        // Visas klases Java netieši manto no java.lang.Object
        Object o = puppy;
        System.out.println("Ir Animal? " + (o instanceof Animal));

        // ⚠️ Mantošanas slazds: dziļa hierarhija = cieša sasaiste (tight coupling).
        // Izmaiņa Animal var salauzt Dog un Puppy ("fragile base class problem").
        // Tāpēc bieži labāk izmantot KOMPOZĪCIJU — skat. CompositionOverInheritance.java
    }
}
