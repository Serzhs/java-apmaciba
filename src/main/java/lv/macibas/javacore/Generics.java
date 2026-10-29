package lv.macibas.javacore;

import java.util.ArrayList;
import java.util.List;

/**
 * GENERICS — tipu drošība kompilācijas laikā + koda atkārtota izmantošana.
 *
 * Intervijā:
 *  - Type erasure: izpildes laikā List<String> un List<Integer> ir viens un tas pats List.
 *    Tāpēc nevar new T(), nevar instanceof List<String>.
 *  - Wildcards — PECS: "Producer Extends, Consumer Super"
 *      <? extends T> — no kolekcijas LASI (tā tev "ražo" T)
 *      <? super T>   — kolekcijā RAKSTI (tā "patērē" T)
 *  - Bounded type: <T extends Comparable<T>> — T jābūt salīdzināmam
 */
public class Generics {

    // Ģeneriska klase — tas pats kods jebkuram tipam
    static class Box<T> {
        private final T value;

        Box(T value) { this.value = value; }

        T get() { return value; }

        <R> Box<R> map(java.util.function.Function<T, R> fn) { return new Box<>(fn.apply(value)); }
    }

    // Ģeneriska metode ar ierobežojumu
    static <T extends Comparable<T>> T max(List<T> items) {
        T best = items.get(0);
        for (T item : items) if (item.compareTo(best) > 0) best = item;
        return best;
    }

    // PECS: producer extends — lasām skaitļus
    static double sum(List<? extends Number> numbers) {
        double total = 0;
        for (Number n : numbers) total += n.doubleValue();
        return total;
    }

    // PECS: consumer super — rakstām Integer
    static void fillWithOnes(List<? super Integer> target, int count) {
        for (int i = 0; i < count; i++) target.add(1);
    }

    // Kāpēc List<Integer> NAV List<Number>? Ja būtu, varētu ielikt Double Integer sarakstā!
    public static void main(String[] args) {
        Box<String> box = new Box<>("spring");
        Box<Integer> len = box.map(String::length);
        System.out.println("Box: " + box.get() + " -> " + len.get());

        System.out.println("max: " + max(List.of(3, 9, 2)) + ", " + max(List.of("ābols", "zivs", "banāns")));

        System.out.println("sum Integer: " + sum(List.of(1, 2, 3)));
        System.out.println("sum Double:  " + sum(List.of(1.5, 2.5)));

        List<Number> numbers = new ArrayList<>();
        fillWithOnes(numbers, 3);
        System.out.println("fillWithOnes: " + numbers);

        // Type erasure demo
        List<String> a = new ArrayList<>();
        List<Integer> b = new ArrayList<>();
        System.out.println("Tā pati klase runtime? " + (a.getClass() == b.getClass()));
    }
}
