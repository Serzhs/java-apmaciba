package lv.macibas.javacore;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * DAUDZPAVEDIENU PROGRAMMĒŠANA (Concurrency) — backend serveris apstrādā daudz pieprasījumu VIENLAICĪGI,
 * tāpēc tas ir svarīgi. Spring MVC/Tomcat: katrs HTTP pieprasījums = savs pavediens (thread).
 *
 * Pamatjēdzieni:
 *  - Race condition: divi pavedieni vienlaikus maina datus -> nepareizs rezultāts
 *  - synchronized: tikai viens pavediens vienlaikus var izpildīt bloku (lock/monitor)
 *  - volatile: garantē, ka izmaiņas ir REDZAMAS citiem pavedieniem (bet NEgarantē atomaritāti!)
 *  - Atomic* klases: lock-free atomāras operācijas (CAS — compare-and-swap)
 *  - Deadlock: A gaida B lock, B gaida A lock -> abi stāv mūžīgi
 *  - ExecutorService: pavedienu baseins (thread pool) — nevajag veidot new Thread() katram uzdevumam
 *  - CompletableFuture: asinhronas operācijas ķēdes
 *  - Virtual threads (Java 21): ļoti "lēti" pavedieni — var palaist miljoniem, ideāli I/O darbiem
 *
 * Spring beani ir singletoni -> TIEK DALĪTI starp pavedieniem. Tāpēc servisos NEGLABĀ mainīgu stāvokli laukos!
 */
public class Concurrency {

    static int unsafeCounter = 0;
    static int syncCounter = 0;
    static final AtomicInteger atomicCounter = new AtomicInteger();

    static synchronized void incrementSync() { syncCounter++; }

    public static void main(String[] args) throws Exception {
        // --- Race condition ---
        try (ExecutorService pool = Executors.newFixedThreadPool(8)) {
            for (int i = 0; i < 10_000; i++) {
                pool.submit(() -> {
                    unsafeCounter++;              // ❌ nav atomāri: read -> +1 -> write
                    incrementSync();              // ✅ synchronized
                    atomicCounter.incrementAndGet(); // ✅ atomic
                });
            }
        } // Java 19+: ExecutorService ir AutoCloseable — close() sagaida visus uzdevumus
        System.out.println("unsafe: " + unsafeCounter + " (bieži < 10000!)");
        System.out.println("sync:   " + syncCounter);
        System.out.println("atomic: " + atomicCounter.get());

        // --- ConcurrentHashMap ---
        Map<String, Integer> visits = new ConcurrentHashMap<>();
        try (ExecutorService pool = Executors.newFixedThreadPool(4)) {
            for (int i = 0; i < 1000; i++) {
                pool.submit(() -> visits.merge("home", 1, Integer::sum)); // atomāra operācija
            }
        }
        System.out.println("ConcurrentHashMap visits: " + visits);

        // --- CompletableFuture: paralēli izsauc 3 "servisus" un apvieno ---
        long start = System.currentTimeMillis();
        CompletableFuture<String> user = CompletableFuture.supplyAsync(() -> slowCall("user", 300));
        CompletableFuture<String> orders = CompletableFuture.supplyAsync(() -> slowCall("orders", 300));
        CompletableFuture<String> recs = CompletableFuture.supplyAsync(() -> slowCall("recommendations", 300));
        String page = user.thenCombine(orders, (u, o) -> u + " + " + o)
                .thenCombine(recs, (uo, r) -> uo + " + " + r)
                .exceptionally(ex -> "kļūda: " + ex.getMessage())
                .get(2, TimeUnit.SECONDS);
        System.out.println("Lapa: " + page + " (" + (System.currentTimeMillis() - start) + " ms, nevis 900)");

        // --- Virtual threads (Java 21) ---
        start = System.currentTimeMillis();
        try (ExecutorService vexec = Executors.newVirtualThreadPerTaskExecutor()) {
            List<? extends java.util.concurrent.Future<?>> futures = IntStream.range(0, 10_000)
                    .mapToObj(i -> vexec.submit(() -> slowCall("x", 100)))
                    .toList();
            System.out.println("Palaisti " + futures.size() + " virtuālie pavedieni");
        }
        System.out.println("10k virtual threads ar 100ms gaidīšanu: " + (System.currentTimeMillis() - start) + " ms");
        // Spring Boot 3.2+: spring.threads.virtual.enabled=true — Tomcat izmanto virtuālos pavedienus
    }

    static String slowCall(String name, long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // labā prakse: atjauno interrupt flagu
        }
        return name;
    }
}
