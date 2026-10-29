package lv.macibas.javacore;

import java.util.ArrayList;
import java.util.List;

/**
 * ATMIŅA, PASS-BY-VALUE, STRING POOL, GC
 *
 * ★ "Vai Java ir pass-by-value vai pass-by-reference?"
 *   -> VIENMĒR pass-by-value. Objektiem tiek nodota ATSAUCES KOPIJA.
 *      Tāpēc metode var MAINĪT objekta saturu, bet NEVAR likt izsaucēja mainīgajam norādīt uz citu objektu.
 *
 * Stack vs Heap:
 *  - Stack: katram pavedienam savs; lokālie mainīgie, primitīvi, atsauces; metodes izsaukumu rāmji.
 *           Pārāk dziļa rekursija -> StackOverflowError
 *  - Heap: visi objekti (new ...); kopīgs visiem pavedieniem; to tīra Garbage Collector.
 *           Pārāk daudz objektu -> OutOfMemoryError
 *
 * Garbage Collection:
 *  - Objekts tiek savākts, kad uz to vairs nav sasniedzamu atsauču (no "GC roots")
 *  - Paaudzes: Young (Eden + Survivor) -> Old. Lielākā daļa objektu "mirst jauni".
 *  - Moderni GC: G1 (noklusējums), ZGC (ļoti mazas pauzes)
 *  - Atmiņas noplūde Javā = objekts joprojām sasniedzams, bet vairs nevajadzīgs
 *    (piem. statisks Map, kurā tikai pievieno, nekad netīra)
 *
 * String vs StringBuilder vs StringBuffer:
 *  - String: immutable; "a" + "b" ciklā rada daudz jaunu objektu
 *  - StringBuilder: mutable, ātrs, NAV thread-safe — lieto šo
 *  - StringBuffer: mutable, thread-safe (synchronized), lēnāks — mūsdienās reti
 */
public class MemoryAndPassByValue {

    static class Person {
        String name;
        Person(String name) { this.name = name; }
    }

    static void modifyPrimitive(int x) { x = 100; }

    static void modifyObject(Person p) { p.name = "Mainīts"; }       // maina SATURU — redzams ārā

    static void reassign(Person p) { p = new Person("Jauns"); }      // maina LOKĀLO kopiju — ārā neredz

    static void addToList(List<String> list) { list.add("pievienots"); }

    public static void main(String[] args) {
        int a = 1;
        modifyPrimitive(a);
        System.out.println("Primitīvs pēc metodes: " + a); // 1

        Person p = new Person("Oriģināls");
        modifyObject(p);
        System.out.println("Pēc modifyObject: " + p.name); // Mainīts

        reassign(p);
        System.out.println("Pēc reassign: " + p.name); // joprojām Mainīts!

        List<String> list = new ArrayList<>();
        addToList(list);
        System.out.println("Saraksts: " + list);

        // StringBuilder ciklā
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) sb.append(i).append(',');
        System.out.println("StringBuilder: " + sb);

        // String pool + intern()
        String s1 = "java";
        String s2 = new String("java").intern();
        System.out.println("intern() atgriež pool objektu: " + (s1 == s2));

        Runtime rt = Runtime.getRuntime();
        System.out.printf("Heap: lietots %d MB / max %d MB%n",
                (rt.totalMemory() - rt.freeMemory()) / 1_048_576, rt.maxMemory() / 1_048_576);
    }
}
