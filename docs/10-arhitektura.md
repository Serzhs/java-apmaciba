# 10. nodarbība — Arhitektūra un sistēmu dizains (pamati)

> ⏱ ~1 stunda. Junior/mid intervijās jautā **pamatus**. Nav jābūt ekspertam — jāsaprot idejas.

---

## Monolīts vs Mikroservisi

**Monolīts** = viena aplikācija, viena datubāze (kā mūsu projekts).
**Mikroservisi** = daudz mazu aplikāciju, katra ar savu DB, runā caur HTTP vai ziņām.

⚛️ Monolīts = viena liela React aplikācija. Mikroservisi ≈ **micro-frontends** (katra komanda savu daļu izvieto atsevišķi).

| | Monolīts | Mikroservisi |
|---|---|---|
| Sākt | ✅ Vienkārši | ❌ Sarežģīti |
| Izvietot (deploy) | Visu kopā | Katru atsevišķi |
| Mērogot | Visu kopā | Tikai to, kas noslogots |
| Kļūdu meklēšana | ✅ Viegli | ❌ Grūti (pāri servisiem) |
| Kad | Mazas/vidējas komandas, sākumā | Lielas organizācijas, daudz komandu |

🎤 > "Es sāktu ar labi strukturētu monolītu un sadalītu mikroservisos tikai tad, kad ir reāla vajadzība — piemēram, dažādām daļām vajag atšķirīgu mērogošanu vai tās attīsta dažādas komandas. Mikroservisi atrisina organizatoriskas problēmas, bet pievieno tehnisku sarežģītību."

---

## Mērogošana (Scaling)

- **Vertikālā** — lielāks serveris (vairāk RAM, CPU). Vienkārši, bet ir griesti.
- **Horizontālā** — vairāk serveru + **Load Balancer** sadala pieprasījumus.

```
            ┌──► Serveris 1 ─┐
Lietotāji ──► Load Balancer ──► Serveris 2 ──► Datubāze
            └──► Serveris 3 ─┘
```

⚠️ Lai horizontālā strādātu, serverim jābūt **stateless** — neglabāt lietotāja datus atmiņā (tāpēc JWT ir populārs!).

---

## Kešošana (Cache)

**Vienkārši:** saglabā rezultātu ātrā vietā, lai nav jārēķina/jāprasa DB vēlreiz.

⚛️ **Tu to zini:** React Query / SWR kešo API atbildes. `useMemo` kešo aprēķinus. Pārlūks kešo bildes.

**BE:** **Redis** (atmiņā glabāta ātra atslēga-vērtība DB). Spring: `@Cacheable("books")`.

⚠️ **Grūtākā daļa:** kad kešu **iztīrīt** (invalidation), lai nerādītu vecus datus. *"There are only two hard things in Computer Science: cache invalidation and naming things."*

---

## Ziņu rindas (Message Queues) — Kafka, RabbitMQ

**Problēma:** lietotājs pasūta → jāsūta e-pasts, jāģenerē rēķins, jāatjauno noliktava. Ja darām visu uzreiz — lietotājs gaida 10 sekundes.

**Risinājums:** ieliek **ziņu rindā** → atbild lietotājam uzreiz → citi servisi apstrādā fonā.

```
Pasūtījuma serviss ──► [ rinda: "OrderPlaced" ] ──► E-pasta serviss
                                                ──► Noliktavas serviss
```

⚛️ Tas ir **Observer šablons** (3. nodarbība) starp serveriem. Mūsu projektā tas pats notiek iekš vienas aplikācijas ar Spring Events.

🎤 > "Rindas lieto, lai atdalītu servisus un apstrādātu lēnus darbus asinhroni. Tas uzlabo atbildes laiku un noturību — ja e-pasta serviss nestrādā, ziņas paliek rindā un tiek apstrādātas vēlāk."

---

## Docker

**Vienkārši:** "kaste", kurā ir tava aplikācija + viss, kas tai vajadzīgs (Java, bibliotēkas). Strādā vienādi uz jebkura datora.

> "Bet uz mana datora strādāja!" — Docker atrisina šo problēmu.

⚛️ Tu varbūt esi redzējis `Dockerfile` Next.js projektam. Javai tas pats:
```dockerfile
FROM eclipse-temurin:21-jre
COPY target/be-macibas-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

- **Image** = recepte / klase
- **Container** = palaists eksemplārs / objekts (😉 OOP!)
- **docker-compose** = palaiž vairākus konteinerus kopā (app + Postgres + Redis)

---

## CI/CD

- **CI** (Continuous Integration) — katrs push → automātiski build + testi (GitHub Actions)
- **CD** (Continuous Delivery/Deployment) — ja testi zaļi → automātiski izvieto

⚛️ Tāpat kā Vercel automātiski izvieto tavu Next.js pēc push.

---

## Observability — kā zināt, kas notiek produkcijā

- **Logi** — `log.info(...)` (kā `console.log`, bet serverī un saglabāti)
- **Metrikas** — cik pieprasījumu/sek, cik kļūdu, cik atmiņas. Mūsu projektā: http://localhost:8080/actuator/metrics
- **Health check** — http://localhost:8080/actuator/health → load balancer pārbauda, vai serveris dzīvs
- **Tracing** — izseko vienu pieprasījumu cauri vairākiem servisiem (mūsu `X-Request-Id` ir vienkāršs piemērs)

---

## Kā atbildēt uz "Uzprojektē X sistēmu" jautājumu

Ja pajautā, piem., "Kā tu uztaisītu URL saīsinātāju?", seko soļiem:

1. **Precizē prasības** — "Cik lietotāju? Vai vajag statistiku? Vai saites beidzas?"
2. **API** — `POST /links {url}` → `{shortCode}`, `GET /{shortCode}` → 302 redirect
3. **Datu modelis** — tabula `links(id, short_code UNIQUE, original_url, created_at)`
4. **Vienkāršs dizains** — 1 serveris + 1 DB
5. **Mērogo** — "Lasīšanas ir daudz vairāk nekā rakstīšanas → pievienotu Redis kešu un vairākus serverus aiz load balancer"

👉 Svarīgākais — **domā skaļi** un sāc vienkārši. Intervētājs vērtē domāšanas gaitu, ne "pareizo atbildi".

## ✅ Pašpārbaude

1. Kad izvēlētos mikroservisus, kad monolītu?
2. Kāpēc horizontālai mērogošanai serverim jābūt stateless?
3. Kas ir grūtākais kešošanā?
4. Docker image vs container — ar ko atšķiras? (OOP analoģija!)

➡️ Nākamā: [11-intervijas-jautajumi.md](11-intervijas-jautajumi.md) — gatavas atbildes atkārtošanai
