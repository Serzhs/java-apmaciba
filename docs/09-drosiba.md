# 9. nodarbība — Drošība

> ⏱ ~45 min. Šeit tava FE pieredze ļoti noderēs — tu jau esi redzējis tokenus un CORS kļūdas!

---

## Autentifikācija vs Autorizācija

| | Jautājums | Kļūda | Piemērs |
|---|---|---|---|
| **Autentifikācija** (AuthN) | **KAS** tu esi? | 401 | Login ar paroli |
| **Autorizācija** (AuthZ) | Ko tu **DRĪKST** darīt? | 403 | Tikai admin drīkst dzēst |

🏢 Analoģija: caurlaide birojā. **Autentifikācija** = sargs pārbauda tavu ID. **Autorizācija** = tava karte atver tikai 3. stāvu, ne serveru telpu.

---

## Sesijas vs JWT

### Sesija (klasiski)
1. Login → serveris atceras tevi atmiņā/DB un iedod **cookie** ar sesijas ID
2. Katrs pieprasījums sūta cookie → serveris meklē, kas tu esi

### JWT (JSON Web Token) — tas, ko tu FE glabā
1. Login → serveris iedod **tokenu**: `eyJhbGciOi...`
2. FE to sūta: `Authorization: Bearer eyJhbGciOi...`
3. Serveris **pārbauda paraksu** — nav jāskatās DB!

⚛️ **Tu to jau dari:**
```js
fetch("/api/books", { headers: { Authorization: `Bearer ${token}` } });
```

**JWT sastāv no 3 daļām:** `header.payload.signature`
- payload = dati (lietotāja id, lomas, derīguma termiņš) — **NAV šifrēts, tikai base64!** Ikviens var nolasīt (pamēģini jwt.io). Tāpēc **nekad neliec paroles JWT**.
- signature = paraksts ar servera slepeno atslēgu → nevar viltot

| | Sesija | JWT |
|---|---|---|
| Kur glabājas stāvoklis | Serverī | Tokenā (klientā) |
| Mērogošana (daudz serveru) | Grūtāk (jādala sesijas) | Viegli ("stateless") |
| Izlogot uzreiz | Viegli (izdzēš sesiju) | Grūti (tokens derīgs līdz termiņam) |

🎤 > "JWT ir parakstīts tokens ar lietotāja datiem. Serveris pārbauda parakstu bez DB, tāpēc tas ir stateless un labi mērogojas. Mīnuss — grūti atsaukt, tāpēc lieto īsu derīgumu (piem. 15 min) + refresh tokenu."

🌱 **Spring Security** — Springā drošību dara ar Spring Security, kas ir **filtru ķēde** pirms kontrolieriem (atceries Filter no 6. nodarbības).

---

## CORS — tā sarkanā kļūda konsolē 😄

```
Access to fetch at 'http://localhost:8080' from origin 'http://localhost:5173'
has been blocked by CORS policy
```

**Kas tas ir:** pārlūks aizsargā lietotāju — neļauj lapai no `a.com` lasīt atbildes no `b.com`, ja `b.com` to **neatļauj**.

👉 **To labo BACKEND**, nevis FE! Serveris atbild ar header:
```
Access-Control-Allow-Origin: http://localhost:5173
```

Spring:
```java
@CrossOrigin(origins = "http://localhost:5173")   // uz kontroliera
// vai globāli WebMvcConfigurer.addCorsMappings(...)
```

🎤 > "CORS ir pārlūka drošības mehānisms. Backend ar headeriem pasaka, kuri domēni drīkst sūtīt pieprasījumus. Pārlūks pirms 'nevienkāršiem' pieprasījumiem sūta preflight OPTIONS pieprasījumu."

---

## Paroļu glabāšana

❌ **NEKAD** neglabā paroli kā tekstu.
❌ Ne MD5, ne SHA-256 (pārāk ātri → var uzlauzt ar brute force).
✅ **BCrypt** / Argon2 — speciāli **lēni** hash algoritmi ar **salt** (nejauša piedeva, lai vienādām parolēm būtu dažādi hash).

```java
PasswordEncoder encoder = new BCryptPasswordEncoder();
String hash = encoder.encode("parole123");            // glabā DB
encoder.matches("parole123", hash);                   // pārbauda login
```

---

## OWASP Top — biežākās ievainojamības

| Uzbrukums | Kas tas ir | Aizsardzība |
|---|---|---|
| **SQL Injection** | Lietotājs ievada `' OR 1=1 --` un tas kļūst par SQL daļu | **Parametrizēti vaicājumi** (JPA to dara automātiski). Nekad nesaliec SQL ar `+` |
| **XSS** | Ļaunprātīgs `<script>` tiek parādīts lapā | React pats escapo! ⚛️ (bet uzmanies ar `dangerouslySetInnerHTML`) |
| **CSRF** | Cita lapa liek tavam pārlūkam sūtīt pieprasījumu ar taviem cookies | CSRF tokens, `SameSite` cookies. Ar JWT headerī — mazāk aktuāli |
| **Broken Access Control** | Lietotājs maina URL `/orders/5` → `/orders/6` un redz svešu pasūtījumu | VIENMĒR pārbaudi serverī, vai resurss pieder lietotājam |
| **Sensitive Data Exposure** | API atgriež paroles hash, stack trace | DTO (sūti tikai vajadzīgo), vispārīgas 500 kļūdas |

**SQL injection piemērs:**
```java
❌ "SELECT * FROM users WHERE name = '" + name + "'"      // name = "' OR '1'='1" → atgriež VISUS
✅ @Query("select u from User u where u.name = :name")    // parametrs — drošs
```

💻 Mūsu [GlobalExceptionHandler](../src/main/java/lv/macibas/library/web/GlobalExceptionHandler.java) 500 kļūdās **neatgriež** iekšējo informāciju klientam.

---

## Citas lietas

- **HTTPS** — šifrē visu trafiku. Vienmēr produkcijā.
- **Secrets** (DB paroles, API atslēgas) — vides mainīgajos vai secret manager, **NE** git.
- **Rate limiting** — ierobežo pieprasījumu skaitu (pret brute force login).
- **Validācija serverī** — vienmēr (5. nodarbība).

## ✅ Pašpārbaude

1. 401 vs 403?
2. Vai JWT payload ir šifrēts? Ko tur nedrīkst likt?
3. Kurš labo CORS kļūdu — FE vai BE? Kā?
4. Kā aizsargāties no SQL injection?
5. Kāpēc paroli hasho ar BCrypt, nevis SHA-256?

➡️ Nākamā: [10-arhitektura.md](10-arhitektura.md)
