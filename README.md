# Java apmācība

Java backend mācību kurss latviešu valodā, lai sagatavotos **Java / Spring Boot junior izstrādātāja intervijām**. Paredzēts tiem, kas jau zina frontendu (React): katra tēma ir skaidrota vienkārši un salīdzināta ar to, ko tu jau zini no React.

Kursā ir trīs daļas:

1. **Mācību aplikācija** (React): 21 diena pa 1–2 stundām. Kreisajā pusē skaidrojums, labajā kods ar tabiem kā IDE. Ir arī taimeris, progress un intervijas jautājumu kartītes.
2. **Teorija** (`docs/`): 13 nodarbības no OOP līdz sistēmu dizainam. Katrā ir gatavas atbildes intervijai un pašpārbaudes jautājumi.
3. **Kods** (`src/`): OOP, SOLID un dizaina šablonu piemēri, ko var palaist, un īsts **Spring Boot REST API** (bibliotēka) ar testiem.

---

## Saturs

- [Ko vajag uzinstalēt](#ko-vajag-uzinstalēt)
- [Ātrais starts](#ātrais-starts)
- [Mācību aplikācija](#mācību-aplikācija)
- [Backend serveris (Spring Boot)](#backend-serveris-spring-boot)
- [Testi](#testi)
- [Mācību plāns](#mācību-plāns)
- [Projekta struktūra](#projekta-struktūra)
- [Bibliotēkas API](#bibliotēkas-api)
- [Biežākās problēmas](#biežākās-problēmas)

---

## Ko vajag uzinstalēt

| Rīks | Versija | Kam vajag |
|---|---|---|
| **Java (JDK)** | 21 vai jaunāka | Java kods un Spring Boot serveris |
| **Node.js** | 20 vai jaunāka | Mācību aplikācija |
| **VS Code** (ieteicams) | jebkura | Koda rediģēšana un palaišana |

Maven nav jāinstalē. Projektā ir **Maven Wrapper** (`./mvnw`), kas pats lejupielādē pareizo Maven versiju.

**macOS (ar [Homebrew](https://brew.sh)):**
```bash
brew install openjdk@21 node
```

**Windows:** lejupielādē [Temurin JDK 21](https://adoptium.net/temurin/releases/?version=21) un [Node.js LTS](https://nodejs.org/).

**Linux (Ubuntu/Debian):**
```bash
sudo apt install openjdk-21-jdk nodejs npm
```

Pārbaudi, vai viss ir uzinstalēts:
```bash
java -version
node -v
```

### VS Code paplašinājumi

Kreisajā pusē atver **Extensions** un uzinstalē:
- **Extension Pack for Java** (Microsoft): lai varētu palaist Java kodu ar pogu **Run**
- **REST Client**: lai sūtītu API pieprasījumus no faila `requests.http`

---

## Ātrais starts

```bash
git clone https://github.com/Serzhs/java-apmaciba.git
cd java-apmaciba
```

**1. Atver mācību aplikāciju:**
```bash
cd frontend
npm install
npm run dev -- --open
```
Pārlūkā atvērsies http://localhost:5173. Sāc ar **1. dienu**.

**2. (Vēlāk, no 9. dienas) Palaid backend serveri** jaunā termināļa logā:
```bash
./start.sh
```
Tad atver http://localhost:8080/api/books

---

## Mācību aplikācija

**macOS:** Finderī dubultklikšķini uz **`Java apmācība.command`**. Tas pats uzinstalē, kas vajadzīgs, un atver aplikāciju pārlūkā.

**Jebkurā sistēmā:**
```bash
cd frontend
npm install        # tikai pirmo reizi
npm run dev -- --open
```

### Ko tajā var darīt

- **Plāns:** 21 diena, katra diena ir viena tēma (~1–2 h). Katram solim ir laika novērtējums.
- **Teksts + kods blakus:** ja kods ir vairākos failos, katrs atveras savā tabā kā IDE. Starp tekstu un kodu ir josla, ko var vilkt, lai mainītu platumu.
- **Taimeris:** skaita, cik šodien mācījies (mērķis 1–2 h), un cik dienas pēc kārtas.
- **Dienas čeklists:** iesildīšanās, koda palaišana un pašpārbaude.
- **Kartītes:** intervijas jautājumu atbildes ir aizmiglotas, līdz uzklikšķini. Vispirms atbildi pats.
- **Faili:** visu projektu var pārlūkot kā VS Code.
- **Īsinājumtaustiņi:** `←` `→` pārslēdz soļus, `⌘B` / `Ctrl+B` paslēpj sānjoslu.

### Kur glabājas progress

Progress tiek saglabāts failā **`progress.json`** projekta saknē. Tāpēc tas nepazūd, ja iztīri pārlūku vai atver citā pārlūkā. Šis fails ir `.gitignore` sarakstā, jo katram ir savs progress.

Katru dienu tiek saglabāta rezerves kopija mapē `.progress-backups/GGGG-MM-DD.json` (pēdējās 30 dienas). Ja kaut ko sabojā, nokopē vajadzīgās dienas failu pāri `progress.json` un pārlādē lapu.

Aplikācija nolasa `docs/` un `src/` failus tieši. Ja maini kodu, izmaiņa uzreiz parādās arī aplikācijā.

---

## Backend serveris (Spring Boot)

```bash
./start.sh                    # macOS / Linux
mvnw.cmd spring-boot:run      # Windows
```

Kad konsolē parādās `Ielādētas 6 grāmatas`, serveris strādā.

| Adrese | Kas tur ir |
|---|---|
| http://localhost:8080/api/books | Grāmatu saraksts (JSON) |
| http://localhost:8080/h2-console | Datubāze pārlūkā (JDBC URL: `jdbc:h2:mem:library`, lietotājs `sa`, bez paroles) |
| http://localhost:8080/actuator/health | Servera veselības pārbaude |

Apturēt serveri: `Ctrl+C`.

**Atsevišķu piemēru palaišana:** VS Code atver jebkuru failu no `src/main/java/lv/macibas/oop/` (vai `solid/`, `patterns/`, `javacore/`) un spied **Run** virs `main` metodes.

---

## Testi

```bash
./test.sh                     # macOS / Linux
mvnw.cmd test                 # Windows
```

Projektā ir 15 testi, un tie parāda visus testu veidus:

| Tests | Veids | Ko pārbauda |
|---|---|---|
| `LoanServiceTest` | Unit (JUnit + Mockito) | Biznesa loģika bez datubāzes |
| `BookControllerTest` | `@WebMvcTest` | URL, HTTP statusi, validācija, kļūdu JSON |
| `BookRepositoryTest` | `@DataJpaTest` | Datubāzes vaicājumi |
| `LibraryIntegrationTest` | `@SpringBootTest` | Visa aplikācija kopā |
| `OopExamplesRunTest` | Unit | Visi mācību piemēri palaižas bez kļūdām |

---

## Mācību plāns

| Dienas | Tēma | Nodarbība |
|---|---|---|
| 1 | FE ↔ BE vārdnīca | [00-FE-BE-vardnica.md](docs/00-FE-BE-vardnica.md) |
| 2–3 | OOP: 4 pīlāri, equals/hashCode, immutability | [01-oop.md](docs/01-oop.md) |
| 4 | SOLID | [02-solid.md](docs/02-solid.md) |
| 5–6 | Dizaina šabloni | [03-dizaina-sabloni.md](docs/03-dizaina-sabloni.md) |
| 7–8 | Java pamati: kolekcijas, streams, izņēmumi, pavedieni | [04-java-pamati.md](docs/04-java-pamati.md) |
| 9 | Kā strādā backend: HTTP, REST | [05-ka-strada-backend.md](docs/05-ka-strada-backend.md) |
| 10 | Spring: IoC, Dependency Injection | [06-spring.md](docs/06-spring.md) |
| 11–12 | Datubāzes, JPA, N+1, transakcijas | [07-datubazes.md](docs/07-datubazes.md) |
| 13 | Testi | [08-testi.md](docs/08-testi.md) |
| 14 | Drošība: JWT, CORS, SQL injection | [09-drosiba.md](docs/09-drosiba.md) |
| 15 | Arhitektūra un sistēmu dizains | [10-arhitektura.md](docs/10-arhitektura.md) |
| 16–17 | 48 intervijas jautājumi ar atbildēm | [11-intervijas-jautajumi.md](docs/11-intervijas-jautajumi.md) |
| 18–21 | Prakse: raksti kodu pats + izmēģinājuma intervija | [12-prakse.md](docs/12-prakse.md) |

---

## Projekta struktūra

```
java-apmaciba/
├── docs/                          Teorija (13 nodarbības, Markdown)
├── frontend/                      Mācību aplikācija (React + Vite)
│   ├── src/                       Aplikācijas kods
│   └── progress-plugin.ts         Saglabā progresu failā progress.json
├── src/main/java/lv/macibas/
│   ├── oop/                       OOP piemēri (katram failam ir main())
│   ├── solid/                     SOLID principi
│   ├── patterns/                  Dizaina šabloni
│   ├── javacore/                  Kolekcijas, streams, izņēmumi, pavedieni, generics
│   ├── springbasics/              Kā Spring IoC konteiners strādā "zem pārsega"
│   └── library/                   Spring Boot REST API
│       ├── web/                   Kontrolieri un kļūdu apstrāde
│       ├── service/               Biznesa loģika un transakcijas
│       ├── repository/            Spring Data JPA
│       ├── domain/                Entītijas (DB tabulas)
│       ├── dto/                   Pieprasījumu un atbilžu formas + validācija
│       ├── notification/          Notikumi (Observer + Strategy)
│       └── config/                Konfigurācija, filtrs, AOP
├── src/main/resources/
│   └── application.yml            Servera iestatījumi
├── src/test/                      Testi
├── requests.http                  API pieprasījumi izmēģināšanai
├── start.sh / test.sh             Palaist serveri / testus
└── pom.xml                        Java atkarības (kā package.json)
```

**Tehnoloģijas:** Java 21, Spring Boot 3.5, Spring Data JPA, Hibernate, H2, Bean Validation, JUnit 5, Mockito, AssertJ. Frontendam React 19, Vite, highlight.js, react-markdown.

---

## Bibliotēkas API

Mācību projekts: ir **autori**, **grāmatas** un **izsniegumi**. Lasītājs var aizņemties grāmatu, ja tā ir brīva un viņam nav jau 3 grāmatas. Pēc aizņemšanās tiek "nosūtīts" e-pasts un SMS (tos redz konsolē).

| Metode | URL | Apraksts | Atbildes |
|---|---|---|---|
| `GET` | `/api/books?author=&page=&size=&sort=` | Grāmatu saraksts ar lapošanu un filtru | 200 |
| `GET` | `/api/books/{id}` | Viena grāmata | 200, 404 |
| `POST` | `/api/books` | Izveidot grāmatu | 201, 400, 409 |
| `PUT` | `/api/books/{id}` | Atjaunot grāmatu | 200, 400, 404, 409 |
| `DELETE` | `/api/books/{id}` | Dzēst grāmatu | 204, 404, 409 |
| `POST` | `/api/books/{id}/loans` | Aizņemties grāmatu | 201, 404, 409 |
| `POST` | `/api/loans/{id}/return` | Atdot grāmatu | 200, 404, 409 |
| `GET` | `/api/loans?borrower=` | Lasītāja aizņemtās grāmatas | 200 |
| `GET` | `/api/authors` | Autoru saraksts | 200 |
| `POST` | `/api/authors` | Izveidot autoru | 201, 400 |

Kļūdas tiek atgrieztas [RFC 9457 Problem Details](https://www.rfc-editor.org/rfc/rfc9457) formātā:
```json
{ "status": 409, "title": "Conflict", "detail": "Grāmata 'Zvejnieka dēls' jau ir izsniegta" }
```

Visus pieprasījumus var izmēģināt failā [requests.http](requests.http).

---

## Biežākās problēmas

**`Unable to locate a Java Runtime` vai `JAVA_HOME is not set`**
Java nav uzinstalēta vai nav atrasta. Uzinstalē JDK 21 (skat. augstāk). macOS ar Homebrew `start.sh` to atrod pats. Citādi iestati ceļu:
```bash
export JAVA_HOME=/ceļš/uz/jdk-21
```

**`Port 8080 was already in use`**
Serveris jau darbojas citā termināļa logā. Aptur to ar `Ctrl+C` vai aizver šo logu.

**Mācību aplikācija atveras uz cita porta (5174, 5175...)**
Ports 5173 ir aizņemts, jo aplikācija jau ir atvērta citā logā. Progress nepazūd, jo tas glabājas failā.

**Sānjoslā rakstīts "Saglabāts tikai pārlūkā"**
Aplikācija atvērta bez Vite servera (piem., no `dist/` mapes). Palaid to ar `npm run dev`.

**VS Code nerāda pogu "Run" virs `main`**
Pagaidi 1–2 minūtes, kamēr Java paplašinājums ielādē projektu (progress redzams apakšējā joslā). Pārliecinies, ka atvēri visu projekta mapi (**File → Open Folder**), nevis vienu failu.

---

## Licence

[MIT](LICENSE): drīksti brīvi lietot, kopēt un mainīt.
