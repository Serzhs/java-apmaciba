package lv.macibas.javacore;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * KOLEKCIJAS — kuru izvēlēties un kāpēc. Gandrīz vienmēr jautā!
 *
 * Hierarhija:
 *   Collection
 *    ├── List   (secība, dublikāti atļauti)   ArrayList, LinkedList
 *    ├── Set    (bez dublikātiem)              HashSet, LinkedHashSet, TreeSet
 *    └── Queue/Deque                           ArrayDeque, PriorityQueue, LinkedList
 *   Map (NAV Collection!)                      HashMap, LinkedHashMap, TreeMap, ConcurrentHashMap
 *
 * Big-O (biežākie):
 * ┌───────────────┬────────┬─────────────┬────────────┬──────────────────────────────┐
 * │               │ get(i) │ add (beigās)│ contains   │ piezīme                      │
 * ├───────────────┼────────┼─────────────┼────────────┼──────────────────────────────┤
 * │ ArrayList     │ O(1)   │ O(1)*       │ O(n)       │ *amortizēti; masīvs iekšā    │
 * │ LinkedList    │ O(n)   │ O(1)        │ O(n)       │ praksē reti labāka           │
 * │ HashSet/Map   │ -      │ O(1)        │ O(1)       │ nav secības                  │
 * │ LinkedHash*   │ -      │ O(1)        │ O(1)       │ saglabā ievietošanas secību  │
 * │ TreeSet/Map   │ -      │ O(log n)    │ O(log n)   │ sakārtots (sarkani-melns koks)│
 * │ PriorityQueue │ -      │ O(log n)    │ O(n)       │ peek min O(1) (heap)          │
 * └───────────────┴────────┴─────────────┴────────────┴──────────────────────────────┘
 */
public class CollectionsGuide {

    public static void main(String[] args) {
        // --- List ---
        List<String> list = new ArrayList<>(List.of("c", "a", "b", "a"));
        System.out.println("ArrayList (dublikāti OK): " + list);

        // --- Set: kurš saglabā kādu secību ---
        List<String> input = List.of("banāns", "ābols", "ķirsis", "ābols");
        System.out.println("HashSet       (nav secības): " + new HashSet<>(input));
        System.out.println("LinkedHashSet (ievietošanas): " + new LinkedHashSet<>(input));
        System.out.println("TreeSet       (sakārtots):    " + new TreeSet<>(input));

        // --- Map ---
        Map<String, Integer> wordCount = new HashMap<>();
        for (String w : "the cat and the hat and the bat".split(" ")) {
            wordCount.merge(w, 1, Integer::sum); // ērtāk nekā get + null pārbaude + put
        }
        System.out.println("Vārdu skaits: " + wordCount);
        System.out.println("Sakārtots:    " + new TreeMap<>(wordCount));

        Map<String, List<String>> groups = new LinkedHashMap<>();
        groups.computeIfAbsent("augļi", k -> new ArrayList<>()).add("ābols");
        groups.computeIfAbsent("augļi", k -> new ArrayList<>()).add("bumbieris");
        System.out.println("computeIfAbsent: " + groups);

        // --- Stack un Queue: lieto ArrayDeque (nevis veco Stack klasi) ---
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(1); stack.push(2); stack.push(3);
        System.out.println("Stack pop (LIFO): " + stack.pop());

        Queue<Integer> queue = new ArrayDeque<>();
        queue.offer(1); queue.offer(2); queue.offer(3);
        System.out.println("Queue poll (FIFO): " + queue.poll());

        PriorityQueue<Integer> pq = new PriorityQueue<>(List.of(5, 1, 4, 2));
        System.out.println("PriorityQueue poll (min): " + pq.poll());

        // --- Nemaināmas kolekcijas ---
        List<String> immutable = List.of("x", "y");
        try {
            immutable.add("z");
        } catch (UnsupportedOperationException e) {
            System.out.println("List.of() ir nemaināms ✔ (un neatļauj null!)");
        }

        // --- ConcurrentModificationException klasika ---
        List<Integer> numbers = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6));
        try {
            for (Integer n : numbers) {
                if (n % 2 == 0) numbers.remove(n); // ❌ maina sarakstu iterācijas laikā
            }
        } catch (java.util.ConcurrentModificationException e) {
            System.out.println("ConcurrentModificationException! Pareizi:");
        }
        numbers.removeIf(n -> n % 2 == 0); // ✅ vai Iterator.remove()
        System.out.println("  removeIf: " + numbers);

        Iterator<Integer> it = new LinkedList<>(List.of(1, 2, 3)).iterator();
        while (it.hasNext()) if (it.next() == 2) it.remove();

        // Thread-safe kolekcijas: ConcurrentHashMap, CopyOnWriteArrayList (skat. Concurrency.java)
        // HashMap pieļauj 1 null atslēgu; ConcurrentHashMap un TreeMap — nē.
        Set<String> s = new HashSet<>();
        s.add(null);
        System.out.println("HashSet ar null: " + s);
    }
}
