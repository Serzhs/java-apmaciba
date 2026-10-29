# 12. nodarbība — Prakse: raksti kodu pats

> Teorija ir izieta! 🎉 Tagad svarīgākais: **uzrakstīt kaut ko pašam**. Tieši šos uzdevumus vari pēc tam stāstīt intervijā: *"Es pievienoju..."*

## Kā strādāt ar uzdevumiem

1. **Palaid serveri** (`./start.sh`) un pārliecinies, ka http://localhost:8080/api/books strādā.
2. **Izlasi uzdevumu** un padomā 5 minūtes, **kurus failus** būs jāmaina. Uzraksti to uz papīra.
3. **Raksti kodu mazos soļos.** Pēc katra soļa — palaid. Ja salūzt — izlasi kļūdu no augšas.
4. **Iestrēgsti ilgāk par 20 min?** Atver padomu (zemāk katram uzdevumam) vai pajautā Claude.
5. **Beigās:** `./test.sh` — visiem testiem jābūt zaļiem.

⚛️ Tas ir tāpat kā FE: vispirms izdomā, kuri komponenti mainās, tad maini pa vienam un skaties pārlūkā.

## Uzdevums 1: Tikai pieejamās grāmatas

**Mērķis:** `GET /api/books/available` → atgriež tikai grāmatas, kuras var aizņemties.

**Kurus failus mainīt (seko pieprasījuma ceļam — no augšas uz leju):**
- [BookController.java](../src/main/java/lv/macibas/library/web/BookController.java) — jauna `@GetMapping("/available")` metode
- [BookService.java](../src/main/java/lv/macibas/library/service/BookService.java) — jauna metode, kas izsauc repozitoriju
- [BookRepository.java](../src/main/java/lv/macibas/library/repository/BookRepository.java) — metode `findAllAvailableWithAuthor()` **jau ir!**

**Padoms:** servisā `bookRepository.findAllAvailableWithAuthor().stream().map(BookResponse::from).toList()`

**Pārbaudi:** aizņemies grāmatu #1 (requests.http 11. pieprasījums) → tā vairs nav `/available` sarakstā.

## Uzdevums 2: Žanrs grāmatai

**Mērķis:** grāmatai ir `genre` lauks (piem. "Romāns", "Programmēšana"), un var filtrēt `GET /api/books?genre=Romāns`.

**Kurus failus mainīt:**
- [Book.java](../src/main/java/lv/macibas/library/domain/Book.java) — jauns lauks `private String genre;` + getter + konstruktorā
- [BookRequest.java](../src/main/java/lv/macibas/library/dto/BookRequest.java) — jauns lauks (ar `@Size(max = 50)`)
- [BookResponse.java](../src/main/java/lv/macibas/library/dto/BookResponse.java) — jauns lauks + `from()` metodē
- [BookRepository.java](../src/main/java/lv/macibas/library/repository/BookRepository.java) — `Page<Book> findByGenre(String genre, Pageable pageable);`
- [DataInitializer.java](../src/main/java/lv/macibas/library/config/DataInitializer.java) — pieliec žanrus testa datiem

**Padoms:** kompilators pats pateiks, kur vēl jāpieliek jaunais parametrs (sarkanās vietas VS Code). Tā ir Java priekšrocība — kā TypeScript!

## Uzdevums 3: Testi taviem uzdevumiem

**Mērķis:** uzraksti testus 1. un 2. uzdevumam.

**Paraugi, ko kopēt un pielāgot:**
- [BookControllerTest.java](../src/test/java/lv/macibas/library/BookControllerTest.java) — kontroliera tests ar MockMvc
- [BookRepositoryTest.java](../src/test/java/lv/macibas/library/BookRepositoryTest.java) — repozitorija tests

**Padoms:** nokopē esošu `@Test` metodi, pārsauc, nomaini URL un gaidīto rezultātu. Given / When / Then.

## Uzdevums 4: React frontends šim API

**Mērķis:** mazs React (Vite) projekts, kas parāda grāmatu sarakstu no `http://localhost:8080/api/books` un ļauj aizņemties grāmatu ar pogu.

**Tu satiksi CORS kļūdu!** 😄 Tas ir labi — tagad tu zini, ka to labo backend:
- [BookController.java](../src/main/java/lv/macibas/library/web/BookController.java) — pievieno `@CrossOrigin(origins = "http://localhost:5173")` virs klases

**Bonuss:** parādi 409 kļūdas `detail` tekstu lietotājam, kad grāmata jau aizņemta. Atceries — tas ir ProblemDetail formāts no [GlobalExceptionHandler.java](../src/main/java/lv/macibas/library/web/GlobalExceptionHandler.java).

## Uzdevums 5: Izmēģinājuma intervija

**Mērķis:** simulēt īstu interviju.

1. Pasaki Claude: *"Uztaisi man 30 min Java backend juniora interviju. Uzdod jautājumus pa vienam un novērtē atbildes."*
2. Atbildi **rakstot tā, kā runātu** — īsi, savos vārdos.
3. Pēc tam: pieraksti 3 jautājumus, kuros biji nedrošs → atkārto tās nodarbības.
4. Sagatavo **stāstu par šo projektu** (2 minūtes): ko tas dara, kāda arhitektūra, kas bija grūti, ko tu pievienoji pats.

> 💪 Ja esi ticis līdz šejienei — tu zini vairāk nekā lielākā daļa juniora kandidātu. Veiksmi intervijās!
