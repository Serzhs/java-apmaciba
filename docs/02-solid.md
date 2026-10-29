# 2. nodarbība — SOLID

> ⏱ ~1 stunda. SOLID = 5 noteikumi, kā rakstīt kodu, ko ir viegli mainīt. Jautā gandrīz katrā intervijā.

💻 Visi piemēri vienā failā: [Solid.java](../src/main/java/lv/macibas/solid/Solid.java) — palaid `main()` un skaties.

🧠 **Iegaumē tā:** katram burtam — **1 teikums + 1 React piemērs**. Ar to pietiek.

---

## S — Single Responsibility (viena atbildība)

**Vienkārši:** viena klase = **viens darbs**.

⚛️ **React:** tu netaisi vienu `<App>` komponentu ar 2000 rindām, kas ielādē datus, validē formu UN zīmē tabulu. Tu sadali: `useBooks()` hook, `<BookForm>`, `<BookTable>`.

```
❌ UserService: validē + saglabā DB + sūta e-pastu + ģenerē PDF
✅ EmailValidator, UserRepository, WelcomeMailer — katram savs darbs
```

🎤 "Klasei jābūt tikai vienam iemeslam mainīties. Piemēram, e-pasta sūtīšana ir atsevišķā klasē, tāpēc, mainot e-pasta servisu, man nav jāaiztiek reģistrācijas loģika."

---

## O — Open/Closed (atvērts paplašināšanai, slēgts mainīšanai)

**Vienkārši:** pievieno jaunu funkcionalitāti, **pievienojot jaunu kodu**, nevis **mainot veco**.

⚛️ **React:** `<Button variant="...">`. Ja vajag jaunu stilu — pievieno jaunu variantu, nevis pārraksti visu komponentu ar vēl vienu `if`.

```java
❌ if (type == "VIP") ... else if (type == "EMPLOYEE") ... else if ...   // mūžīgi aug

✅ interface DiscountPolicy { double apply(double price); }
   class VipDiscount implements DiscountPolicy { ... }
   class BlackFridayDiscount implements DiscountPolicy { ... }  // jauns — nekas vecs nav mainīts
```

🎤 "Kods ir atvērts paplašināšanai, bet slēgts modifikācijai. Garu if/else ķēžu vietā lietoju interfeisu un jaunu implementāciju katram gadījumam."

---

## L — Liskov Substitution (aizvietošana)

**Vienkārši:** ja `B extends A`, tad **visur, kur strādā A, jāstrādā arī B**. Bez pārsteigumiem.

⚛️ **React:** ja tu uztaisi `<MyInput>`, kas "ir kā `<input>`", tad tam jāatbalsta `value` un `onChange` kā parastajam inputam. Ja tas pēkšņi ignorē `onChange` — tas salauž cilvēku gaidas.

🟥 **Klasiskais piemērs:** `Square extends Rectangle`.
Taisnstūrim iestatām platumu 5, augstumu 4 → gaidām laukumu 20.
Kvadrātam, mainot augstumu, mainās arī platums → dabū 16. 💥 Kods salūzt.

🎤 "Apakšklasei jābūt aizvietojamai vietā, kur gaida vecāku, nesalaužot uzvedību. Klasiskais pārkāpums ir Square, kas manto no Rectangle."

---

## I — Interface Segregation (mazi interfeisi)

**Vienkārši:** labāk **daudz mazu** interfeisu nekā **viens milzīgs**. Neviens nedrīkst būt spiests implementēt metodes, kas viņam nav vajadzīgas.

⚛️ **React:** komponentam nepadod visu `user` objektu ar 50 laukiem, ja tam vajag tikai `name`. Props jābūt minimāliem.

```java
❌ interface Worker { work(); eat(); sleep(); }   // robotam jāimplementē eat()?!
✅ interface Workable { work(); }
   interface Eatable { eat(); }
```

🎤 "Klientiem nevajadzētu būt atkarīgiem no metodēm, ko tie nelieto. Sadalu lielus interfeisus mazos."

---

## D — Dependency Inversion (atkarība no abstrakcijām)

**Vienkārši:** klase **pati neveido** savas atkarības ar `new`. Tā tās **saņem no ārpuses** un zina tikai **interfeisu**.

⚛️ **React — šis ir svarīgākais salīdzinājums:**
```jsx
// ❌ komponents pats izveido API klientu — nevar testēt, nevar nomainīt
function BookList() {
  const api = new RealApiClient();
}

// ✅ saņem no ārpuses (props vai Context) — testā var iedot mock
function BookList({ api }) { ... }
<ApiContext.Provider value={mockApi}>
```

Tieši tā strādā **Spring**: tu klasei konstruktorā pasaki, kas tai vajadzīgs, un Spring to **iedod** (= Dependency Injection).

```java
class ReportService {
    private final ReportStorage storage;      // interfeiss, nevis konkrēta klase
    ReportService(ReportStorage storage) {    // saņem no ārpuses
        this.storage = storage;
    }
}
```

🎤 "Augsta līmeņa kods nav atkarīgs no konkrētām klasēm, bet no abstrakcijām. Spring to realizē ar Dependency Injection — es injicēju interfeisu caur konstruktoru, un testos varu ielikt mock."

---

## ✅ Pašpārbaude

1. Nosauc visus 5 burtus un ko tie nozīmē (1 teikums katram).
2. Kurš SOLID princips ir saistīts ar Spring Dependency Injection?
3. Kāpēc `Square extends Rectangle` ir slikta ideja?
4. Kā React komponentos tu jau ievēro "S"?

➡️ Nākamā: [03-dizaina-sabloni.md](03-dizaina-sabloni.md)
