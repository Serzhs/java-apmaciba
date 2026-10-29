package lv.macibas.solid;

import java.util.List;

/**
 * SOLID — 5 principi labam OOP dizainam. Jautā TIKPAT KĀ VISĀS intervijās.
 *
 *  S — Single Responsibility  : klasei viens iemesls mainīties
 *  O — Open/Closed            : atvērta paplašināšanai, slēgta modificēšanai
 *  L — Liskov Substitution    : apakšklasi var likt virsklases vietā, nesalaužot uzvedību
 *  I — Interface Segregation  : daudz mazu interfeisu labāk par vienu "resnu"
 *  D — Dependency Inversion   : atkarīgs no abstrakcijām, nevis konkrētām klasēm
 *
 * Padoms: intervijā katram principam nosauc 1 teikumu + 1 piemēru no sava projekta.
 */
public class Solid {

    // =====================================================================
    // S — SINGLE RESPONSIBILITY PRINCIPLE
    // =====================================================================
    // ❌ Viena klase dara visu: validē, saglabā DB, sūta e-pastu, ģenerē PDF.
    //    Ja mainās e-pasta formāts VAI datubāze — jāmaina tā pati klase.
    static class GodUserService {
        void register(String email) {
            /* validācija */
            /* INSERT INTO users ... */
            /* SMTP sūtīšana ... */
            /* PDF ģenerēšana ... */
        }
    }

    // ✅ Katrai atbildībai sava klase
    static class EmailValidator {
        boolean isValid(String email) { return email != null && email.contains("@"); }
    }

    static class UserRepository {
        void save(String email) { System.out.println("  [DB] saglabāts " + email); }
    }

    static class WelcomeMailer {
        void send(String email) { System.out.println("  [MAIL] sveiciens nosūtīts uz " + email); }
    }

    static class UserRegistrationService { // tikai koordinē (orchestrates)
        private final EmailValidator validator = new EmailValidator();
        private final UserRepository repo = new UserRepository();
        private final WelcomeMailer mailer = new WelcomeMailer();

        void register(String email) {
            if (!validator.isValid(email)) throw new IllegalArgumentException("Slikts e-pasts");
            repo.save(email);
            mailer.send(email);
        }
    }

    // =====================================================================
    // O — OPEN/CLOSED PRINCIPLE
    // =====================================================================
    // ❌ Katru reizi, kad parādās jauns klienta tips, jāmaina šī metode
    static double discountBad(String customerType, double price) {
        if (customerType.equals("REGULAR")) return price;
        else if (customerType.equals("VIP")) return price * 0.8;
        else if (customerType.equals("EMPLOYEE")) return price * 0.5;
        return price; // ...un tā bezgalīgi
    }

    // ✅ Jauns tips = jauna klase. Esošais kods netiek aiztikts.
    interface DiscountPolicy {
        double apply(double price);
    }

    static class RegularDiscount implements DiscountPolicy {
        public double apply(double price) { return price; }
    }

    static class VipDiscount implements DiscountPolicy {
        public double apply(double price) { return price * 0.8; }
    }

    static class BlackFridayDiscount implements DiscountPolicy { // pievienots VĒLĀK — nekas cits nemainījās
        public double apply(double price) { return price * 0.6; }
    }

    // =====================================================================
    // L — LISKOV SUBSTITUTION PRINCIPLE
    // =====================================================================
    // ❌ Klasiskais piemērs: Square extends Rectangle.
    //    Matemātiski kvadrāts IR taisnstūris, bet UZVEDĪBA atšķiras.
    static class Rectangle {
        protected int width, height;
        void setWidth(int w) { width = w; }
        void setHeight(int h) { height = h; }
        int area() { return width * height; }
    }

    static class Square extends Rectangle {
        @Override void setWidth(int w) { width = w; height = w; }  // maina abus!
        @Override void setHeight(int h) { width = h; height = h; }
    }

    static void resizeAndCheck(Rectangle r) {
        r.setWidth(5);
        r.setHeight(4);
        // Kods sagaida 20. Ar Square dabūs 16 -> LSP pārkāpts.
        System.out.println("  " + r.getClass().getSimpleName() + " laukums: " + r.area() + " (gaidīts 20)");
    }
    // ✅ Risinājums: nemantot Square no Rectangle; abiem kopīgs interfeiss Shape { int area(); }
    //    un immutable objekti. Citi LSP pārkāpumi: apakšklase met UnsupportedOperationException,
    //    pastiprina priekšnosacījumus vai vājina pēcnosacījumus.

    // =====================================================================
    // I — INTERFACE SEGREGATION PRINCIPLE
    // =====================================================================
    // ❌ "Resns" interfeiss — robotam jāimplementē eat() un sleep()
    interface Worker {
        void work();
        void eat();
        void sleep();
    }

    // ✅ Sadalīts mazos interfeisos
    interface Workable { void work(); }
    interface Eatable { void eat(); }

    static class Human implements Workable, Eatable {
        public void work() { System.out.println("  Cilvēks strādā"); }
        public void eat() { System.out.println("  Cilvēks ēd"); }
    }

    static class Robot implements Workable { // nav jāimplementē eat()
        public void work() { System.out.println("  Robots strādā"); }
    }

    // =====================================================================
    // D — DEPENDENCY INVERSION PRINCIPLE
    // =====================================================================
    // ❌ Augsta līmeņa klase pati izveido zema līmeņa klasi -> cieši sasaistīts, grūti testēt
    static class MySqlDatabase {
        void insert(String data) { System.out.println("  MySQL INSERT " + data); }
    }

    static class ReportServiceBad {
        private final MySqlDatabase db = new MySqlDatabase(); // "new" = cieša sasaiste
        void generate() { db.insert("report"); }
    }

    // ✅ Abi atkarīgi no abstrakcijas; atkarību "iedod" no ārpuses (Dependency Injection)
    interface ReportStorage {
        void store(String data);
    }

    static class PostgresStorage implements ReportStorage {
        public void store(String data) { System.out.println("  Postgres INSERT " + data); }
    }

    static class InMemoryStorage implements ReportStorage { // lieliski testiem
        public void store(String data) { System.out.println("  Atmiņā saglabāts " + data); }
    }

    static class ReportService {
        private final ReportStorage storage;

        ReportService(ReportStorage storage) { // constructor injection — tieši tā dara Spring
            this.storage = storage;
        }

        void generate() { storage.store("report"); }
    }
    // DI (Dependency Injection) ir TEHNIKA, kā sasniegt DIP principu.
    // IoC (Inversion of Control) — ne tu veido objektus, bet "konteiners" (Spring) to dara tavā vietā.

    public static void main(String[] args) {
        System.out.println("S:");
        new UserRegistrationService().register("janis@example.com");

        System.out.println("O:");
        List<DiscountPolicy> policies = List.of(new RegularDiscount(), new VipDiscount(), new BlackFridayDiscount());
        policies.forEach(p -> System.out.println("  " + p.getClass().getSimpleName() + ": " + p.apply(100)));
        System.out.println("  (vecā versija VIP: " + discountBad("VIP", 100) + ")");

        System.out.println("L:");
        resizeAndCheck(new Rectangle());
        resizeAndCheck(new Square());

        System.out.println("I:");
        List<Workable> workers = List.of(new Human(), new Robot());
        workers.forEach(Workable::work);

        System.out.println("D:");
        new ReportServiceBad().generate();
        new ReportService(new PostgresStorage()).generate();
        new ReportService(new InMemoryStorage()).generate();
    }
}
