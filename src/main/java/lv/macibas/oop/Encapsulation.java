package lv.macibas.oop;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 1. IEKAPSULĒŠANA (Encapsulation)
 *
 * Ideja: objekts PATS sargā savu stāvokli. Lauki ir private, un mainīt tos var
 * tikai caur metodēm, kas pārbauda biznesa likumus (invariantus).
 *
 * Intervijā saki: "Iekapsulēšana = datu slēpšana + kontrolēta piekļuve.
 * Tas ļauj garantēt, ka objekts nekad nav nederīgā stāvoklī, un ļauj mainīt
 * iekšējo implementāciju, nesalaužot kodu, kas objektu izmanto."
 *
 * Palaid: IntelliJ -> zaļā bultiņa pie main().
 */
public class Encapsulation {

    // ❌ SLIKTI: publiski lauki — jebkurš var ielikt negatīvu bilanci
    static class BadAccount {
        public BigDecimal balance = BigDecimal.ZERO;
    }

    // ✅ LABI: stāvoklis ir slēpts, izmaiņas tikai caur metodēm ar validāciju
    static class BankAccount {
        private final String iban;                    // final — nevar nomainīt pēc izveides
        private BigDecimal balance = BigDecimal.ZERO; // private — neviens no ārpuses neaiztiek
        private final List<String> history = new ArrayList<>();

        BankAccount(String iban) {
            if (iban == null || iban.isBlank()) {
                throw new IllegalArgumentException("IBAN ir obligāts");
            }
            this.iban = iban;
        }

        void deposit(BigDecimal amount) {
            requirePositive(amount);
            balance = balance.add(amount);
            history.add("+" + amount);
        }

        void withdraw(BigDecimal amount) {
            requirePositive(amount);
            if (balance.compareTo(amount) < 0) {
                // Invariants: bilance nekad nav negatīva
                throw new IllegalStateException("Nepietiek līdzekļu");
            }
            balance = balance.subtract(amount);
            history.add("-" + amount);
        }

        BigDecimal getBalance() {
            return balance; // BigDecimal ir immutable, tāpēc droši atgriezt
        }

        // ⚠️ Svarīgi: NEatgriez iekšējo sarakstu tieši, citādi ārējais kods var to mainīt!
        List<String> getHistory() {
            return Collections.unmodifiableList(history);
        }

        String getIban() {
            return iban;
        }

        // Nav setBalance()! Ne katram laukam vajag setteri.
        // "Tell, don't ask" — saki objektam, KO darīt (withdraw), nevis ņem datus un rēķini ārā.

        private static void requirePositive(BigDecimal amount) {
            if (amount == null || amount.signum() <= 0) {
                throw new IllegalArgumentException("Summai jābūt pozitīvai");
            }
        }
    }

    public static void main(String[] args) {
        BadAccount bad = new BadAccount();
        bad.balance = new BigDecimal("-1000000"); // nekas mūs neaptur 😬
        System.out.println("Slikts konts: " + bad.balance);

        BankAccount account = new BankAccount("LV80BANK0000435195001");
        account.deposit(new BigDecimal("100"));
        account.withdraw(new BigDecimal("30"));
        System.out.println("Bilance: " + account.getBalance());
        System.out.println("Vēsture: " + account.getHistory());

        try {
            account.withdraw(new BigDecimal("1000"));
        } catch (IllegalStateException e) {
            System.out.println("Aizsardzība nostrādāja: " + e.getMessage());
        }

        try {
            account.getHistory().add("hack");
        } catch (UnsupportedOperationException e) {
            System.out.println("Vēsturi no ārpuses mainīt nevar ✔");
        }
    }
}
