package lv.macibas.springbasics;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Pārbauda, ka visi mācību piemēri (main metodes) palaižas bez kļūdām.
 * Ja kaut ko maini piemēros un salauz — šis tests to pateiks.
 */
class OopExamplesRunTest {

    @Test
    void allExamplesRun() {
        assertThatCode(() -> {
            String[] none = {};
            lv.macibas.oop.Encapsulation.main(none);
            lv.macibas.oop.Inheritance.main(none);
            lv.macibas.oop.Polymorphism.main(none);
            lv.macibas.oop.Abstraction.main(none);
            lv.macibas.oop.CompositionOverInheritance.main(none);
            lv.macibas.oop.EqualsAndHashCode.main(none);
            lv.macibas.oop.Immutability.main(none);
            lv.macibas.solid.Solid.main(none);
            lv.macibas.patterns.SingletonPattern.main(none);
            lv.macibas.patterns.BuilderPattern.main(none);
            lv.macibas.patterns.FactoryPattern.main(none);
            lv.macibas.patterns.StrategyPattern.main(none);
            lv.macibas.patterns.ObserverPattern.main(none);
            lv.macibas.patterns.DecoratorAndProxyPattern.main(none);
            lv.macibas.javacore.CollectionsGuide.main(none);
            lv.macibas.javacore.StreamsAndLambdas.main(none);
            lv.macibas.javacore.Exceptions.main(none);
            lv.macibas.javacore.Generics.main(none);
            lv.macibas.javacore.ModernJava.main(none);
            lv.macibas.javacore.MemoryAndPassByValue.main(none);
            lv.macibas.javacore.Concurrency.main(none);
            IocAndBeansDemo.main(none);
        }).doesNotThrowAnyException();
    }
}
