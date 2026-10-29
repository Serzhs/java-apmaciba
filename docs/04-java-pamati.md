# 4. nodarbība — Java pamati, kas jāzina

> ⏱ ~1,5 stundas (var sadalīt 2 dienās). Labā ziņa: **ļoti daudz no šī tu jau zini no JavaScript** — tikai cits sintakse.

---

## 1. Kolekcijas (List, Set, Map)

| Java | JS analogs | Kad lietot |
|---|---|---|
| `ArrayList` | `[]` masīvs | Saraksts ar secību. **Lieto 90% gadījumu.** |
| `HashSet` | `new Set()` | Bez dublikātiem, ātri pārbaudīt "vai ir?" |
| `HashMap` | `{}` objekts / `new Map()` | Atslēga → vērtība |
| `TreeMap` / `TreeSet` | — | Kad vajag **sakārtotu** |
| `LinkedHashMap` | `Map` (saglabā secību) | Map, kas atceras ievietošanas secību |

🎤 **Bieži jautā: "Kā strādā HashMap?"**
> "HashMap no atslēgas izrēķina `hashCode()`, kas norāda uz vietu ('bucket') iekšējā masīvā. Tur ar `equals()` atrod īsto atslēgu. Tāpēc meklēšana ir ļoti ātra — vidēji O(1). Ja vairākas atslēgas trāpa vienā vietā, tās glabā sarakstā vai kokā."

🎤 **"ArrayList vs LinkedList?"**
> "ArrayList iekšā ir masīvs — ātrs `get(i)`. LinkedList ir saistīts saraksts — ātra ievietošana sākumā, bet lēns `get(i)`. Praksē gandrīz vienmēr lietoju ArrayList."

💻 [CollectionsGuide.java](../src/main/java/lv/macibas/javacore/CollectionsGuide.java)

---

## 2. Streams = JS masīvu metodes 🎉

**Šo tu jau proti!** Tikai jāpieliek `.stream()` sākumā un `.toList()` beigās.

```js
// JavaScript
const names = users
  .filter(u => u.age > 18)
  .map(u => u.name);
```

```java
// Java
List<String> names = users.stream()
    .filter(u -> u.age() > 18)     // => kļūst par ->
    .map(u -> u.name())
    .toList();
```

| JS | Java Stream |
|---|---|
| `.filter()` | `.filter()` |
| `.map()` | `.map()` |
| `.reduce()` | `.reduce()` |
| `.find()` | `.filter().findFirst()` |
| `.some()` | `.anyMatch()` |
| `.every()` | `.allMatch()` |
| `.flat()` / `.flatMap()` | `.flatMap()` |
| `.sort()` | `.sorted()` |
| lodash `groupBy` | `Collectors.groupingBy()` ⭐ (bieži live coding!) |

💻 [StreamsAndLambdas.java](../src/main/java/lv/macibas/javacore/StreamsAndLambdas.java)

---

## 3. Optional = `?.` un `??`

**Vienkārši:** "kaste", kurā **var būt vērtība, var nebūt**. Lai izvairītos no `NullPointerException` (Java versija `Cannot read property of undefined` 😄).

```js
const nick = user?.nickname ?? "nav";
```
```java
String nick = findUser(id).map(User::nickname).orElse("nav");
```

💻 Tajā pašā failā: [StreamsAndLambdas.java](../src/main/java/lv/macibas/javacore/StreamsAndLambdas.java)

---

## 4. Izņēmumi (Exceptions)

`try / catch / finally` — tieši kā JS. Bet Javā ir **2 veidi**:

| | Checked | Unchecked |
|---|---|---|
| Piemērs | `IOException` (fails nav atrasts) | `NullPointerException`, `IllegalArgumentException` |
| Kompilators liek apstrādāt? | ✅ Jā, obligāti | ❌ Nē |
| Manto no | `Exception` | `RuntimeException` |

🌱 **Spring praksē:** savas biznesa kļūdas taisām **unchecked** (`extends RuntimeException`). Mūsu projektā: [NotFoundException](../src/main/java/lv/macibas/library/exception/NotFoundException.java) → automātiski kļūst par HTTP 404.

🎤 > "Checked izņēmumi kompilators liek apstrādāt — parasti ārējas problēmas kā faili vai tīkls. Unchecked ir RuntimeException — programmētāja kļūdas vai biznesa likumi. Spring `@Transactional` pēc noklusējuma atritina transakciju tikai uz unchecked."

💻 [Exceptions.java](../src/main/java/lv/macibas/javacore/Exceptions.java)

---

## 5. Generics = TypeScript `<T>`

```ts
function first<T>(items: T[]): T { return items[0]; }   // TypeScript
```
```java
<T> T first(List<T> items) { return items.get(0); }     // Java
```

`List<String>` = `string[]` / `Array<string>`. Tas pats koncepts!

💻 [Generics.java](../src/main/java/lv/macibas/javacore/Generics.java)

---

## 6. Pavedieni (Threads) un vienlaicīgums ⭐

**Šeit FE un BE atšķiras visvairāk!**

⚛️ **JavaScript** ir **viens pavediens** (single-threaded) + event loop. Divi kodi nekad neizpildās TIEŠI vienlaicīgi.

☕ **Java serveris** ir **daudzpavedienu**. 100 lietotāji sūta pieprasījumus → 100 pavedieni strādā **TIEŠI vienlaicīgi**.

💥 **Problēma (race condition):** divi pavedieni vienlaicīgi dara `counter++` → viens pieskaitījums "pazūd".
🛠 **Risinājumi:** `synchronized`, `AtomicInteger`, `ConcurrentHashMap`, vai vienkārši **nemaināmi objekti**.

| Java | JS |
|---|---|
| `CompletableFuture` | `Promise` |
| `.thenApply()` | `.then()` |
| `.exceptionally()` | `.catch()` |
| `CompletableFuture.allOf()` | `Promise.all()` |
| Virtual threads (Java 21) | (līdzīgi kā `async` — lēti gaidīt I/O) |

🎤 > "Spring serviss ir singleton un to lieto daudzi pavedieni vienlaicīgi, tāpēc servisos neglabāju mainīgu stāvokli laukos. Ja vajag kopīgu skaitītāju, lietoju AtomicInteger vai ConcurrentHashMap."

💻 [Concurrency.java](../src/main/java/lv/macibas/javacore/Concurrency.java)

---

## 7. Atmiņa un "pass by value"

**Stack** = īslaicīgi mainīgie metodes iekšā (kā funkcijas lokālie mainīgie).
**Heap** = visi objekti (`new ...`). Neizmantotus objektus automātiski tīra **Garbage Collector** (tāpat kā JS!).

🎤 **Āķīgs jautājums: "Java ir pass-by-value vai pass-by-reference?"**
> "Vienmēr pass-by-value. Objektiem tiek nodota atsauces **kopija**. Tāpēc metode var mainīt objekta saturu, bet nevar likt izsaucēja mainīgajam norādīt uz citu objektu."

⚛️ Tieši tāpat kā JS: funkcijā vari mainīt `obj.name`, bet `obj = {}` ārā neko nemainīs.

💻 [MemoryAndPassByValue.java](../src/main/java/lv/macibas/javacore/MemoryAndPassByValue.java)

---

## 8. Modernā Java (17, 21)

Rādi, ka zini jaunās lietas:
- **`record`** — īss datu objekts (kā TS `type`), automātiski `equals/hashCode/toString`
- **`var`** — `var list = new ArrayList<String>()` (kā JS `const`)
- **Text blocks** `"""..."""` — kā JS template literals `` ` ` ``
- **switch ar `->`** un pattern matching
- **Virtual threads** (Java 21)

💻 [ModernJava.java](../src/main/java/lv/macibas/javacore/ModernJava.java)

---

## ✅ Pašpārbaude

1. Pārraksti uz Java: `users.filter(u => u.active).map(u => u.email)`
2. Kāda atšķirība starp checked un unchecked izņēmumu?
3. Kāpēc JS nav race condition problēmas tā kā Javā?
4. Kas ir `Optional` un kāpēc tas vajadzīgs?

➡️ Nākamā: [05-ka-strada-backend.md](05-ka-strada-backend.md)
