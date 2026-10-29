# 📖 FE ↔ BE vārdnīca

Tu jau zini React. Lielākā daļa backend ideju ir **tās pašas idejas ar citiem nosaukumiem**.
Kad kaut kas nav skaidrs — atgriezies šeit.

| Backend (Java/Spring) | Ko tas nozīmē | Tas pats React/FE pasaulē |
|---|---|---|
| **Klase** (`class Book`) | Veidne objektiem | Komponenta funkcija `function Book()` |
| **Objekts** (`new Book()`) | Konkrēts eksemplārs | Komponents uz ekrāna `<Book />` |
| **Konstruktors** | Ko objekts saņem, kad tiek izveidots | **props** |
| **Lauks** (`private String title`) | Objekta dati | **state** (`useState`) |
| **Metode** (`book.borrow()`) | Ko objekts prot darīt | Funkcija komponentā (`handleClick`) |
| **private** | Neviens no ārpuses neredz | Lokāls state — vecāks to nevar aiztikt |
| **interface** | Līgums: "tev JĀBŪT šīm metodēm" | TypeScript `interface ButtonProps` |
| **Mantošana** (`extends`) | Paņem visu no vecāka | Vecie React class komponenti `extends React.Component` |
| **Kompozīcija** | Objekts satur citus objektus | `<Card><Header/><Body/></Card>` — React dod priekšroku TIEŠI šim! |
| **Polimorfisms** | Viena "forma", dažāda uzvedība | `<Button variant="primary" />` vs `variant="danger"` |
| **record** | Nemaināms datu objekts | `const` objekts / TS `type` / `Readonly<T>` |
| **Immutability** | Nemaini — veido jaunu | `setState([...items, newItem])` — nekad `items.push()` |
| **Stream** (`list.stream().filter().map()`) | Saraksta apstrāde | `array.filter().map()` |
| **Optional** | Vērtības var nebūt | `user?.name ?? "nav"` |
| **CompletableFuture** | Asinhrons rezultāts | **Promise** / `async/await` |
| **Exception** + `try/catch` | Kļūda | Tas pats `try/catch` JS |
| **Maven** (`pom.xml`) | Bibliotēku pārvaldnieks | **npm** (`package.json`) |
| **`mvn spring-boot:run`** | Palaiž aplikāciju | `npm run dev` |
| **`mvn test`** | Palaiž testus | `npm test` |
| **application.yml** | Konfigurācija | `.env` fails |
| **Spring Profile** (`dev`, `prod`) | Dažādi iestatījumi videi | `.env.development` / `.env.production` |
| **@RestController** | Pieņem HTTP pieprasījumus | Tas, uz ko tu dari `fetch()` |
| **@GetMapping("/api/books")** | URL + metode | Next.js `app/api/books/route.ts` → `export function GET()` |
| **@RequestBody** | JSON no pieprasījuma | `body: JSON.stringify(data)` ko tu sūti ar fetch |
| **DTO** | Datu forma, ko sūta pa tīklu | TS `type BookResponse = {...}` ko tu gaidi no API |
| **@Valid** + `@NotBlank` | Ievaddatu pārbaude serverī | Formas validācija (Zod / Yup / react-hook-form) — bet serverī! |
| **Service** | Biznesa loģika | Custom hook (`useCart()`) — loģika atdalīta no UI |
| **Repository** | Darbs ar datubāzi | API klients / `services/api.ts` |
| **Entity** | Java klase = DB tabula | — (FE nav) |
| **Dependency Injection** | Spring iedod atkarības | **React Context** / props — komponents neveido pats, saņem no augšas |
| **Bean** | Objekts, ko pārvalda Spring | Vērtība Context Provider iekšā |
| **Singleton bean** | Viens objekts visai aplikācijai | Viens globāls store (Redux/Zustand) |
| **Filter / Interceptor** | Kods pirms/pēc katra pieprasījuma | **Axios interceptors** / Next.js **middleware** |
| **@ControllerAdvice** (kļūdu handleris) | Viena vieta visām kļūdām | **Error Boundary** |
| **Event** + `@EventListener` | Kaut kas notika → citi reaģē | `onClick` / `addEventListener` / `useEffect` reakcija |
| **Cache** | Saglabā rezultātu, lai nerēķina vēlreiz | React Query / `useMemo` |
| **Transakcija** | Viss vai nekas | — (nav FE analoga; skat. 07. nodarbību) |
| **JWT** | Lietotāja "caurlaide" | Tas tokens, ko tu glabā un sūti `Authorization: Bearer ...` |
| **CORS** | Kurš domēns drīkst saukt API | Tā sarkanā kļūda konsolē, kas tevi kaitina 😄 — to labo BACKEND |
| **Unit test** + Mockito | Testē vienu klasi | Jest + `jest.mock()` |
| **@WebMvcTest** + MockMvc | Testē API bez īsta servera | MSW (Mock Service Worker) |
| **@SpringBootTest** | Testē visu kopā | Cypress / Playwright |
| **Logs** (`log.info`) | Ieraksti, kas notiek | `console.log` |
