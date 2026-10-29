# 8. nodarbība — Testēšana

> ⏱ ~45 min. Ja esi rakstījis Jest testus — šis būs viegli.

---

## Testu piramīda

```
          /\
         /E2E\        ← maz: lēni, bet pārbauda visu kopā     (Cypress/Playwright)
        /------\
       /  Slāņa \     ← vidēji: API bez īsta servera          (MSW / React Testing Library)
      /----------\
     /    Unit    \   ← daudz: ātri, viena klase/funkcija     (Jest)
    /--------------\
```

| Java/Spring | Ko testē | Ātrums | FE analogs | Mūsu fails |
|---|---|---|---|---|
| **JUnit + Mockito** | Viena klase, atkarības viltotas | ⚡ ms | Jest + `jest.mock()` | [LoanServiceTest](../src/test/java/lv/macibas/library/LoanServiceTest.java) |
| **@WebMvcTest** | Tikai kontrolieris (URL, JSON, statusi) | 🚀 ~1s | MSW / RTL | [BookControllerTest](../src/test/java/lv/macibas/library/BookControllerTest.java) |
| **@DataJpaTest** | Tikai DB vaicājumi | 🚀 ~1s | — | [BookRepositoryTest](../src/test/java/lv/macibas/library/BookRepositoryTest.java) |
| **@SpringBootTest** | Visa aplikācija kopā | 🐢 vairākas s | Cypress | [LibraryIntegrationTest](../src/test/java/lv/macibas/library/LibraryIntegrationTest.java) |

---

## Unit tests: Jest vs JUnit — gandrīz vienādi!

```js
// Jest
jest.mock("./bookApi");
test("throws when book already borrowed", () => {
  bookApi.findById.mockReturnValue({ available: false });
  expect(() => borrow(1)).toThrow("jau ir izsniegta");
});
```

```java
// JUnit + Mockito
@Mock BookRepository bookRepository;

@Test
void borrow_bookAlreadyBorrowed_throws() {
    when(bookRepository.findById(1L)).thenReturn(Optional.of(borrowedBook));   // = mockReturnValue
    assertThatThrownBy(() -> loanService.borrow(1L, "Jānis"))                   // = expect().toThrow()
        .hasMessageContaining("jau ir izsniegta");
}
```

| Jest | Mockito / AssertJ |
|---|---|
| `jest.fn()` / `jest.mock()` | `@Mock` |
| `.mockReturnValue(x)` | `when(...).thenReturn(x)` |
| `expect(x).toBe(y)` | `assertThat(x).isEqualTo(y)` |
| `expect(fn).toHaveBeenCalled()` | `verify(mock).method()` |
| `expect(() => ...).toThrow()` | `assertThatThrownBy(() -> ...)` |
| `beforeEach` | `@BeforeEach` |

**Struktūra — Given / When / Then:**
```java
// Given — sagatavo datus
// When  — izsauc to, ko testē
// Then  — pārbaudi rezultātu
```

---

## Kāpēc `Clock` ir bean? (labs intervijas stāsts)

Ja kodā raksti `LocalDate.now()` — testā nevar pārbaudīt "termiņš = šodiena + 14", jo "šodiena" katru dienu cita.

Risinājums: `Clock` ir atkarība → testā iedod **fiksētu** laiku:
```java
Clock clock = Clock.fixed(Instant.parse("2026-01-10T10:00:00Z"), ZoneOffset.UTC);
```

⚛️ Jest: `jest.useFakeTimers().setSystemTime(new Date("2026-01-10"))`.

---

## Palaist testus

```bash
mvn test
```
Vai IntelliJ: labais klikšķis uz `src/test/java` → **Run 'All Tests'**.

---

## 🎤 Intervijā

> "Rakstu daudz unit testu ar JUnit un Mockito servisu loģikai — tie ir ātri. Kontrolierus testēju ar @WebMvcTest un MockMvc, lai pārbaudītu statusa kodus un validāciju. Repozitorijus — ar @DataJpaTest. Un pāris integrācijas testus ar @SpringBootTest galvenajiem scenārijiem. Produkcijas datubāzes testiem labāk lietot Testcontainers ar īstu Postgres Docker konteinerā."

**Citi jēdzieni:**
- **TDD** — vispirms raksti testu (sarkans 🔴), tad kodu (zaļš 🟢), tad sakārto (refactor 🔵)
- **Mock vs Stub** — stub tikai atgriež vērtības; mock arī pārbauda, vai tika izsaukts
- **Testcontainers** — palaiž īstu DB Docker konteinerā testa laikā

## ✅ Pašpārbaude

1. Kāpēc unit testos lieto mock?
2. Ar ko atšķiras @WebMvcTest no @SpringBootTest?
3. Kā testēt kodu, kas lieto pašreizējo datumu?

➡️ Nākamā: [09-drosiba.md](09-drosiba.md)
