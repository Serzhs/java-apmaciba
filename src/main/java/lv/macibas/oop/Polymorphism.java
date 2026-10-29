package lv.macibas.oop;

import java.util.List;

/**
 * 3. POLIMORFISMS (Polymorphism) — "daudzas formas"
 *
 * Divi veidi:
 *  a) Compile-time (statiskais) = METHOD OVERLOADING
 *     Viens metodes nosaukums, dažādi parametri. Kompilators izvēlas, kuru izsaukt.
 *  b) Runtime (dinamiskais) = METHOD OVERRIDING
 *     Apakšklase pārraksta metodi; JVM izpildes laikā izvēlas īsto implementāciju
 *     pēc OBJEKTA faktiskā tipa, nevis mainīgā tipa ("dynamic dispatch").
 *
 * Intervijā saki: "Polimorfisms ļauj rakstīt kodu, kas strādā ar abstrakciju (interfeisu),
 * un pievienot jaunas implementācijas, nemainot esošo kodu (Open/Closed princips)."
 */
public class Polymorphism {

    interface Shape {
        double area();

        // default metode interfeisā (Java 8+)
        default String info() {
            return getClass().getSimpleName() + " laukums = " + String.format("%.2f", area());
        }
    }

    record Circle(double radius) implements Shape {
        public double area() {
            return Math.PI * radius * radius;
        }
    }

    record Rectangle(double width, double height) implements Shape {
        public double area() {
            return width * height;
        }
    }

    record Square(double side) implements Shape {
        public double area() {
            return side * side;
        }
    }

    // --- OVERLOADING (compile-time) ---
    static class Calculator {
        int add(int a, int b) {
            return a + b;
        }

        double add(double a, double b) {
            return a + b;
        }

        int add(int a, int b, int c) {
            return a + b + c;
        }
        // ⚠️ Nevar overloadot tikai pēc atgriežamā tipa — kompilācijas kļūda!
    }

    // Šī metode nezina un neinteresējas, kādas konkrēti figūras ir sarakstā.
    // Pievienojot Triangle, šī metode NAV jāmaina.
    static double totalArea(List<Shape> shapes) {
        double total = 0;
        for (Shape s : shapes) {
            total += s.area(); // dynamic dispatch — JVM izvēlas pareizo area()
        }
        return total;
    }

    public static void main(String[] args) {
        // Mainīgā tips = Shape, objekta tips = Circle
        Shape shape = new Circle(1);
        System.out.println(shape.info());

        List<Shape> shapes = List.of(new Circle(2), new Rectangle(2, 3), new Square(4));
        shapes.forEach(s -> System.out.println(s.info()));
        System.out.printf("Kopā: %.2f%n", totalArea(shapes));

        Calculator calc = new Calculator();
        System.out.println("add(int,int) = " + calc.add(1, 2));
        System.out.println("add(double,double) = " + calc.add(1.5, 2.5));
        System.out.println("add(int,int,int) = " + calc.add(1, 2, 3));

        // ❌ Anti-pattern: instanceof ķēdes vietā, kur derētu polimorfisms
        // if (s instanceof Circle) {...} else if (s instanceof Rectangle) {...}
        // Moderns izņēmums: sealed interfeisi + switch pattern matching (skat. javacore/ModernJava.java)
    }
}
