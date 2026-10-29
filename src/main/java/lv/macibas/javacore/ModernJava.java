package lv.macibas.javacore;

import java.util.List;

/**
 * MODERNĀ JAVA (11 -> 21) — parāda, ka esi "up to date".
 *
 *  Java 10  var — lokālo mainīgo tipu izsecināšana
 *  Java 14  switch expressions (->)
 *  Java 15  text blocks """ """
 *  Java 16  records, pattern matching instanceof
 *  Java 17  sealed classes (LTS)
 *  Java 21  pattern matching switch, record patterns, virtual threads, Sequenced collections (LTS)
 *
 * LTS versijas (Long Term Support): 8, 11, 17, 21, 25. Uzņēmumos visbiežāk 17 vai 21.
 */
public class ModernJava {

    // sealed — TIKAI uzskaitītās klases drīkst implementēt. Kompilators zina visus variantus.
    sealed interface PaymentMethod permits Card, BankTransfer, Cash {}

    record Card(String number, String holder) implements PaymentMethod {}

    record BankTransfer(String iban) implements PaymentMethod {}

    record Cash() implements PaymentMethod {}

    // record = immutable datu nesējs. Automātiski: konstruktors, getteri (bez "get"), equals, hashCode, toString
    record Money(long cents, String currency) {
        Money { // compact constructor — validācija
            if (cents < 0) throw new IllegalArgumentException("Negatīva summa");
        }

        Money add(Money other) { return new Money(cents + other.cents, currency); }
    }

    // Pattern matching switch + record deconstruction. Nav vajadzīgs default — sealed garantē pilnību.
    static String describe(PaymentMethod pm) {
        return switch (pm) {
            case Card(String number, String holder) -> "Karte ****" + number.substring(number.length() - 4) + " (" + holder + ")";
            case BankTransfer(String iban) -> "Pārskaitījums uz " + iban;
            case Cash c -> "Skaidra nauda";
        };
    }

    public static void main(String[] args) {
        var list = List.of(new Card("4111111111111111", "J. Bērziņš"), new BankTransfer("LV80BANK..."), new Cash());
        list.forEach(pm -> System.out.println(describe(pm)));

        Money m = new Money(1050, "EUR").add(new Money(250, "EUR"));
        System.out.println(m + " -> cents=" + m.cents());

        // Pattern matching instanceof — vairs nav jākastē
        Object o = "Sveiki";
        if (o instanceof String s && s.length() > 3) {
            System.out.println("Garš strings: " + s.toUpperCase());
        }

        // Text block — ērti JSON/SQL testiem
        String json = """
                {
                  "title": "Dune",
                  "year": 1965
                }
                """;
        System.out.println(json);

        String day = "SAT";
        int hours = switch (day) {
            case "SAT", "SUN" -> 0;
            case "FRI" -> 6;
            default -> 8;
        };
        System.out.println("Darba stundas " + day + ": " + hours);

        // Java 21 Sequenced collections
        var nums = new java.util.ArrayList<>(List.of(1, 2, 3));
        System.out.println("first=" + nums.getFirst() + " last=" + nums.getLast() + " reversed=" + nums.reversed());
    }
}
