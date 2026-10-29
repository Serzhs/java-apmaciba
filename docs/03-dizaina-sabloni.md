# 3. nodarbība — Dizaina šabloni (Design Patterns)

> ⏱ ~1 stunda. Šabloni = **gatavi risinājumi bieži sastopamām problēmām**. Intervijā parasti pajautā: "Kādus šablonus zini? Kur tos lietoji?"
> Pietiek zināt **6** labi. Katrs ir savā failā mapē [patterns/](../src/main/java/lv/macibas/patterns/).

---

## 1. Singleton — "tikai viens eksemplārs"

**Vienkārši:** visā aplikācijā ir **tikai VIENS** šīs klases objekts.

⚛️ **React:** Redux/Zustand **store** — visai aplikācijai viens.

🌱 **Spring:** visi tavi `@Service`, `@Repository` pēc noklusējuma ir **singletoni** — Spring izveido vienu un dod to visiem.

⚠️ Tāpēc servisā **NEGLABĀ** lietotāja datus laukos! Visi lietotāji dala to pašu objektu.

💻 [SingletonPattern.java](../src/main/java/lv/macibas/patterns/SingletonPattern.java)

---

## 2. Builder — "būvē soli pa solim"

**Vienkārši:** kad objektam ir **daudz parametru**, tos uzstāda pa vienam ar saprotamiem nosaukumiem.

```java
❌ new HttpRequest("POST", "/x", null, 30, true, false)   // kas ir kas?!
✅ HttpRequest.builder("/x").method("POST").timeout(30).build()
```

⚛️ **FE:** līdzīgi kā `fetch(url, { method: "POST", headers: {...} })` — nosaukti parametri. Vai jQuery/Cypress ķēdes: `cy.get().click().type()`.

💻 [BuilderPattern.java](../src/main/java/lv/macibas/patterns/BuilderPattern.java)

---

## 3. Factory — "rūpnīca, kas izveido objektus"

**Vienkārši:** tu saki **"dod man SMS paziņojumu"**, un rūpnīca izlemj, kuru klasi veidot. Tev nav jāzina detaļas.

⚛️ **React:**
```jsx
const icons = { email: EmailIcon, sms: SmsIcon };
const Icon = icons[type];     // ← izvēlas komponentu pēc tipa
return <Icon />;
```

🌱 **Spring:** viss Spring ir viena liela rūpnīca (`BeanFactory`) — tā izveido tavus objektus.

💻 [FactoryPattern.java](../src/main/java/lv/macibas/patterns/FactoryPattern.java)

---

## 4. Strategy — "nomaināms algoritms"

**Vienkārši:** vairāki veidi, kā izdarīt vienu lietu, un tu **izvēlies, kuru lietot**. Aizstāj garus `if/else`.

⚛️ **React:** `sort` funkcijai padod dažādus salīdzinātājus:
```js
items.sort(byPrice);   items.sort(byName);   // stratēģija = funkcija
```

📦 Piegāde: DPD, Omniva, bezmaksas — katrs savs aprēķins, bet checkout kods vienāds.

🌱 **Mūsu projektā:** [NotificationSender](../src/main/java/lv/macibas/library/notification/NotificationSender.java) ar e-pasta un SMS implementācijām.

💻 [StrategyPattern.java](../src/main/java/lv/macibas/patterns/StrategyPattern.java)

---

## 5. Observer — "paziņo visiem, kas klausās"

**Vienkārši:** kad kaut kas notiek, **visi abonenti automātiski uzzina**. Tas, kurš paziņo, nezina, kas klausās.

⚛️ **Tu to zini:** `button.addEventListener("click", ...)`, `onClick`, Redux `subscribe`, WebSocket ziņas.

🌱 **Mūsu projektā:** kad grāmata aizņemta → `LoanService` izsūta notikumu `BookBorrowedEvent` → [NotificationService](../src/main/java/lv/macibas/library/notification/NotificationService.java) to "dzird" un sūta e-pastu + SMS.

💻 [ObserverPattern.java](../src/main/java/lv/macibas/patterns/ObserverPattern.java)

---

## 6. Decorator / Proxy — "ietin objektu un pievieno kaut ko"

**Vienkārši:** ap objektu uzliek "apvalku", kas izskatās tāpat, bet **pievieno papildus darbību** (logošana, kešošana, drošība).

⚛️ **React:** **Higher-Order Component** — `withAuth(MyPage)`, `withRouter(Component)`, `React.memo(Component)`. Ietin komponentu un pievieno uzvedību!

🌱 **Spring (⭐ svarīgi):** `@Transactional`, `@Cacheable` strādā tieši tā — Spring ap tavu servisu izveido **proxy**.
→ Tāpēc, ja klase izsauc **pati savu** metodi (`this.metode()`), `@Transactional` **nestrādā** — izsaukums neiet caur apvalku. To mēdz jautāt kā āķīgu jautājumu!

💻 [DecoratorAndProxyPattern.java](../src/main/java/lv/macibas/patterns/DecoratorAndProxyPattern.java)

---

## 🎤 Gatava atbilde intervijai

> "Lietoju Strategy, piemēram, paziņojumiem — ir `NotificationSender` interfeiss ar e-pasta un SMS implementācijām, un Spring injicē visas sarakstā. Builder lietoju objektiem ar daudziem parametriem. Observer — Spring events, lai pēc pasūtījuma nosūtītu e-pastu, nesasaistot servisus. Un zinu, ka Spring `@Transactional` strādā caur Proxy šablonu."

## ✅ Pašpārbaude

1. Kurš šablons ir React HOC (`withAuth(Page)`)?
2. Kāpēc Spring servisos nedrīkst glabāt lietotāja datus laukos?
3. Kāpēc `@Transactional` nestrādā, ja metodi izsauc no tās pašas klases?

➡️ Nākamā: [04-java-pamati.md](04-java-pamati.md)
