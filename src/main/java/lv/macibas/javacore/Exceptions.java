package lv.macibas.javacore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * IZŅĒMUMI (Exceptions)
 *
 *            Throwable
 *           /         \
 *       Error        Exception
 *  (OutOfMemory,     /         \
 *  StackOverflow)  IOException  RuntimeException
 *   — neķer!       SQLException  (NullPointer, IllegalArgument,
 *                  (CHECKED)      IllegalState, IndexOutOfBounds)
 *                                 (UNCHECKED)
 *
 * Checked:   kompilators PIESPIEŽ apstrādāt (try/catch vai throws). Ārējas problēmas (fails, tīkls).
 * Unchecked: RuntimeException apakšklases, nav jādeklarē. Parasti programmētāja kļūda vai biznesa likums.
 *
 * Spring/moderns Java: biznesa kļūdām lieto UNCHECKED (piem. BookNotFoundException extends RuntimeException).
 * Svarīgi: Spring @Transactional pēc noklusējuma rollback dara TIKAI uz unchecked izņēmumiem!
 *
 * Labās prakses:
 *  - Neķer Exception/Throwable "visu pēc kārtas", ja nezini, ko ar to darīt
 *  - Nekad "norij" izņēmumu tukšā catch blokā
 *  - Ietin (wrap) ar cēloni: new MyException("...", e) — lai nepazūd stack trace
 *  - Resursus aizver ar try-with-resources
 *  - Met specifisku izņēmumu ar saprotamu ziņu
 */
public class Exceptions {

    // Savs biznesa izņēmums
    static class InsufficientStockException extends RuntimeException {
        private final String productId;

        InsufficientStockException(String productId, int requested, int available) {
            super("Produktam " + productId + " pieprasīti " + requested + ", pieejami " + available);
            this.productId = productId;
        }

        String getProductId() { return productId; }
    }

    static void reserve(String productId, int qty) {
        int available = 3;
        if (qty > available) throw new InsufficientStockException(productId, qty, available);
        System.out.println("Rezervēti " + qty);
    }

    // try-with-resources: resurss tiek aizvērts automātiski (tam jāimplementē AutoCloseable)
    static String readFirstLine(String text) {
        try (BufferedReader reader = new BufferedReader(new StringReader(text))) {
            return reader.readLine();
        } catch (IOException e) {
            // Wrap checked -> unchecked, saglabājot cēloni
            throw new IllegalStateException("Neizdevās nolasīt", e);
        }
    }

    // finally izpildās VIENMĒR (pat ja ir return) — triku jautājums
    @SuppressWarnings("finally")
    static int finallyTrick() {
        try {
            return 1;
        } finally {
            System.out.println("finally izpildījās pirms return");
        }
    }

    public static void main(String[] args) {
        try {
            reserve("book-1", 10);
        } catch (InsufficientStockException e) {
            System.out.println("Noķerts: " + e.getMessage() + " (produkts: " + e.getProductId() + ")");
        }

        System.out.println("Pirmā rinda: " + readFirstLine("sveiki\npasaule"));
        System.out.println("finallyTrick() = " + finallyTrick());

        // Multi-catch
        try {
            Object o = "teksts";
            Integer i = (Integer) o;
        } catch (ClassCastException | NullPointerException e) {
            System.out.println("Multi-catch: " + e.getClass().getSimpleName());
        }

        // throw vs throws:
        //   throw  — izmet izņēmumu (darbība):          throw new X();
        //   throws — deklarē metodes parakstā (brīdinājums): void read() throws IOException
    }
}
