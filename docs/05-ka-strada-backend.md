# 5. nodarbība — Kā strādā backend (HTTP un REST)

> ⏱ ~1 stunda. Šī ir **vissvarīgākā** nodarbība, lai saprastu "lielo bildi".

---

## Lielā bilde: ko notiek, kad React dara `fetch()`?

Tu React kodā raksti:
```js
const res = await fetch("/api/books/1");
const book = await res.json();
```

Lūk, ko backend dara **otrā pusē**:

```
 ⚛️ React                         ☕ Spring Boot serveris                         🗄 Datubāze
┌──────────┐   HTTP GET     ┌────────────────────────────────────────────┐      ┌─────────┐
│  fetch() │ ─────────────► │ 1. Filter        (pārbauda, logo)          │      │         │
│          │ /api/books/1   │ 2. Controller    (saprot URL, nolasa JSON) │      │         │
│          │                │ 3. Service       (biznesa loģika)          │      │         │
│          │                │ 4. Repository    (SQL vaicājums) ──────────┼────► │ SELECT  │
│          │                │                                    ◄───────┼───── │ ...     │
│          │  ◄──────────── │ 5. Atgriež JSON  {"id":1,"title":"Dune"}   │      │         │
└──────────┘   200 OK       └────────────────────────────────────────────┘      └─────────┘
```

🧠 **Iegaumē 3 slāņus:**

| Slānis | Darbs | React analoģija | Mūsu kods |
|---|---|---|---|
| **Controller** | Pieņem HTTP, atgriež JSON. Nekādas loģikas! | Komponents, kas tikai zīmē UI | [BookController](../src/main/java/lv/macibas/library/web/BookController.java) |
| **Service** | Biznesa noteikumi ("vai drīkst aizņemties?") | Custom hook `useBorrowBook()` | [LoanService](../src/main/java/lv/macibas/library/service/LoanService.java) |
| **Repository** | Runā ar datubāzi | `api.ts` fails ar fetch funkcijām | [BookRepository](../src/main/java/lv/macibas/library/repository/BookRepository.java) |

🎤 > "Lietoju slāņu arhitektūru: kontrolieris apstrādā HTTP, serviss satur biznesa loģiku un transakcijas, repozitorijs strādā ar datubāzi. Tā katrs slānis ir vienkāršs un viegli testējams."

---

## HTTP metodes

Tu tās jau lieto ar `fetch(url, { method: "..." })`:

| Metode | Ko dara | Piemērs | Idempotenta?* |
|---|---|---|---|
| **GET** | Nolasīt | `GET /api/books` | ✅ |
| **POST** | Izveidot jaunu | `POST /api/books` | ❌ |
| **PUT** | Aizvietot **visu** | `PUT /api/books/1` | ✅ |
| **PATCH** | Mainīt **daļu** | `PATCH /api/books/1` `{"title":"X"}` | parasti ❌ |
| **DELETE** | Dzēst | `DELETE /api/books/1` | ✅ |

\* **Idempotents** = ja izsauc 1 reizi vai 10 reizes — rezultāts tas pats. `DELETE /books/1` 10 reizes → grāmata joprojām vienkārši dzēsta. `POST` 10 reizes → 10 jaunas grāmatas! (Tāpēc FE poga "Pasūtīt" jābloķē pēc klikšķa 😉)

---

## Statusa kodi

Tu tos redzi DevTools → Network. Tagad tu tos **sūtīsi**.

| Kods | Nozīme | Kad mūsu projektā |
|---|---|---|
| **200** OK | Viss labi | GET grāmata |
| **201** Created | Izveidots | POST jauna grāmata |
| **204** No Content | Labi, nav ko atgriezt | DELETE |
| **400** Bad Request | Klients atsūtīja sliktus datus | Tukšs nosaukums, slikts ISBN |
| **401** Unauthorized | Neesi ielogojies | Nav tokena |
| **403** Forbidden | Esi ielogojies, bet nav tiesību | Lietotājs mēģina dzēst (tikai admin drīkst) |
| **404** Not Found | Nav tāda resursa | `GET /api/books/999` |
| **409** Conflict | Konflikts ar esošo stāvokli | Grāmata jau aizņemta, ISBN jau eksistē |
| **500** Server Error | Serverī kļūda (mūsu vaina!) | Bug kodā |

🧠 **Viegli atcerēties:** `2xx` = 👍, `4xx` = **klienta** (tava FE) vaina, `5xx` = **servera** vaina.

---

## REST — kā saukt URL

**Noteikumi:**
- ✅ Lietvārdi daudzskaitlī: `/api/books`, `/api/authors`
- ❌ Ne darbības vārdi: ~~`/api/getBooks`~~, ~~`/api/createBook`~~ — darbību pasaka HTTP metode
- ✅ Hierarhija: `/api/books/5/loans` = grāmatas #5 izsniegumi
- ✅ Filtri un lapošana caur query: `/api/books?author=rainis&page=0&size=10`

**Mūsu API:**
```
GET    /api/books                  → visu grāmatu saraksts (ar lapošanu)
GET    /api/books/1                → viena grāmata
POST   /api/books                  → izveidot
PUT    /api/books/1                → atjaunot
DELETE /api/books/1                → dzēst
POST   /api/books/1/loans          → aizņemties grāmatu
POST   /api/loans/1/return         → atdot
GET    /api/loans?borrower=Jānis   → Jāņa aizņemtās grāmatas
```

🧪 **Izmēģini pats:** palaid aplikāciju un atver [requests.http](../requests.http) (IntelliJ ļauj spiest ▶ pie katra pieprasījuma).

---

## DTO — kāpēc nesūtām DB objektu tieši?

**DTO** = Data Transfer Object = **tā JSON forma, ko sūta pa tīklu**.

⚛️ **React:** tavs TypeScript `type BookResponse = { id: number; title: string }` — tas ir DTO no FE puses!

**Kāpēc ne tieši Entity (DB objektu)?**
1. 🔒 **Drošība:** ja pieņem Entity, ļaunprātīgs lietotājs var atsūtīt `"isAdmin": true` vai `"price": 0`
2. 🔌 **API nemainās**, ja maini DB tabulas
3. 🎯 Sūti **tikai vajadzīgo** (ne paroles hash!)

💻 [BookRequest.java](../src/main/java/lv/macibas/library/dto/BookRequest.java) (ko FE sūta) un [BookResponse.java](../src/main/java/lv/macibas/library/dto/BookResponse.java) (ko FE saņem)

---

## Validācija — FE validē, bet BE validē OBLIGĀTI

⚛️ Tu FE formā pārbaudi, vai e-pasts ir derīgs. **Bet** jebkurš var atvērt Postman/curl un sūtīt ko grib, apejot tavu FE.

👉 **Backend nekad neuzticas klientam.** Vienmēr validē serverī.

```java
public record BookRequest(
    @NotBlank(message = "Nosaukums ir obligāts") String title,
    @Pattern(regexp = "\\d{10}|\\d{13}") String isbn
) {}
```
Kontrolierī `@Valid` → ja dati slikti, automātiski **400** ar kļūdu sarakstu.

---

## Kļūdu apstrāde — viena vieta visām kļūdām

⚛️ **React:** `<ErrorBoundary>` noķer kļūdas no visiem komponentiem iekšā.
☕ **Spring:** `@RestControllerAdvice` noķer izņēmumus no visiem kontrolieriem un pārvērš tos smukā JSON:

```json
{ "status": 404, "title": "Not Found", "detail": "Grāmata ar id=99 nav atrasts" }
```

💻 [GlobalExceptionHandler.java](../src/main/java/lv/macibas/library/web/GlobalExceptionHandler.java)

---

## ✅ Pašpārbaude

1. Uzzīmē uz papīra, kas notiek no `fetch()` līdz datubāzei un atpakaļ.
2. Kāda atšķirība starp 401 un 403?
3. Kāda atšķirība starp PUT un PATCH? Kas ir idempotents?
4. Kāpēc backend jāvalidē, ja FE jau validē?
5. Kāpēc lieto DTO, nevis atgriež Entity?

➡️ Nākamā: [06-spring.md](06-spring.md)
