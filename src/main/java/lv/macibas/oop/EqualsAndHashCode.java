package lv.macibas.oop;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 6. equals() un hashCode() — ĻOTI biežs intervijas jautājums!
 *
 * ==       salīdzina ATSAUCES (vai tas ir tas pats objekts atmiņā)
 * equals() salīdzina SATURU (ja klase to pārraksta)
 *
 * LĪGUMS (contract):
 *  1. Ja a.equals(b) == true, tad a.hashCode() == b.hashCode()  ← OBLIGĀTI
 *  2. Ja hashCode vienāds, objekti NAV obligāti vienādi (kolīzija ir OK)
 *  3. equals jābūt: refleksīvam, simetriskam, tranzitīvam, konsekventam, x.equals(null)==false
 *
 * Ja pārraksti equals, bet NE hashCode -> HashMap/HashSet salūzt (skat. demo zemāk).
 *
 * Kā strādā HashMap (arī bieži jautā):
 *  - hashCode() -> nosaka "bucket" (masīva indeksu)
 *  - bucket iekšā ar equals() meklē īsto atslēgu
 *  - kolīzijas glabā sarakstā; ja bucketā > 8 elementi -> pārvēršas par sarkani-melno koku (Java 8+)
 *  - get/put vidēji O(1), sliktākajā gadījumā O(log n)
 *  - load factor 0.75 -> kad pilns par 75%, masīvs dubultojas (rehash)
 */
public class EqualsAndHashCode {

    // ❌ Tikai equals, bez hashCode
    static class BrokenPoint {
        final int x, y;

        BrokenPoint(int x, int y) { this.x = x; this.y = y; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof BrokenPoint p)) return false;
            return x == p.x && y == p.y;
        }
        // hashCode() aizmirsts -> tiek lietots Object.hashCode() (atšķirīgs katram objektam)
    }

    // ✅ Pareizi
    static final class Point {
        private final int x, y;

        Point(int x, int y) { this.x = x; this.y = y; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point p = (Point) o;
            return x == p.x && y == p.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y); // jāizmanto TIE PAŠI lauki, kas equals
        }
    }

    // ✅✅ Vēl vienkāršāk: record automātiski ģenerē equals/hashCode/toString
    record PointRecord(int x, int y) {}

    public static void main(String[] args) {
        // == vs equals ar String
        String a = "hello";
        String b = "hello";              // String pool — tas pats objekts
        String c = new String("hello");  // jauns objekts heapā
        System.out.println("a == b: " + (a == b));          // true
        System.out.println("a == c: " + (a == c));          // false!
        System.out.println("a.equals(c): " + a.equals(c));  // true
        // Secinājums: Stringus VIENMĒR salīdzini ar equals()

        // Integer cache slazds: -128..127 tiek kešoti
        Integer i1 = 127, i2 = 127, i3 = 128, i4 = 128;
        System.out.println("Integer 127 == 127: " + (i1 == i2)); // true
        System.out.println("Integer 128 == 128: " + (i3 == i4)); // false!

        Set<BrokenPoint> brokenSet = new HashSet<>();
        brokenSet.add(new BrokenPoint(1, 2));
        System.out.println("Broken contains(1,2)? " + brokenSet.contains(new BrokenPoint(1, 2))); // false 😱

        Set<Point> set = new HashSet<>();
        set.add(new Point(1, 2));
        System.out.println("Point contains(1,2)? " + set.contains(new Point(1, 2))); // true

        Map<PointRecord, String> map = new HashMap<>();
        map.put(new PointRecord(0, 0), "sākums");
        System.out.println("Record atslēga: " + map.get(new PointRecord(0, 0)));

        // ⚠️ Nekad nemaini objektu, kas ir HashMap atslēga — tā hashCode mainīsies un to vairs neatradīs.
        // Tāpēc atslēgām lieto immutable objektus (String, Integer, record).
    }
}
