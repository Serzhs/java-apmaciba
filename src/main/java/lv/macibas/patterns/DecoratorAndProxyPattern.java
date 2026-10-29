package lv.macibas.patterns;

import java.util.HashMap;
import java.util.Map;

/**
 * DECORATOR un PROXY — Structural (struktūras) šabloni.
 * Abi "iesaiņo" objektu ar to pašu interfeisu.
 *
 *  - Decorator: PIEVIENO uzvedību (logošana, kešošana, retry), var likt vienu virs otra.
 *    Dabā: java.io — new BufferedReader(new InputStreamReader(new FileInputStream(...)))
 *  - Proxy: KONTROLĒ piekļuvi oriģinālam (lazy load, drošība, transakcijas, attālināts izsaukums).
 *
 * ★ SVARĪGI SPRING KONTEKSTĀ: @Transactional, @Cacheable, @Async, @PreAuthorize strādā,
 *   jo Spring ap tavu beanu izveido PROXY objektu. Tāpēc:
 *   - self-invocation (this.otherMethod()) apiet proxy -> @Transactional NESTRĀDĀ!
 *   - private metodes netiek proksētas.
 *   Tas ir ļoti populārs "triku" jautājums.
 */
public class DecoratorAndProxyPattern {

    interface PriceService {
        double getPrice(String productId);
    }

    static class RealPriceService implements PriceService {
        public double getPrice(String productId) {
            System.out.println("    (lēns izsaukums uz ārējo API par " + productId + ")");
            return productId.length() * 10.0;
        }
    }

    // Decorator: logošana
    static class LoggingPriceService implements PriceService {
        private final PriceService delegate;

        LoggingPriceService(PriceService delegate) { this.delegate = delegate; }

        public double getPrice(String productId) {
            long start = System.nanoTime();
            double price = delegate.getPrice(productId);
            System.out.printf("  [LOG] getPrice(%s)=%.2f, %d µs%n", productId, price, (System.nanoTime() - start) / 1000);
            return price;
        }
    }

    // Decorator/Proxy: kešošana
    static class CachingPriceService implements PriceService {
        private final PriceService delegate;
        private final Map<String, Double> cache = new HashMap<>();

        CachingPriceService(PriceService delegate) { this.delegate = delegate; }

        public double getPrice(String productId) {
            return cache.computeIfAbsent(productId, delegate::getPrice);
        }
    }

    public static void main(String[] args) {
        // Slāņi: Logging -> Caching -> Real
        PriceService service = new LoggingPriceService(new CachingPriceService(new RealPriceService()));

        service.getPrice("book-1"); // iet līdz Real
        service.getPrice("book-1"); // no keša — nav "lēnā izsaukuma"
        service.getPrice("book-22");
    }
}
