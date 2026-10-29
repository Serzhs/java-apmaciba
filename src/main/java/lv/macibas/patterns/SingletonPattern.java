package lv.macibas.patterns;

/**
 * SINGLETON — klasei ir tikai VIENS eksemplārs visā aplikācijā.
 * Tips: Creational (radīšanas) šablons.
 *
 * Intervijā:
 *  - Kā uztaisīt thread-safe? -> enum, "holder" klase vai double-checked locking ar volatile.
 *  - Kāpēc singletons bieži tiek uzskatīts par anti-pattern? -> globāls stāvoklis,
 *    grūti testēt (nevar viegli aizvietot ar mock), slēptas atkarības.
 *  - Spring beani pēc noklusējuma ir singleton scope — bet tas ir "viens uz Spring konteineru",
 *    nevis klasiskais GoF singleton ar private konstruktoru. Spring to pārvalda, un to var injicēt/mockot.
 */
public class SingletonPattern {

    // 1) Vienkāršākais un drošākais (Effective Java iesaka): enum
    enum AppConfig {
        INSTANCE;

        private final String env = "dev";

        String env() { return env; }
    }

    // 2) "Initialization-on-demand holder" — lazy + thread-safe bez sinhronizācijas
    static final class IdGenerator {
        private long counter = 0;

        private IdGenerator() {} // private konstruktors — nevar izsaukt "new"

        private static class Holder {
            // JVM garantē, ka klase tiek inicializēta vienreiz un thread-safe
            private static final IdGenerator INSTANCE = new IdGenerator();
        }

        static IdGenerator getInstance() { return Holder.INSTANCE; }

        synchronized long next() { return ++counter; }
    }

    // 3) Double-checked locking — jāzina, jo jautā "kāpēc volatile?"
    static final class Cache {
        // volatile novērš to, ka cits pavediens redz daļēji inicializētu objektu (instrukciju pārkārtošana)
        private static volatile Cache instance;

        private Cache() {}

        static Cache getInstance() {
            if (instance == null) {                 // 1. pārbaude bez lock (ātri)
                synchronized (Cache.class) {
                    if (instance == null) {         // 2. pārbaude ar lock
                        instance = new Cache();
                    }
                }
            }
            return instance;
        }
    }

    public static void main(String[] args) {
        System.out.println("Env: " + AppConfig.INSTANCE.env());
        System.out.println("Tas pats objekts? " + (IdGenerator.getInstance() == IdGenerator.getInstance()));
        System.out.println("ID: " + IdGenerator.getInstance().next() + ", " + IdGenerator.getInstance().next());
        System.out.println("Cache tas pats? " + (Cache.getInstance() == Cache.getInstance()));
    }
}
