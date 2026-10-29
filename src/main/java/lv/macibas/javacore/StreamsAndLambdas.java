package lv.macibas.javacore;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * STREAMS, LAMBDAS, OPTIONAL (Java 8+)
 *
 * Lambda = īss veids, kā uzrakstīt FUNKCIONĀLA INTERFEISA (viena abstrakta metode) implementāciju.
 *   Predicate<T>   T -> boolean        filter
 *   Function<T,R>  T -> R              map
 *   Consumer<T>    T -> void           forEach
 *   Supplier<T>    () -> T             orElseGet, lazy
 *
 * Stream:
 *   - NEglabā datus, apstrādā tos (pipeline)
 *   - intermediate operācijas (filter, map, sorted) ir LAZY — nekas nenotiek līdz terminal operācijai
 *   - terminal operācijas: collect, forEach, count, reduce, findFirst, anyMatch
 *   - stream var izmantot TIKAI VIENREIZ
 *   - map vs flatMap: map 1->1, flatMap 1->daudzi (saplacina List<List<X>> -> List<X>)
 */
public class StreamsAndLambdas {

    record Employee(String name, String department, double salary, List<String> skills) {}

    public static void main(String[] args) {
        List<Employee> employees = List.of(
                new Employee("Anna", "IT", 3500, List.of("Java", "SQL")),
                new Employee("Jānis", "IT", 2800, List.of("Java", "Spring", "Docker")),
                new Employee("Līga", "HR", 2200, List.of("Excel")),
                new Employee("Pēteris", "Sales", 2600, List.of("Excel", "CRM")),
                new Employee("Marta", "IT", 4100, List.of("Kotlin", "SQL"))
        );

        // filter + map + collect
        List<String> itNames = employees.stream()
                .filter(e -> e.department().equals("IT"))
                .map(Employee::name)               // method reference
                .sorted()
                .toList();                          // Java 16+
        System.out.println("IT darbinieki: " + itNames);

        // groupingBy — ĻOTI bieži live coding uzdevumos
        Map<String, Long> countByDept = employees.stream()
                .collect(Collectors.groupingBy(Employee::department, Collectors.counting()));
        System.out.println("Skaits pa nodaļām: " + countByDept);

        Map<String, Double> avgSalary = employees.stream()
                .collect(Collectors.groupingBy(Employee::department, Collectors.averagingDouble(Employee::salary)));
        System.out.println("Vidējā alga: " + avgSalary);

        // Augstākais atalgojums
        Optional<Employee> top = employees.stream().max(Comparator.comparingDouble(Employee::salary));
        System.out.println("Top alga: " + top.map(Employee::name).orElse("nav"));

        // Sorting ar vairākiem kritērijiem
        employees.stream()
                .sorted(Comparator.comparing(Employee::department).thenComparing(Employee::salary, Comparator.reverseOrder()))
                .forEach(e -> System.out.println("  " + e.department() + " " + e.name() + " " + e.salary()));

        // flatMap — visas unikālās prasmes
        List<String> allSkills = employees.stream()
                .flatMap(e -> e.skills().stream())
                .distinct()
                .sorted()
                .toList();
        System.out.println("Visas prasmes: " + allSkills);

        // reduce
        double totalPayroll = employees.stream().mapToDouble(Employee::salary).sum();
        int sumOfSquares = IntStream.rangeClosed(1, 5).map(x -> x * x).reduce(0, Integer::sum);
        System.out.println("Algu fonds: " + totalPayroll + ", kvadrātu summa 1..5: " + sumOfSquares);

        // partitioningBy — sadala divās grupās pēc boolean
        Map<Boolean, List<String>> highEarners = employees.stream()
                .collect(Collectors.partitioningBy(e -> e.salary() > 3000,
                        Collectors.mapping(Employee::name, Collectors.toList())));
        System.out.println("> 3000: " + highEarners.get(true));

        // toMap — uzmanies: dublikāta atslēga = IllegalStateException, ja nedod merge funkciju
        Map<String, Double> salaryByName = employees.stream()
                .collect(Collectors.toMap(Employee::name, Employee::salary));
        System.out.println("Jāņa alga: " + salaryByName.get("Jānis"));

        // Laziness demo — filter izsaucas tikai līdz pirmajam atrastajam
        employees.stream()
                .filter(e -> { System.out.println("  filter: " + e.name()); return e.salary() > 3000; })
                .findFirst()
                .ifPresent(e -> System.out.println("  atrasts: " + e.name()));

        // --- Funkcionālie interfeisi ---
        Predicate<String> isLong = s -> s.length() > 4;
        Function<String, Integer> length = String::length;
        Supplier<String> lazyDefault = () -> "noklusējums";
        System.out.println(isLong.test("Spring") + " " + length.apply("Java") + " " + lazyDefault.get());

        // --- OPTIONAL ---
        // Mērķis: skaidri pateikt "vērtības var nebūt" un izvairīties no NullPointerException.
        // ✅ Lieto kā metodes atgriežamo tipu. ❌ Nelieto kā lauku, parametru vai kolekcijās.
        Optional<String> maybe = findNickname("Anna");
        System.out.println("Iesauka: " + maybe.orElse("nav"));
        System.out.println("Iesauka2: " + findNickname("Jānis").map(String::toUpperCase).orElse("nav"));
        // orElse(x)       — x tiek izrēķināts VIENMĒR
        // orElseGet(() -> x) — x tiek izrēķināts tikai ja vajag (lazy)
        // orElseThrow()   — met NoSuchElementException vai tavu izņēmumu
        // ❌ optional.get() bez pārbaudes — tikpat slikti kā null
    }

    static Optional<String> findNickname(String name) {
        return name.equals("Jānis") ? Optional.of("Jancis") : Optional.empty();
    }
}
