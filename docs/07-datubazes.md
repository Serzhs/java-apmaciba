# 7. nodarbība — Datubāzes, SQL, JPA, transakcijas

> ⏱ ~1,5 stundas. FE šito parasti neredz, tāpēc lasi lēnām.

---

## SQL pamati — tabulas ir kā Excel lapas

```
authors                          books
┌────┬──────────────────┐        ┌────┬────────────────┬───────────┐
│ id │ name             │        │ id │ title          │ author_id │ ← "foreign key" (norāde uz authors.id)
├────┼──────────────────┤        ├────┼────────────────┼───────────┤
│ 1  │ Rūdolfs Blaumanis│ ◄──────│ 1  │ Zvejnieka dēls │ 1         │
│ 2  │ Rainis           │ ◄──┐   │ 2  │ Nāves ēnā      │ 1         │
└────┴──────────────────┘    └───│ 3  │ Uguns un nakts │ 2         │
                                 └────┴────────────────┴───────────┘
```

**4 komandas, kas jāzina:**
```sql
SELECT title FROM books WHERE author_id = 1;                 -- nolasīt
INSERT INTO books (title, author_id) VALUES ('X', 1);         -- pievienot
UPDATE books SET title = 'Y' WHERE id = 1;                    -- mainīt
DELETE FROM books WHERE id = 1;                               -- dzēst
```

**JOIN** = savienot tabulas:
```sql
SELECT b.title, a.name
FROM books b
JOIN authors a ON a.id = b.author_id;
```

🧪 **Pamēģini:** palaid aplikāciju → atver http://localhost:8080/h2-console → JDBC URL: `jdbc:h2:mem:library`, user `sa`, parole tukša → raksti SQL!

**Biežākie jautājumi:**
- **INNER JOIN vs LEFT JOIN?** INNER — tikai tie, kam ir pāris abās tabulās. LEFT — visi no kreisās, pat ja labajā nav pāra (tad `null`).
- **WHERE vs HAVING?** WHERE filtrē rindas **pirms** grupēšanas, HAVING — **pēc** `GROUP BY`.
- **Primary key?** Unikāls rindas ID. **Foreign key?** Norāde uz citas tabulas primary key.

---

## Indekss — kā grāmatas satura rādītājs

**Bez indeksa:** lai atrastu grāmatu "Dune", DB pārskata **katru rindu** (lēni, ja miljons rindu).
**Ar indeksu:** DB uzreiz zina, kur meklēt (kā vārdnīca pēc alfabēta).

⚠️ Bet: indekss **paātrina lasīšanu**, **palēnina rakstīšanu** (jāatjauno indekss) un aizņem vietu.

🎤 > "Indeksu lieku kolonnām, pēc kurām bieži meklē vai JOINo — piemēram, foreign key vai e-pasts. Bet ne visām, jo tas palēnina INSERT/UPDATE."

---

## SQL vs NoSQL

| SQL (Postgres, MySQL) | NoSQL (MongoDB, Redis) |
|---|---|
| Tabulas ar stingru struktūru | Dokumenti (JSON), elastīga struktūra |
| Spēcīgas saites (JOIN), transakcijas | Viegli mērogot horizontāli |
| Nauda, pasūtījumi, lietotāji | Logi, keši, ļoti mainīgi dati |

⚛️ MongoDB dokuments izskatās tieši kā JS objekts.

---

## JPA / Hibernate — Java objekti ↔ DB tabulas

**ORM** = Object-Relational Mapping. Tu strādā ar Java objektiem, Hibernate pats raksta SQL.

⚛️ Līdzīgi kā **Prisma** vai **Drizzle** TS pasaulē!

```java
@Entity                    // šī klase = tabula "books"
public class Book {
    @Id @GeneratedValue
    private Long id;       // primary key
    private String title;  // kolonna
    @ManyToOne
    private Author author; // foreign key uz authors
}
```

**Spring Data repozitorijs** — tu raksti tikai interfeisu, SQL ģenerē automātiski **no metodes nosaukuma**:
```java
interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByIsbn(String isbn);                  // → WHERE isbn = ?
    List<Book> findByAuthorNameContainingIgnoreCase(String n); // → WHERE lower(name) LIKE %n%
}
```
Bez jebkāda implementācijas koda! ✨

💻 [Book.java](../src/main/java/lv/macibas/library/domain/Book.java), [BookRepository.java](../src/main/java/lv/macibas/library/repository/BookRepository.java)

---

## ⭐ N+1 problēma (ĻOTI bieži jautā!)

**Situācija:** gribam parādīt 10 grāmatas ar autoru vārdiem.

```
1 vaicājums:   SELECT * FROM books LIMIT 10;             ← dabū 10 grāmatas
+10 vaicājumi: SELECT * FROM authors WHERE id = 1;       ← katrai grāmatai atsevišķi!
               SELECT * FROM authors WHERE id = 2;
               ...
= 11 vaicājumi 🐌
```

⚛️ **React analoģija:** tu ielādē sarakstu ar 10 lietotājiem, un tad **katrs** `<UserCard>` komponents dara savu `fetch("/api/users/" + id + "/avatar")`. 1 + 10 pieprasījumi. Tā pati problēma! "Request waterfall".

**Risinājums:** ielādēt visu **vienā** vaicājumā ar JOIN:
```java
@EntityGraph(attributePaths = "author")   // vai: @Query("... JOIN FETCH b.author")
Page<Book> findAll(Pageable pageable);
```

🎤 > "N+1 ir, kad ielādē N ierakstus ar vienu vaicājumu un tad katram saistītajam objektam izpilda vēl vienu. Risinu ar JOIN FETCH vai @EntityGraph, lai visu dabūtu vienā vaicājumā. Pamanu to, ieslēdzot SQL logošanu."

🧪 **Redzi pats:** aplikācijas konsolē ir redzami SQL vaicājumi (`org.hibernate.SQL: debug` ieslēgts).

---

## LAZY vs EAGER

- **LAZY** = ielādē saistītos datus **tikai tad, kad tiem piekļūst** (kā `React.lazy()` — ielādē komponentu, kad vajag)
- **EAGER** = ielādē **uzreiz vienmēr**

👉 Labā prakse: **vienmēr LAZY**, un kad vajag — ielādē ar JOIN FETCH.

---

## ⭐ Transakcijas — "viss vai nekas"

**Piemērs:** pārskaitījums no Jāņa uz Annu.
1. Noņemt 100€ Jānim
2. Pielikt 100€ Annai

Ja pēc 1. soļa serveris nokrīt → 100€ **pazuda**! 😱

**Transakcija** garantē: **vai nu abi soļi notiek, vai neviens**. Ja kļūda → **rollback** (atpakaļ kā bija).

⚛️ **React analoģija:** "optimistic update" ar rollback — tu uzreiz parādi izmaiņu UI, bet ja serveris atgriež kļūdu, atgriez veco state. Transakcija to dara DB līmenī.

```java
@Transactional
public LoanResponse borrow(Long bookId, String borrower) {
    book.markBorrowed();          // 1. grāmata vairs nav pieejama
    loanRepository.save(loan);    // 2. izveido izsniegumu
    // ja te notiek kļūda → abas izmaiņas tiek atceltas
}
```

**ACID** (jautā!):
- **A**tomicity — viss vai nekas
- **C**onsistency — DB vienmēr derīgā stāvoklī
- **I**solation — vienlaicīgas transakcijas netraucē viena otrai
- **D**urability — kad saglabāts, tad saglabāts (arī ja serveris nokrīt)

⚠️ **@Transactional slazdi** (jautā kā āķīgu jautājumu):
1. Nestrādā, ja metodi izsauc **no tās pašas klases** (`this.metode()`) — jo Spring lieto proxy
2. Nestrādā uz **private** metodēm
3. Rollback tikai uz **RuntimeException** (ne checked)

💻 [LoanService.java](../src/main/java/lv/macibas/library/service/LoanService.java)

---

## Optimistic locking — ko darīt, ja 2 cilvēki vienlaicīgi?

Jānis un Anna **tajā pašā sekundē** spiež "Aizņemties" vienai grāmatai.

**Risinājums:** `@Version` lauks. Katrai rindai ir versijas numurs. Kad saglabā, DB pārbauda: "vai versija joprojām tā pati?" Ja kāds paspēja pirmais → otrajam kļūda **409 Conflict**.

⚛️ Kā Google Docs: "Šo dokumentu kāds cits izmainīja, pārlādē lapu".

💻 `@Version` laukā [Book.java](../src/main/java/lv/macibas/library/domain/Book.java)

---

## Migrācijas (Flyway / Liquibase)

Mūsu projektā Hibernate pats taisa tabulas (`ddl-auto: create-drop`) — **tikai mācībām!**
Produkcijā lieto **migrācijas**: SQL faili ar versijām (`V1__create_books.sql`, `V2__add_isbn.sql`), kas glabājas git.

⚛️ Kā **Prisma migrate**.

---

## ✅ Pašpārbaude

1. Izskaidro N+1 problēmu ar React piemēru un pasaki risinājumu.
2. Ko nozīmē ACID? Kāpēc transakcijas vajadzīgas?
3. Kāpēc `@Transactional` nestrādā `this.metode()` izsaukumā?
4. Kad likt indeksu? Kāds ir tā mīnuss?
5. INNER JOIN vs LEFT JOIN?

➡️ Nākamā: [08-testi.md](08-testi.md)
