package lv.macibas.oop;

/**
 * 4. ABSTRAKCIJA (Abstraction)
 *
 * Ideja: parādīt TIKAI to, KAS objekts dara, un paslēpt, KĀ tas to dara.
 * Tu brauc ar mašīnu caur stūri un pedāļiem, nezinot, kā strādā dzinējs.
 *
 * Java rīki: abstract class un interface.
 *
 * ★ BIEŽS JAUTĀJUMS: Abstract class vs Interface?
 * ┌──────────────────────┬──────────────────────────────┬─────────────────────────────────┐
 * │                      │ abstract class               │ interface                       │
 * ├──────────────────────┼──────────────────────────────┼─────────────────────────────────┤
 * │ Mantošana            │ tikai VIENU (extends)        │ VAIRĀKUS (implements)           │
 * │ Stāvoklis (lauki)    │ jā, jebkādi lauki            │ tikai public static final konst.│
 * │ Konstruktors         │ jā                           │ nē                              │
 * │ Metodes ar ķermeni   │ jā                           │ default/static/private (Java 8+)│
 * │ Piekļuves modifikat. │ jebkādi                      │ metodes pēc noklusējuma public  │
 * │ Kad lietot           │ "IS-A" + kopīgs kods/stāvoklis│ "CAN-DO" līgums/spēja          │
 * └──────────────────────┴──────────────────────────────┴─────────────────────────────────┘
 * Piemērs: abstract class Vehicle (ir kopīgs stāvoklis), interface Comparable/Serializable (spēja).
 */
public class Abstraction {

    // Interfeiss = līgums. "Jebkurš, kas implementē, PROT sūtīt maksājumu."
    interface PaymentProcessor {
        PaymentResult pay(String customerId, long amountCents);
    }

    record PaymentResult(boolean success, String transactionId) {}

    // Abstraktā klase = kopīgs skelets + kopīgs kods. "Template Method" šablons.
    abstract static class AbstractPaymentProcessor implements PaymentProcessor {
        private int processedCount = 0; // stāvoklis — interfeisā tā nevar

        // final — algoritma soļi ir fiksēti, apakšklases maina tikai konkrētos soļus
        @Override
        public final PaymentResult pay(String customerId, long amountCents) {
            validate(amountCents);
            String txId = doCharge(customerId, amountCents); // abstraktais solis
            processedCount++;
            log(customerId, amountCents, txId);
            return new PaymentResult(true, txId);
        }

        protected abstract String doCharge(String customerId, long amountCents);

        private void validate(long amountCents) {
            if (amountCents <= 0) throw new IllegalArgumentException("Summai jābūt > 0");
        }

        private void log(String customerId, long amountCents, String txId) {
            System.out.printf("[%s] klients=%s summa=%d tx=%s (kopā apstrādāti: %d)%n",
                    getClass().getSimpleName(), customerId, amountCents, txId, processedCount);
        }
    }

    static class StripeProcessor extends AbstractPaymentProcessor {
        @Override
        protected String doCharge(String customerId, long amountCents) {
            return "stripe_" + System.nanoTime(); // te būtu HTTP izsaukums uz Stripe API
        }
    }

    static class PayPalProcessor extends AbstractPaymentProcessor {
        @Override
        protected String doCharge(String customerId, long amountCents) {
            return "pp_" + System.nanoTime();
        }
    }

    // Klients zina TIKAI par interfeisu — nezina, vai tas ir Stripe vai PayPal.
    static class CheckoutService {
        private final PaymentProcessor processor;

        CheckoutService(PaymentProcessor processor) {
            this.processor = processor;
        }

        void checkout(String customerId, long amountCents) {
            PaymentResult result = processor.pay(customerId, amountCents);
            System.out.println("Pasūtījums apmaksāts: " + result);
        }
    }

    public static void main(String[] args) {
        new CheckoutService(new StripeProcessor()).checkout("c-1", 1999);
        new CheckoutService(new PayPalProcessor()).checkout("c-2", 4999);
        // Tieši šādi strādā Spring: tu injicē interfeisu, Spring ieliek implementāciju.
    }
}
