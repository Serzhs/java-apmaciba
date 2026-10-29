# 11. Intervijas jautājumi — ātrā atkārtošana

> Lieto kā **kartītes**: izlasi jautājumu → **aizsedz atbildi** → atbildi skaļi → salīdzini.
> Atbildes ir īsas (2–3 teikumi) — tieši tik, cik intervijā vajag. Ja grib vairāk, viņi pajautās.

---

## 🟦 OOP

**1. Nosauc 4 OOP pīlārus.**
> Iekapsulēšana (dati private, maina caur metodēm), mantošana (apakšklase pārņem vecāka uzvedību), polimorfisms (viens interfeiss, dažādas implementācijas), abstrakcija (paslēpj KĀ, parāda KO).

**2. Interface vs abstract class?**
> Interfeiss ir līgums bez stāvokļa, klase var implementēt vairākus. Abstraktā klase var saturēt laukus un kopīgu kodu, bet mantot var tikai vienu. Interfeisu lietoju "prot darīt", abstrakto klasi — kopīgam kodam līdzīgām klasēm.

**3. Overloading vs overriding?**
> Overloading — viens metodes vārds, dažādi parametri, izvēle kompilējot. Overriding — apakšklase pārraksta vecāka metodi, izvēle izpildes laikā.

**4. Kāpēc kompozīcija labāka par mantošanu?**
> Mantošana rada ciešu sasaisti — izmaiņas vecākā var salauzt bērnus. Kompozīcija ir elastīgāka, daļas var nomainīt un vieglāk testēt. React arī dod priekšroku kompozīcijai.

**5. `==` vs `equals()`?**
> `==` salīdzina atsauces (vai tas pats objekts), `equals` — saturu. Stringus vienmēr salīdzinu ar equals.

**6. equals un hashCode līgums?**
> Ja divi objekti ir equal, tiem jābūt vienādam hashCode. Ja pārraksti vienu, jāpārraksta abi, citādi HashMap/HashSet nestrādās pareizi.

**7. Kā uztaisīt immutable klasi?**
> final klase, private final lauki, nav setteru, mainīgiem laukiem (List) defensive copy. Vai vienkārši `record`. Immutable objekti ir thread-safe.

**8. Kāpēc String ir immutable?**
> Drošība, String pool (var droši dalīt), hashCode kešošana un thread-safety.

---

## 🟩 SOLID un šabloni

**9. SOLID?**
> Single Responsibility — viena atbildība. Open/Closed — paplašini, nemainot. Liskov — apakšklase aizvietojama. Interface Segregation — mazi interfeisi. Dependency Inversion — atkarība no abstrakcijām.

**10. Kādus dizaina šablonus zini?**
> Singleton (Spring beani), Builder (daudz parametru), Factory, Strategy (nomaināmi algoritmi, piem. paziņojumu kanāli), Observer (Spring Events), Proxy (Spring @Transactional strādā caur to).

---

## 🟨 Java

**11. ArrayList vs LinkedList?**
> ArrayList — masīvs, ātrs get(i). LinkedList — saistīts saraksts, ātra ievietošana galos. Praksē gandrīz vienmēr ArrayList.

**12. Kā strādā HashMap?**
> hashCode() nosaka bucket masīvā, equals() atrod īsto atslēgu bucketā. Vidēji O(1). Kolīzijas glabā sarakstā vai kokā (Java 8+).

**13. HashMap vs ConcurrentHashMap?**
> HashMap nav thread-safe. ConcurrentHashMap ir drošs vairākiem pavedieniem un ātrāks par Collections.synchronizedMap.

**14. Checked vs unchecked exception?**
> Checked — kompilators liek apstrādāt (IOException). Unchecked — RuntimeException, nav obligāti. Biznesa kļūdām lietoju unchecked.

**15. Java ir pass-by-value vai reference?**
> Vienmēr pass-by-value. Objektiem tiek nodota atsauces kopija.

**16. Stack vs Heap?**
> Stack — lokālie mainīgie un metožu izsaukumi, katram pavedienam savs. Heap — visi objekti, kopīgs, to tīra Garbage Collector.

**17. Kas ir Stream API?**
> Funkcionāls veids, kā apstrādāt kolekcijas — filter, map, reduce, groupingBy. Kā JS masīvu metodes. Starpoperācijas ir lazy — izpildās tikai ar termināloperāciju.

**18. Kas ir Optional?**
> Konteiners vērtībai, kas var nebūt. Izvairās no NullPointerException. Lietoju kā atgriežamo tipu, ne laukiem.

**19. synchronized vs volatile?**
> synchronized — tikai viens pavediens vienlaikus izpilda bloku. volatile — garantē, ka izmaiņas redz citi pavedieni, bet negarantē atomaritāti.

**20. Kas jauns Java 17/21?**
> Records, sealed classes, pattern matching switch, text blocks, virtual threads (21).

---

## 🟧 Spring

**21. IoC un DI?**
> IoC — objektu izveidi pārvalda Spring konteiners, ne mans kods. DI — Spring injicē atkarības, parasti caur konstruktoru.

**22. Kāpēc constructor injection?**
> Lauki var būt final, atkarības skaidri redzamas, viegli testēt bez Spring, un nevar izveidot objektu bez atkarībām.

**23. @Component vs @Service vs @Repository?**
> Visi reģistrē beanu. Atšķirība semantiska. @Repository papildus pārveido DB izņēmumus.

**24. Bean scopes?**
> singleton (noklusējums, viens), prototype (jauns katru reizi), request, session.

**25. Kas ir Spring Boot?**
> Spring ar auto-konfigurāciju, starter atkarībām un iebūvētu serveri. Var palaist kā vienu jar failu.

**26. Kā Spring Boot zina, ko konfigurēt?**
> @EnableAutoConfiguration skatās, kas ir classpath, un konfigurē atbilstoši — piem., redz H2 → izveido DataSource. Var pārrakstīt ar saviem beaniem vai application.yml.

**27. Kā apstrādāt kļūdas REST API?**
> @RestControllerAdvice ar @ExceptionHandler metodēm, kas pārvērš izņēmumus pareizos HTTP statusos. Atgriežu ProblemDetail formātā.

**28. Filter vs Interceptor?**
> Filter ir Servlet līmenī, pirms Spring, redz visus pieprasījumus. Interceptor ir Spring MVC līmenī un zina, kurš kontrolieris tiks izsaukts.

**29. Kas ir AOP?**
> Veids, kā pievienot "cross-cutting" loģiku (logošana, transakcijas, drošība) daudzām metodēm, nemainot to kodu. Spring to dara caur proxy.

---

## 🟥 Datubāzes

**30. @Transactional — kā strādā un slazdi?**
> Spring izveido proxy, kas atver transakciju pirms metodes un commit/rollback pēc. Slazdi: nestrādā self-invocation (this.method()), private metodēm, un rollback tikai uz unchecked izņēmumiem.

**31. ACID?**
> Atomicity (viss vai nekas), Consistency (derīgs stāvoklis), Isolation (transakcijas netraucē), Durability (saglabāts paliek).

**32. N+1 problēma?**
> Viens vaicājums ielādē N ierakstus, tad katram saistītajam — vēl viens. Risinu ar JOIN FETCH vai @EntityGraph.

**33. LAZY vs EAGER?**
> LAZY ielādē, kad piekļūst; EAGER — uzreiz. Noklusēti lietoju LAZY un ielādēju vajadzīgo ar JOIN FETCH.

**34. Optimistic vs pessimistic locking?**
> Optimistic — @Version lauks, pārbauda pie saglabāšanas, konfliktā kļūda. Labs, ja konflikti reti. Pessimistic — bloķē rindu (SELECT FOR UPDATE), citi gaida.

**35. Kas ir indekss?**
> Datu struktūra (parasti B-tree), kas paātrina meklēšanu. Paātrina lasīšanu, palēnina rakstīšanu.

**36. SQL vs NoSQL?**
> SQL — stingra struktūra, JOIN, transakcijas. NoSQL — elastīga struktūra, viegli mērogot horizontāli.

**37. INNER vs LEFT JOIN?**
> INNER — tikai sakritības abās tabulās. LEFT — visi no kreisās, labajā null, ja nav pāra.

---

## 🟪 REST, HTTP, drošība

**38. REST principi?**
> Resursi ar URL (lietvārdi), HTTP metodes darbībām, stateless, pareizi statusa kodi, JSON.

**39. PUT vs PATCH vs POST?**
> POST izveido (nav idempotents). PUT aizvieto visu (idempotents). PATCH maina daļu.

**40. Kas ir idempotents?**
> Atkārtots izsaukums dod to pašu rezultātu. GET, PUT, DELETE — jā; POST — nē.

**41. 401 vs 403?**
> 401 — neesi autentificējies. 403 — esi, bet nav tiesību.

**42. Kā strādā JWT?**
> Parakstīts tokens ar lietotāja datiem (header.payload.signature). Serveris pārbauda parakstu bez DB. Payload nav šifrēts — tikai base64.

**43. Kas ir CORS?**
> Pārlūka drošība, kas neļauj lapai lasīt atbildes no cita domēna, ja serveris to neatļauj ar Access-Control-Allow-Origin header.

**44. Kā aizsargāties no SQL injection?**
> Parametrizēti vaicājumi — JPA to dara automātiski. Nekad nesaliec SQL ar stringu konkatenāciju.

**45. Kāpēc DTO?**
> Drošība (klients nevar uzstādīt aizliegtus laukus), API nav piesiets DB struktūrai, sūti tikai vajadzīgo.

---

## 🟫 "Pastāsti par sevi / projektu" tipa jautājumi

**46. "Pastāsti par projektu, ko esi taisījis backendā."**
> Izmanto ŠO projektu! Piemēram: *"Uztaisīju bibliotēkas REST API ar Spring Boot. Ir slāņu arhitektūra — kontrolieri, servisi, repozitoriji. Lietoju DTO ar validāciju, centralizētu kļūdu apstrādi ar ProblemDetail, JPA ar @EntityGraph pret N+1, @Transactional izsniegšanai un optimistic locking vienlaicīgiem pieprasījumiem. Paziņojumus sūtu caur Spring Events pēc commit. Ir unit testi ar Mockito un integrācijas testi ar MockMvc."*

**47. "Tu esi FE izstrādātājs — kāpēc BE?"**
> *"Strādājot ar FE, es bieži redzēju API no otras puses un gribēju saprast, kā dati tiek apstrādāti un glabāti. FE pieredze man palīdz taisīt API, ko ir ērti lietot — es zinu, ko frontendam vajag."* ← tas ir tavs **plus**!

**48. "Ko darītu, ja nezini atbildi?"**
> Godīgi saki: *"Precīzi nezinu, bet es domāju, ka... [loģisks minējums]. Es to pārbaudītu dokumentācijā."* — tas ir daudz labāk nekā izdomāt.
