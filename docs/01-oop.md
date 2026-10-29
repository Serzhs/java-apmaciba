# 1. nodarbība — OOP (objektorientētā programmēšana)

> ⏱ ~1 stunda. Nesteidzies. Izlasi vienu punktu → atver kodu → palaid → tad nākamais.

## Kas vispār ir OOP?

**Vienā teikumā:** programmu veido no **objektiem**, kur katrs objekts glabā **savus datus** un pats **prot ar tiem strādāt**.

⚛️ **Tu jau to zini!** React komponents ir gandrīz objekts:

```jsx
function Counter() {            // ← klase (veidne)
  const [count, setCount] = useState(0);   // ← lauks (dati)
  const increment = () => setCount(count + 1); // ← metode (ko prot darīt)
  return <button onClick={increment}>{count}</button>;
}
<Counter />  <Counter />        // ← divi objekti, katram savs count
```

Tas pats Javā:

```java
class Counter {
    private int count = 0;          // lauks
    void increment() { count++; }   // metode
}
Counter a = new Counter();          // objekts 1
Counter b = new Counter();          // objekts 2, savs count
```

OOP ir **4 pīlāri**. Intervijā tos jautā gandrīz vienmēr.

---

## Pīlārs 1: Iekapsulēšana (Encapsulation)

**Vienkārši:** objekts **pats sargā savus datus**. No ārpuses tos nevar sabojāt — tikai caur metodēm, kas pārbauda noteikumus.

⚛️ **React:** vecāks komponents nevar tieši ielīst bērna `useState`. Bērns pats izlemj, kā savu state mainīt.

🏦 **Dzīves piemērs:** bankomāts. Tu nevari ielīst iekšā un paņemt naudu. Tu spied pogas (metodes), un bankomāts pārbauda: vai ir pietiekami naudas?

```java
class BankAccount {
    private BigDecimal balance;   // private = neviens no ārpuses neaiztiek

    void withdraw(BigDecimal amount) {
        if (balance < amount) throw new IllegalStateException("Nepietiek naudas");
        balance = balance - amount;
    }
}
```

💻 **Kods:** [Encapsulation.java](../src/main/java/lv/macibas/oop/Encapsulation.java)
💻 **Īstajā projektā:** [Book.java](../src/main/java/lv/macibas/library/domain/Book.java) — metode `markBorrowed()` neļauj aizņemties jau aizņemtu grāmatu.

🎤 **Intervijā saki:**
> "Iekapsulēšana nozīmē, ka objekta dati ir private un tos maina tikai caur metodēm, kas pārbauda noteikumus. Tā objekts nekad nenonāk nepareizā stāvoklī — piemēram, bankas kontam nevar būt negatīva bilance."

---

## Pīlārs 2: Mantošana (Inheritance)

**Vienkārši:** jauna klase **paņem visu no vecāka** klases un var pielikt klāt savu.

`Suns extends Dzīvnieks` → suns **IR** dzīvnieks, tāpēc prot visu, ko dzīvnieks.

⚛️ **React:** vecajos class komponentos bija `class App extends React.Component`. React komanda vēlāk **atteicās** no tā un pārgāja uz funkcijām + hooks. Kāpēc? Jo mantošana rada problēmas → skat. "Kompozīcija" zemāk.

```java
class Animal {
    String makeSound() { return "..."; }
}
class Dog extends Animal {
    @Override
    String makeSound() { return "Vau!"; }   // pārraksta vecāka metodi
}
```

⚠️ Java klase var mantot **tikai no VIENAS** klases. (Bet var implementēt daudzus interfeisus.)

💻 **Kods:** [Inheritance.java](../src/main/java/lv/macibas/oop/Inheritance.java)

🎤 **Intervijā saki:**
> "Mantošana ir 'IS-A' attiecība — apakšklase pārņem vecāka uzvedību un var to paplašināt vai pārrakstīt. Javā var mantot tikai no vienas klases. Praksē es bieži dodu priekšroku kompozīcijai, jo mantošana rada ciešu sasaisti."

---

## Pīlārs 3: Polimorfisms (Polymorphism)

**Vienkārši:** "daudzas formas". **Viena un tā pati komanda** — bet katrs objekts to izpilda **savā veidā**.

⚛️ **React:** tu raksti `<Button />`, bet tas izskatās dažādi:
```jsx
<Button variant="primary" />   // zila
<Button variant="danger" />    // sarkana
```
Kods, kas lieto `<Button>`, neinteresējas par detaļām.

```java
interface Shape { double area(); }

class Circle implements Shape { public double area() { return 3.14 * r * r; } }
class Square implements Shape { public double area() { return side * side; } }

for (Shape s : shapes) {
    s.area();   // Java PATI izvēlas pareizo area() — aplim vai kvadrātam
}
```

Svarīgākais: ja rīt pievienosi `Triangle`, **cikls nav jāmaina**. ✨

Ir 2 veidi (to mēdz jautāt):
- **Overriding** (pārrakstīšana) — apakšklase pārraksta vecāka metodi. Izlemj **programmas darbības laikā**.
- **Overloading** (pārslogošana) — vienāds metodes vārds, dažādi parametri: `add(int, int)` un `add(double, double)`. Izlemj **kompilējot**.

💻 **Kods:** [Polymorphism.java](../src/main/java/lv/macibas/oop/Polymorphism.java)

🎤 **Intervijā saki:**
> "Polimorfisms ļauj strādāt ar interfeisu, nezinot konkrēto klasi. Piemēram, man ir `PaymentProcessor` interfeiss, un es varu pievienot Stripe vai PayPal, nemainot kodu, kas to izsauc. Ir overriding — izvēle runtime, un overloading — izvēle compile-time."

---

## Pīlārs 4: Abstrakcija (Abstraction)

**Vienkārši:** parādi **KO** tas dara, paslēp **KĀ** tas to dara.

🚗 Tu brauc ar mašīnu ar stūri un pedāļiem. Tev nav jāzina, kā strādā dzinējs.

⚛️ **React:** tu lieto `<DatePicker onChange={...} />`. Tev nav jāzina, kā tas iekšā zīmē kalendāru. Tu zini tikai **props** (= interfeiss).

Javā abstrakcijai ir 2 rīki:

| | `interface` | `abstract class` |
|---|---|---|
| Kas tas ir | Tikai **līgums** ("tev jāprot X") | **Puspabeigta** klase |
| Cik var mantot | **Daudzus** | Tikai **vienu** |
| Var glabāt datus (laukus)? | ❌ Nē | ✅ Jā |
| Kad lietot | "Prot darīt" (`Comparable`, `PaymentProcessor`) | Kopīgs kods vairākām līdzīgām klasēm |

⚛️ `interface` ≈ TypeScript `interface ButtonProps`. Tikai apraksta formu, nav koda.

💻 **Kods:** [Abstraction.java](../src/main/java/lv/macibas/oop/Abstraction.java)

🎤 **Intervijā saki:**
> "Abstrakcija paslēpj implementācijas detaļas un parāda tikai to, kas vajadzīgs. Javā to dara ar interfeisiem un abstraktām klasēm. Interfeiss ir tīrs līgums, un klase var implementēt vairākus. Abstraktā klase var saturēt stāvokli un kopīgu kodu, bet mantot var tikai vienu."

---

## Bonus 1: Kompozīcija > Mantošana ⭐

**Vienkārši:** labāk **"objekts SATUR citu objektu"**, nevis **"objekts IR cits objekts"**.

⚛️ **Šo tu jau dari katru dienu React!**
```jsx
<Card>
  <Header />
  <Body />
</Card>
```
Tu neraksti `class FancyCard extends Card`. Tu **saliec** komponentus kopā. Tieši tas ir kompozīcija.

```java
class Car {
    private Engine engine;     // mašīnai IR dzinējs (HAS-A)
}
// Var nomainīt dzinēju: new Car(new ElectricEngine())
```

💻 **Kods:** [CompositionOverInheritance.java](../src/main/java/lv/macibas/oop/CompositionOverInheritance.java) — tur ir piemērs, kur mantošana **salūzt** un kompozīcija strādā.

---

## Bonus 2: `equals()` un `hashCode()` ⭐ (ĻOTI bieži jautā)

**Vienkārši:**
- `==` jautā: **"vai tas ir TAS PATS objekts?"** (tā pati vieta atmiņā)
- `.equals()` jautā: **"vai tiem ir VIENĀDS saturs?"**

⚛️ **JS:** `{a:1} === {a:1}` ir `false`! Tieši tas pats — `===` salīdzina atsauces. Tāpēc React `useEffect` dependencies ar objektiem mēdz "trakot".

**Noteikums:** ja pārraksti `equals()`, **obligāti** pārraksti arī `hashCode()`. Citādi `HashMap` un `HashSet` neatradīs tavus objektus.

🧠 **Viegli atcerēties:** `record` Javā to izdara automātiski.

💻 **Kods:** [EqualsAndHashCode.java](../src/main/java/lv/macibas/oop/EqualsAndHashCode.java)

🎤 **Intervijā saki:**
> "`==` salīdzina atsauces, `equals` — saturu. Ja divi objekti ir equal, tiem jābūt vienādam hashCode, citādi HashMap tos ieliks dažādos 'bucketos' un neatradīs. Stringus vienmēr salīdzinu ar equals."

---

## Bonus 3: Immutability (nemaināmi objekti)

**Vienkārši:** objektu pēc izveides **nevar mainīt**. Gribi izmaiņu → izveido **jaunu**.

⚛️ **React — tu to jau zini!**
```js
items.push(x);                  // ❌ React nepamanīs izmaiņu
setItems([...items, x]);        // ✅ jauns masīvs
```

Kāpēc tas labi BE? Nemaināmus objektus **droši** var lietot vairāki pavedieni (threads) vienlaicīgi — neviens neko nesabojās.

💻 **Kods:** [Immutability.java](../src/main/java/lv/macibas/oop/Immutability.java)

---

## ✅ Pašpārbaude (atbildi skaļi, it kā intervijā)

1. Nosauc 4 OOP pīlārus un katram pa vienam piemēram.
2. Ar ko atšķiras `interface` no `abstract class`?
3. Kāpēc React dod priekšroku kompozīcijai, nevis mantošanai?
4. Ko izdrukās `new String("a") == new String("a")`? Kāpēc?
5. Kas notiks, ja pārrakstīsi `equals`, bet ne `hashCode`?

➡️ Nākamā: [02-solid.md](02-solid.md)
