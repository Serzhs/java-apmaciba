# 6. nodarbība — Spring un Spring Boot

> ⏱ ~1,5 stundas.

---

## Kas ir Spring un Spring Boot?

- **Spring** = Java framework backendam. Kā **React** frontendam — dod struktūru un gatavas lietas.
- **Spring Boot** = Spring, kas **pats sevi konfigurē**. Kā **Next.js / Vite** — `npm create vite` un tas vienkārši strādā, nav jākonfigurē webpack.

🎤 > "Spring Boot ir Spring ar auto-konfigurāciju un iebūvētu serveri (Tomcat). Tas pēc classpath bibliotēkām pats saprot, ko konfigurēt — piemēram, ja redz H2 vai Postgres draiveri, izveido datubāzes savienojumu."

---

## ⭐ IoC un Dependency Injection — SVARĪGĀKAIS jēdziens

### Problēma
```java
class LoanService {
    private BookRepository repo = new BookRepository();   // ❌ pats izveido
}
```
- Nevar testēt ar viltus (mock) repozitoriju
- Ja `BookRepository` vajag savas atkarības — tās arī jāveido pašam... un tā bezgalīgi

### Risinājums: Dependency Injection (DI)
```java
@Service
class LoanService {
    private final BookRepository repo;

    LoanService(BookRepository repo) {     // ✅ "Man vajag BookRepository"
        this.repo = repo;                   //    Spring to IEDOD
    }
}
```

⚛️ **React analoģija — Context:**
```jsx
// Tu neveido API klientu komponenta iekšā — tu to saņem no Provider
const api = useContext(ApiContext);
```
Spring ir **viens liels Context Provider** visai aplikācijai. Tu pasaki, ko tev vajag (konstruktorā), un Spring iedod.

- **IoC (Inversion of Control)** = "ne TU veido objektus, bet Spring". Kontrole ir "apgriezta".
- **DI (Dependency Injection)** = veids, KĀ Spring iedod atkarības (caur konstruktoru).
- **Bean** = jebkurš objekts, ko pārvalda Spring.

🎤 > "IoC nozīmē, ka objektu izveidi un savienošanu pārvalda Spring konteiners, nevis mans kods. DI ir veids, kā to dara — es konstruktorā deklarēju atkarības, un Spring tās injicē. Lietoju constructor injection, jo lauki var būt final un klasi viegli testēt bez Spring."

💻 Redzēt "zem pārsega": [IocAndBeansDemo.java](../src/main/java/lv/macibas/springbasics/IocAndBeansDemo.java) — palaid `main()`.

---

## Anotācijas — "uzlīmes", ko Spring lasa

| Anotācija | Ko nozīmē | Analoģija |
|---|---|---|
| `@Component` | "Spring, pārvaldi šo klasi" | Reģistrē komponentu |
| `@Service` | Tas pats, bet biznesa loģikai | Custom hook |
| `@Repository` | Tas pats, bet DB darbam | `api.ts` |
| `@RestController` | HTTP kontrolieris, atgriež JSON | API route |
| `@Configuration` + `@Bean` | Manuāli izveido beanu (no svešas bibliotēkas) | `createContext(new Something())` |
| `@GetMapping("/x")` | Šī metode apstrādā `GET /x` | `export function GET()` Next.js |
| `@PathVariable` | Ņem no URL: `/books/{id}` | `useParams()` |
| `@RequestParam` | Ņem no query: `?author=x` | `useSearchParams()` |
| `@RequestBody` | Ņem JSON no body | `await req.json()` |
| `@Valid` | Validē ienākošos datus | Zod `.parse()` |
| `@Transactional` | Viss vai nekas DB | (skat. 7. nodarbību) |

🎤 **"Kāda atšķirība starp @Component, @Service, @Repository?"**
> "Tehniski visi trīs reģistrē beanu. Atšķirība ir nozīmē un lasāmībā. `@Repository` papildus pārvērš DB izņēmumus Spring `DataAccessException`."

---

## Bean scopes — cik eksemplāru?

| Scope | Nozīme |
|---|---|
| **singleton** (noklusējums) | **Viens** objekts visai aplikācijai |
| **prototype** | **Jauns** katru reizi, kad prasa |
| **request** | Viens uz katru HTTP pieprasījumu |
| **session** | Viens uz lietotāja sesiju |

⚠️ Singleton = **visi lietotāji dala vienu objektu**. Tāpēc:
```java
@Service
class CartService {
    private List<Item> items;   // ❌ BĪSTAMI! Visi lietotāji redzēs vienu grozu!
}
```

---

## Konfigurācija — `application.yml`

⚛️ Tas ir tavs **`.env` fails**.

```yaml
library:
  max-active-loans-per-borrower: 3
  loan-period-days: 14
```

Java kodā to nolasa ar:
```java
@ConfigurationProperties(prefix = "library")
public record LibraryProperties(int maxActiveLoansPerBorrower, int loanPeriodDays) {}
```

**Profili** = `.env.development` vs `.env.production`:
- `application-dev.yml`, `application-prod.yml`
- palaiž ar `--spring.profiles.active=prod`

⚠️ **Paroles NEKAD nav kodā/git!** Tās nāk no vides mainīgajiem (environment variables) — tāpat kā FE `.env` nav git.

💻 [application.yml](../src/main/resources/application.yml), [LibraryProperties.java](../src/main/java/lv/macibas/library/config/LibraryProperties.java)

---

## Filter un AOP — kods, kas izpildās "visur"

⚛️ **Filter** = **Axios interceptor** / Next.js **middleware**. Izpildās **pirms katra** pieprasījuma.
→ [RequestIdFilter.java](../src/main/java/lv/macibas/library/config/RequestIdFilter.java) — katram pieprasījumam pieliek unikālu ID.

⚛️ **AOP (Aspect)** = līdzīgi kā **HOC**, bet automātiski uzlikts daudzām metodēm. Labs logošanai, laika mērīšanai.
→ [LoggingAspect.java](../src/main/java/lv/macibas/library/config/LoggingAspect.java) — mēra, cik ilgi izpildās katra servisa metode.

---

## Events — Observer šablons Springā

```java
// LoanService (izsūta)
events.publishEvent(new BookBorrowedEvent(...));

// NotificationService (klausās)
@TransactionalEventListener
public void onBookBorrowed(BookBorrowedEvent event) { ... sūta e-pastu ... }
```

⚛️ Kā `dispatch(action)` Redux un reducers/listeners, kas reaģē.

💻 [NotificationService.java](../src/main/java/lv/macibas/library/notification/NotificationService.java)

---

## ✅ Pašpārbaude

1. Izskaidro IoC un DI tā, it kā stāstītu React izstrādātājam.
2. Kāpēc constructor injection labāka par `@Autowired` uz lauka?
3. Kāpēc servisā nedrīkst glabāt lietotāja datus laukā?
4. Kas notiek, ja ir 2 klases, kas implementē vienu interfeisu, un tu injicē interfeisu? (Atbilde: kļūda! Risina ar `@Primary`, `@Qualifier` vai injicē `List<>`)

➡️ Nākamā: [07-datubazes.md](07-datubazes.md)
