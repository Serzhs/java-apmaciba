# 📚 BE mācības — Java + Spring intervijām

Mācību projekts: **OOP + Java + Spring Boot backend**, skaidrots vienkārši, ar **React/FE salīdzinājumiem**.

---

## 🚀 Sāc šeit (pirmā reize)

### 1. Uzinstalē IntelliJ IDEA (bezmaksas)
https://www.jetbrains.com/idea/download/ → **Community Edition**

> Java 21 un Maven jau ir uzinstalēti šajā datorā (caur Homebrew).

### 2. Atver projektu
IntelliJ → **Open** → izvēlies mapi `Desktop/be-macibas` → pagaidi, kamēr lejupielādē bibliotēkas (apakšā redzams progress).

Ja IntelliJ prasa JDK: **File → Project Structure → SDK** → izvēlies `21` (vai **Add JDK** → `/opt/homebrew/opt/openjdk@21`).

### 3. Palaid kādu piemēru
Atver [src/main/java/lv/macibas/oop/Encapsulation.java](src/main/java/lv/macibas/oop/Encapsulation.java) → spied zaļo ▶ pie `main` → apakšā redzēsi rezultātu.

### 4. Palaid backend serveri
Atver [BeMacibasApplication.java](src/main/java/lv/macibas/BeMacibasApplication.java) → ▶
Vai terminālī:
```bash
./start.sh
```
Tad pārlūkā: http://localhost:8080/api/books 🎉

### 5. Palaid testus
```bash
./test.sh
```

---

## 🗺 Mācību plāns

> **Noteikums:** 1 nodarbība dienā. Nesteidzies. Labāk saprast mazāk, nekā iegaumēt daudz.
>
> **Katrai nodarbībai:**
> 1. Izlasi doc failu
> 2. Atver norādīto Java failu un **palaid** to
> 3. **Pamaini kaut ko** kodā un skaties, kas notiek (tā mācās labāk!)
> 4. Atbildi uz "Pašpārbaude" jautājumiem **skaļi**, it kā intervijā

| ✅ | Diena | Nodarbība | Kods |
|---|---|---|---|
| ☐ | 0 | [📖 FE ↔ BE vārdnīca](docs/00-FE-BE-vardnica.md) — **izlasi pirmo!** | — |
| ☐ | 1 | [OOP — 4 pīlāri](docs/01-oop.md) | [oop/](src/main/java/lv/macibas/oop/) |
| ☐ | 2 | [SOLID](docs/02-solid.md) | [solid/](src/main/java/lv/macibas/solid/) |
| ☐ | 3 | [Dizaina šabloni](docs/03-dizaina-sabloni.md) | [patterns/](src/main/java/lv/macibas/patterns/) |
| ☐ | 4–5 | [Java pamati](docs/04-java-pamati.md) | [javacore/](src/main/java/lv/macibas/javacore/) |
| ☐ | 6 | [Kā strādā backend (HTTP, REST)](docs/05-ka-strada-backend.md) ⭐ | [library/web/](src/main/java/lv/macibas/library/web/) + [requests.http](requests.http) |
| ☐ | 7–8 | [Spring](docs/06-spring.md) | [library/](src/main/java/lv/macibas/library/), [springbasics/](src/main/java/lv/macibas/springbasics/) |
| ☐ | 9–10 | [Datubāzes, JPA, transakcijas](docs/07-datubazes.md) | [domain/](src/main/java/lv/macibas/library/domain/), [repository/](src/main/java/lv/macibas/library/repository/) |
| ☐ | 11 | [Testi](docs/08-testi.md) | [src/test/](src/test/java/lv/macibas/) |
| ☐ | 12 | [Drošība](docs/09-drosiba.md) | — |
| ☐ | 13 | [Arhitektūra](docs/10-arhitektura.md) | — |
| ☐ | 14+ | [🎤 Intervijas jautājumi — atkārto katru dienu!](docs/11-intervijas-jautajumi.md) | — |

---

## 🏗 Kas ir projektā

```
be-macibas/
├── docs/                         ← 📖 SKAIDROJUMI (sāc šeit)
├── requests.http                 ← API izmēģināšana (kā Postman)
├── start.sh / test.sh            ← palaist serveri / testus
├── pom.xml                       ← bibliotēkas (kā package.json)
└── src/
    ├── main/java/lv/macibas/
    │   ├── oop/                  ← OOP piemēri (katram failam main())
    │   ├── solid/                ← SOLID principi
    │   ├── patterns/             ← dizaina šabloni
    │   ├── javacore/             ← kolekcijas, streams, izņēmumi, pavedieni...
    │   ├── springbasics/         ← kā Spring strādā "zem pārsega"
    │   └── library/              ← ⭐ ĪSTS backend: bibliotēkas REST API
    │       ├── web/              ←   Controllers (HTTP)        ⚛️ API routes
    │       ├── service/          ←   Biznesa loģika           ⚛️ custom hooks
    │       ├── repository/       ←   Datubāze                 ⚛️ api.ts
    │       ├── domain/           ←   Entities (DB tabulas)
    │       ├── dto/              ←   JSON formas               ⚛️ TS types
    │       ├── exception/        ←   Kļūdas
    │       ├── notification/     ←   E-pasts/SMS (Observer + Strategy)
    │       └── config/           ←   Konfigurācija, filtri, AOP
    ├── main/resources/
    │   └── application.yml       ← iestatījumi (kā .env)
    └── test/                     ← testi (kā Jest)
```

### Bibliotēkas API — ko tas dara?
Bibliotēka: ir **autori**, **grāmatas** un **izsniegumi**. Lasītājs var aizņemties grāmatu (ja tā brīva un viņam nav jau 3 grāmatas), un atdot to. Pēc aizņemšanās tiek "nosūtīts" e-pasts un SMS (konsolē).

Noderīgas adreses, kad serveris palaists:
- http://localhost:8080/api/books — grāmatas
- http://localhost:8080/h2-console — datubāze (JDBC URL: `jdbc:h2:mem:library`, user: `sa`, bez paroles)
- http://localhost:8080/actuator/health — servera veselība

---

## 💡 Ko darīt pēc tam (kad viss saprasts)

Mēģini **pats** pievienot (tas ir labākais treniņš un labs stāsts intervijai):
1. ☐ `GET /api/books/available` — tikai pieejamās grāmatas (repozitorijā jau ir `findAllAvailableWithAuthor()`)
2. ☐ Lauku `genre` grāmatai + filtrs `?genre=...`
3. ☐ Testu jaunajam endpointam
4. ☐ Nomainīt H2 uz Postgres ar Docker
5. ☐ Pievienot Spring Security ar JWT
6. ☐ Uztaisīt mazu React FE, kas rāda grāmatas no šī API (tu to proti! — un tu satiksies ar CORS 😉)
