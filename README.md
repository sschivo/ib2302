# Gedistribueerde Algoritmen (IB2302) - Programmeeropdrachten



## Introductie

Om aan de bijzondere verplichting van IB2302 te voldoen, dient de student vijf programmeeropdrachten (in Java) succesvol te voltooien. De opdrachten beslaan de volgende onderwerpen:

 * **Opdracht 1:** Steen-Papier-Schaar (week 1)
 * **Opdracht 2:** Gedragsmodellen (week 2)
 * **Opdracht 3:** Snapshots (week 3-4)
 * **Opdracht 4:** Waves (week 5-6)
 * **Opdracht 5:** Deadlock-detectie (week 7-8)

Bij elke opdracht wordt aan de student verstrekt: **(a)** klassen, waarin sommige methoden nog niet geïmplementeerd zijn; **(b)** unit-tests voor de methoden die nog niet geïmplementeerd zijn; **(c)** een library die de student dient te gebruiken om de methoden die nog niet geïmplementeerd zijn te implementeren. De opdracht is dus: implementeer de methoden die nog niet geïmplementeerd zijn.

Wanneer **alle** opdrachten zijn gemaakt, kunnen deze collectief worden ingeleverd ter beoordeling, inclusief een bondig reflectieverslag (maximaal 800 woorden). Er zijn twee mogelijke scores: *voldoende* en *onvoldoende*. De student haalt een *voldoende* als aan **elk** van de volgende eisen wordt voldaan:

 * Elke verstrekte unit-test voor elke opdracht slaagt.
 * De *[cyclomatische complexiteit](https://en.wikipedia.org/wiki/Cyclomatic_complexity)* van elke geïmplementeerde methode is *kleiner* dan het aantal unit-tests voor die methode. De intentie van deze eis is dat de docent "sjoemelimplementaties" (= implementaties die tijdens de executie proberen te achterhalen welke unit-test wordt uitgevoerd en aan de hand daarvan precies de verwachte output produceren) kan afkeuren. Zolang het duidelijk is dat er van een sjoemelimplementatie geen sprake is, hoeft de student zich geen zorgen te maken over wat cyclomatische complexiteit precies inhoudt.
 * Het reflectieverslag geeft inzicht in de ervaringen van de student met het programmeren van gedistribueerde algoritmen.

Voor de duidelijkheid: elke unit-test die later bij de beoordeling door de docent wordt gebruikt, is een unit-test die eerder aan de student is verstrekt.

Per inschrijving heeft de student drie inleverpogingen. Als na de derde inleverpoging nog geen voldoende is behaald, dan kan de student het vak niet meer succesvol afronden. Zie [Brightspace](https://brightspace.ou.nl/) voor deadlines.



## Voorbereiding

### Virtual machine (VM)

Voor het maken van de programmeeropdrachten bieden we een virtual machine (VM) aan. Het doel van de VM is om alle studenten te laten werken met hetzelfde besturingssysteem, dezelfde versie van Java, en dezelfde versie van Eclipse; dit maakt het oplossen van problemen door de docent, zodra de VM is geinstalleerd is (hieronder beschreven), een stuk eenvoudiger voor zowel de student als de docent. Hoewel sterk aangeraden, is het niet verplicht om de VM te gebruiken. Echter, de docent biedt geen ondersteuning bij installatie en configuratie van de benodigde tools (Java SDK, Eclipse, ``git``) buiten de VM.

 1. Download en installeer [VirtualBox](https://www.virtualbox.org).
 1. Download de [virtual machine](https://openuniversiteit-my.sharepoint.com/:u:/g/personal/ssc_ou_nl/EV2zYWHzEhJIvF7RqJGjj_YBntjRxCoxyeQygzJ6quXV9w?e=MbS1Dt).
 1. Importeer de VM in VirtualBox.
 1. [Optioneel] Verander de keyboard layout naar `azerty`:
     1. Start de VM.
     1. Open een terminal en voer uit:
        ```
        setxkbmap be
        ```
 
### Eclipse

 1. Start de VM.
 1. Open een terminal en voer uit:
    ```
    git clone https://github.com/sschivo/ib2302.git
    ```
 1. Open Eclipse.
 1. Importeer de geclonede repository:
     1. Klik: ``File`` (menubalk) > ``Import...`` > ``General`` > ``Existing Projects into Workspace``
     1. Bij ``Select root directory``, vul in: ``/home/ou/ib2302/Opdrachten``
     1. Klik: ``Finish``



## Algemene aanwijzingen

### Verstrekte code

De verstrekte code bestaat uit zes packages: ``week1``, ``week2``, ``week34``, ``week56``, ``week78``, en ``framework``. De eerste vijf packages komen overeen met de vijf opdrachten. Package ``framework`` is een kleine library waarmee gedistribueerde algoritmen kunnen worden geïmplementeerd. De benodigde klassen en interfaces op een rijtje:

 * Klasse ``Process`` representeert processen. Relevante methoden zijn:
    * ``p.getName()`` retourneert de naam van ``p``.
    * ``p.getIncoming()`` retourneert de inkomende kanalen van ``p``.
    * ``p.getOutgoing()`` retourneert de uitgaande kanalen van ``p``.
    * ``p.init()`` laat ``p`` zichzelf initialiseren (= verwerking van een init-event) 
    * ``p.send(m,c)`` laat ``p`` bericht ``m`` versturen langs kanaal ``c`` (als onderdeel van de verwerking van een init- of ontvang-event).
    * ``p.receive(m,c)`` laat ``p`` bericht ``m`` ontvangen langs kanaal ``c`` (= verwerking van een ontvang-event).
    * ``p.print(s)`` laat ``p`` tekst ``s`` printen naar ``System.out`` (als onderdeel van de verwerking van een init- of ontvang-event).
   
   **NB1:** ``Process`` is een **abstracte klasse**, met ``init`` en ``receive`` als **abstracte methoden**. Dit betekent dat er geen instanties van ``Process`` gecreëerd kunnen worden; van (niet-abstracte) subklassen van ``Process`` (die methoden ``init`` en ``receive`` van een implementatie voorzien) kunnen wel instanties worden gecreëerd.

   **NB2:** Importeer ``Process`` door ``import framework.Process`` handmatig toe te voegen; Eclipse doet dit niet automatisch (en waarschuwt ook niet als het is vergeten), omdat er in de standaardbibliotheek van Java ook een klasse ``Process`` is (``java.lang.Process``).

   **NB3:** Methode ``print`` slaat intern op wat er tijdens een executie allemaal al geprint is (en doet dus meer dan ``System.out.print``). Deze informatie wordt in de unit-tests geraadpleegd. Gebruik deze methode dus *alleen* zoals geinstrueerd in de opdrachtbeschrijvingen; gebruik voor alle andere prints (bijvoorbeeld om te debuggen) ``System.out.print`` en/of ``System.out.println``.
   
 * Klasse ``Channel`` representeert kanalen tussen processen. Relevante methoden zijn:
    * ``c.getSender()`` retourneert het proces aan de ingang van het kanaal ``c``.
    * ``c.getReceiver()`` retourneert het proces aan de uitgang van het kanaal ``c``.

   **NB:** Klasse ``Channel`` is al volledig geimplementeerd; de student hoeft hier zelf niets aan te doen. Het plaatsen van een bericht in een kanaal gebeurt als onderdeel van methode ``send`` van klasse ``Process``.
 
 * Interface ``Message`` representeert berichten. De interface specificeert geen methoden; klassen die de interface implementeren zijn vrij om zelf relevante attributen en methoden aan te bieden.
 
 * Klasse ``IllegalReceiveException`` is een subklasse van ``Exception``. Het idee is dat een ``IllegalReceiveException`` wordt geworpen als onderdeel van methodeaanroep ``p.receive(m,c)`` als deze aanroep niet is toegestaan volgens het gedistribueerde algoritme dat ``p`` volgt (afhankelijk van de lokale toestand van ``p`` en/of de argumenten van de aanroep).
 
 * Kennis van de overige klassen in package ``framework`` is **niet nodig** voor het maken van de opdrachten.

### Toe te voegen code

 * Met uitzondering van Opdracht 2 bestaat elke opdracht uit het schrijven van implementaties van methoden ``init`` en ``receive`` (= implementeren van gedrag dat een proces moet vertonen wanneer init- en ontvang-events plaatsvinden), in subklassen van klasse ``framework.Process``. Gebruik hiervoor als basis de informele beschrijvingen die besproken zijn in de hoorcolleges.
 
 * Het staat de student vrij om&mdash;geheel naar eigen inzicht&mdash;extra attributen, methoden, en zelfs klassen toe te voegen. Echter, testklassen (= alle klassen met een naam van de vorm ``...Test``) en de inhoud van package ``framework`` dienen ongewijzigd te blijven.
 
 * Bij twijfel over de opdrachtomschrijving: bestudeer de unit-tests. Deze bepalen uiteindelijk welk gedrag goed en fout is. **Let op:** dit is geen "testontleedspel": je hoeft niet tegen de unit-tests te vechten totdat je uiteindelijk een pass krijgt! Als je het doel of het verwachte resultaat van een test niet snapt, schrijf dan in de Discussies en vraag de docenten.

 * De unit-tests controleren ook op **robuustheid** van implementaties van methode ``receive``; deze implementaties dienen gebruik te maken van ``IllegalReceiveException`` om ongewenste situaties te signaleren (bijvoorbeeld: de ontvangst van een tiende bericht in een ronde Steen-Papier-Schaar met slechts negen deelnemers). Echter, algemene eisen waaraan de implementatie van *elk* gedistribueerd algoritme moet voldoen, mogen weggelaten worden (bijvoorbeeld: bij een methodeaanroep ``p.receive(m,c)`` is het niet nodig om te controleren of ``m`` en ``c`` niet ``null`` zijn, en of ``c`` daadwerkelijk een inkomend kanaal is van ``p``).
 
 * Om unit-tests uit te voeren:
    * Open de klasse waarin de unit-tests gedefinieerd zijn.
    * Klik: ``Run`` (menubalk) > ``Coverage``



## Opdracht 1: Steen-Papier-Schaar (week 1)

### 1a: Een eerlijk proces

**De opdracht:** Implementeer methoden ``init`` en ``receive`` in klasse ``week1.RockPaperScissorsProcess``.

**Voldoende/onvoldoende?** Alle unit-tests in klasse ``week1.RockPaperScissorsProcessTest`` dienen te slagen.

**Aanwijzingen:**

 * Gebruik de informele beschrijving en/of pseudocode die tijdens Hoorcollege 1 is behandeld. Wanneer een proces van elk ander proces een item heeft ontvangen, print het ``<win> <lose>`` (gebruik methode ``print`` van superklasse ``framework.Process``).

 * Klasse ``week1.Item`` representeert items (steen, papier, of schaar).
 
 * Klasse ``week1.RockPaperScissorsMessage`` representeert berichten met steen, papier, of schaar als inhoud.

### 1b: De valsspeler

**De opdracht:** Implementeer methoden ``init`` en ``receive`` in klasse ``week1.RockPaperScissorsCheatingProcess``.

**Voldoende/onvoldoende?** Alle unit-tests in klasse ``week1.RockPaperScissorsCheatingProcessTest`` dienen te slagen.

**Aanwijzingen:**

 * Het is de bedoeling om Cheating Guldo, wiens lokaal transitiesysteem is besproken tijdens Hoorcollege 1, te veralgemeniseren, volgens de volgende beschrijving:
    * Een algemeen valsspelend proces ``p`` ontvangt eerst van elk ander proces een item.
    * Wanneer ``p`` van elk ander proces een item heeft ontvangen, kiest ``p`` een eigen item zodanig dat het: in ieder geval niet verliest; en, wint als het kan winnen. (Met meer dan twee spelers kan het zijn dat ``p`` geen item kan kiezen zodanig dat hij wint, maar ``p`` kan wel voorkomen dat hij verliest.)
    * Vervolgens verstuurt ``p`` zijn gekozen item naar elk ander proces.
    * Tenslotte print ``p``, zoals eerlijke processen, ``<win> <lose>`` (gebruik methode ``print`` van superklasse ``framework.Process``).

### 1c: Meerdere rondes

**De opdracht:** Implementeer methoden ``init`` en ``receive`` in klasse ``week1.RockPaperScissorsMultiRoundsProcess``.

**Voldoende/onvoldoende?** Alle unit-tests in klasse ``week1.RockPaperScissorsMultiRoundsProcessTest`` dienen te slagen.

**Aanwijzingen:**

 * Dit is een uitbreiding op **1a**; de eerder toegevoegde implementaties van ``init`` en ``receive`` van klasse ``week1.RockPaperScissorsProcess`` kunnen daarom als basis worden genomen (hoewel het aannemelijk is dat aanzienlijke wijzigingen noodzakelijk zijn). Het idee van de uitbreiding is dat elk proces aan het eind van de huidige ronde bepaalt&mdash;op basis van zijn winnaar- en verliezerschap&mdash;of het meedoet aan de volgende ronde. In detail:
     * Als een proces winnaar is in de huidige ronde (= zijn item verslaat het item van minimaal een ander proces), en verliezer (= het item van minimaal een ander proces verslaat zijn item), dan doet het mee aan de volgende ronde.
     * Als een proces winnaar is in de huidige ronde, maar geen verliezer, dan doet het niet mee aan de volgende ronde (sterker nog: er is geen volgende ronde). In dit geval is het proces winnaar van het hele spel, en print het ``true`` (gebruik methode ``print`` van superklasse ``framework.Process``).
     * Als een proces verliezer is in de huidige ronde, maar geen winnaar, dan doet het niet mee aan de volgende ronde. In dit geval is het proces verliezer van het hele spel en print het ``false``. Wanneer het proces in het vervolg een bericht ontvangt, stuurt het datzelfde bericht terug naar de afzender.
     * Als een proces geen verliezer is in de huidige ronde, en geen winnaar, dan doet het mee aan de volgende ronde.

 * Neem aan dat kanalen de volgorde waarin berichten verstuurd zijn, behouden (FIFO).

 * Een complicatie is het fenomeen dat een proces "voor kan lopen" op andere processen. De volgende executie is, bijvoorbeeld, mogelijk:
     1. Guldo, in ronde 1, verstuurt ``Item.ROCK`` naar Recoome en Burter.
     1. Recoome, in ronde 1, verstuurt ``Item.PAPER`` naar Guldo en Burter.
     1. Burter, in ronde 1, verstuurt ``Item.SCISSORS`` naar Guldo en Recoome.
     1. Recoome ontvangt ``Item.ROCK`` en ``Item.SCISSORS`` van Guldo en Burter; hij bepaalt vervolgens dat hij zowel winnaar als verliezer is in ronde 1 en doet daarom mee aan ronde 2.
     1. Recoome, in ronde 2, verstuurt ``Item.SCISSORS`` naar Guldo en Burter. Recoome is nu dus al met ronde 2 begonnen, terwijl Guldo en Burter nog met ronde 1 bezig zijn.
     1. Guldo, in ronde 1, ontvangt ``Item.PAPER`` van Recoome.
     1. Guldo, in ronde 1, ontvangt ``ITEM.SCISSORS`` van Recoome. Guldo heeft nu dus, terwijl hij nog in ronde 1 is, Recoome's item uit ronde 2 ontvangen.

   Er is aanvullend boekhoudwerk nodig om ervoor te zorgen dat goed omgegaan wordt met dit soort situaties.



## Opdracht 2: Gedragsmodellen (week 2)

### 2a: (Globale) transitiesystemen en executies

**De opdracht:** Implementeer methode ``hasExecution`` in klasse ``week2.GlobalTransitionSystem``.

**Voldoende/onvoldoende?** Alle unit-tests in klasse ``week2.GlobalTransitionSystemsTest`` dienen te slagen.

**Aanwijzingen:**

 * Klasse ``week2.Configuration`` is een attribuutloze klasse; elk ``Configuration``-object representeert een configuratie in een globaal transitiesysteem, maar de interne details zijn voor deze opdracht irrelevant (daarom: geen attributen).

 * Klasse ``week2.GlobalTransitionSystem`` heeft twee attributen: ``transitions`` en ``initial``. Attribuut ``transitions`` representeert de transities in een globaal transitiesysteem. Enkele voorbeelden:
    * Als er een transitie is van configuratie ``c1`` naar configuratie ``c2`` middels event ``e``, dan geldt: ``c2.equals(transitions.get(c1).get(e))``
    * De events die kunnen plaatsvinden in configuratie ``c`` zijn: ``transitions.get(c).keySet()``
    * De configuraties die met een transitie bereikt kunnen worden vanuit ``c`` zijn: ``transitions.get(c).values()``
 
 * Wanneer een configuratie geen uitgaande transities heeft, geldt: ``transitions.get(c) == null``

 * Begrip van de code onder de "horizontale lijn" in klasse ``week2.GlobalTransitionSystem`` (onder methode ``hasExecution``) is onnodig voor deze opdracht.

### 2b: Causale ordeningen

**De opdracht:** Implementeer methoden ``CausalOrder`` (constructor) en ``toComputation`` in klasse ``week2.CausalOrder``.

**Voldoende/onvoldoende?** Alle unit-tests in klasse ``week2.CausalOrderTest`` dienen te slagen.

**Aanwijzingen:**

 * Klasse ``Event`` is een abstracte klasse met subklassen ``SendEvent``, ``ReceiveEvent``, en ``InternalEvent``; elk ``Event``-object representeert een (verzend-, verstuur-, of intern) event. Methode ``getProcess`` retourneert het proces bij wie het event plaatsvindt; de subklassen hebben ook een aantal specifieke methoden die nuttig kunnen zijn bij het maken van deze opdracht.

 * Klasse ``Pair`` is een klasse met twee attributen: ``left`` en ``right``; elk ``Pair``-object representeert een causaal verband tussen twee events. Als event ``a`` causaal geordend is voor event ``b`` (dus: ``a``&#8826;``b``), dan wordt dat gerepresenteerd met een ``Pair``-object waarvoor geldt: ``left.equals(a) && right.equals(b)``

 * Klasse ``week2.CausalOrder`` heeft een attribuut: ``pairs``. Elk ``CausalOrder``-object representeert een causale ordening als een verzameling van causale verbanden.

 * Methode ``CausalOrder`` (constructor) dient attribuut ``pairs`` te vullen met alle causale verbanden die afgeleid kunnen worden van ``sequence``, volgens de twee regels die besproken zijn tijdens Hoorcollege 1. Houdt hierbij de volgende aanvullende eis aan:
    * Als events ``a``, ``b``, en ``c`` plaatsvinden bij hetzelfde proces, bevat de causale ordening ``a``&#8826;``b``, en ``b``&#8826;``c``, maar **niet** ``a``&#8826;``c`` (omdat die laatste in principe kan worden afgeleid van de eerste twee door de [transitieve afsluiting](https://en.wikipedia.org/wiki/Transitive_closure) te berekenen; dit is **geen** onderdeel van deze opdracht).

 * Methode ``toComputation`` dient de unieke verzameling van **alle** executies (= lijst van events) op te leveren die enkel verschillen in de volgorde van concurrent events, op basis van de paren in de causale ordening, en op basis van parameter ``events`` (om precies te zijn: elke executie is een permutatie van events in ``events``). Performance is hierbij geen evaluatiecriterium; een recursieve brute-force-aanpak is toegestaan.

 * Begrip van de code onder de "horizontale lijn" in klasse ``week2.CausalOrder`` (onder methode ``toComputation``) is onnodig voor deze opdracht.

### 2c: Logische klokken

**De opdracht:** Implementeer methoden ``LamportsClock`` (constructor) en ``VectorClock`` (constructor) in klassen ``week2.LamportsClock`` en ``week2.VectorClock``.

**Voldoende/onvoldoende?** Alle unit-tests in klassen ``week2.LamportsClockTest`` en ``week2.VectorClockTest`` dienen te slagen.

**Aanwijzingen:**

 * Klassen ``week2.LamportsClock`` en ``week2.VectorClock`` zijn subklassen van abstracte klasse ``week2.LogicalClock``. Deze abstracte klasse heeft een attribuut: ``timestamps``. Dit attribuut houdt per event een klokwaarde bij. De klokwaarden zijn van type ``T``. In ``week2.LamportsClock`` is ``T`` geïnstantieerd met ``Integer`` (elke klokwaarde van Lamport's Clock is een getal); in ``week2.VectorClock`` is ``T`` geïnstantieerd met ``Map<Process, Integer>`` (elke klokwaarde van de Vector Clock is een getal voor elk proces; informeel noemt men dit een vector van getallen, maar in Java is dit het makkelijskt te representeren met een ``Map``).

  * Methoden ``LamportsClock`` (constructor) en ``VectorClock`` (constructor) dienen attribuut ``timestamps`` (in de superklasse) te vullen volgens de regels van Lamport's Clock en de Vector Clock. Gebruik hiervoor methoden uit de superklasse (in ieder geval ``addTimestamp``; optioneel, naar eigen inzicht, ``containsTimestamp`` en ``getTimestamp``).

 * Begrip van de code onder de "horizontale lijn" in klasse ``week2.VectorClock`` (onder de constructor) is onnodig voor deze opdracht.



## Opdracht 3: Snapshots (week 3-4)

### 3a: Chandy-Lamport

**De opdracht:** Implementeer methoden ``init`` en ``receive`` in klassen ``week34.ChandyLamportProcess``, ``week34.ChandyLamportInitiator``, en ``week34.ChandyLamportNonInitiator``, volgens het Chandy-Lamport-algoritme.

**Voldoende/onvoldoende?** Alle unit-tests in klasse ``week34.ChandyLamportProcessTest`` dienen te slagen.

**Aanwijzingen:**

 * Klasse ``week34.SnapshotProcess`` is een superklasse die processen representeert die zich gedragen volgens een snapshot-algoritme (Chandy-Lamport of Lai-Yang). De klasse heeft drie attributen: ``started`` (``true`` als het proces een lokale snapshot gestart is), ``finished`` (``true`` als het proces een lokale snapshot gefinisht is), en ``channelStates`` (voor elk kanaal ``c`` een lijst met berichten die op het moment van de lokale snapshot onderweg zijn langs ``c``). Deze attributen kunnen geïnspecteerd en gemanipuleerd worden middels de methoden van klasse ``week34.SnapshotProcess``.

 * Klassen ``week34.ChandyLamportProcess``, ``week34.ChandyLamportInitiator``, en ``week34.ChandyLamportNonInitiator`` zijn subklassen van ``week34.SnapshotProcess``. Strikt genomen is ``week34.ChandyLamportProcess`` redundant: het gedrag van een initiator en non-initiators in het Chandy-Lamport-algoritme kan prima gedefinieerd worden in methoden ``init`` en ``receive`` van klassen ``week34.ChandyLamportInitiator`` en ``week34.ChandyLamportNonInitiator``, zonder gebruik te maken van ``week34.ChandyLamportProcess``. Echter, om codeduplicatie te voorkomen, kan het handig zijn om gemeenschappelijk gedrag (tussen initiator en non-initiators) onder te brengen in ``week34.ChandyLamportProcess``. Hierdoor kan het gebeuren dat implementaties van ``init`` en/of ``receive`` in de subklassen *leeg* zijn (of alleen ``super.<methode>(...)`` aanroepen); dat is prima.

 * Methode ``receive`` van ``week34.ChandyLamportProcess``, ``week34.ChandyLamportInitiator``, en ``week34.ChandyLamportNonInitiator`` wordt *als het goed is* (denk aan robuustheid) aangeroepen met een ``ChandyLamportBasicMessage``-object of een ``ChandyLamportControlMessage``-object (= een *marker*).

 * Om een lokaal snapshot te starten/finishen, dient methode ``startSnapshot``/``finishSnapshot`` (gedefinieerd in klasse ``week34.SnapshotProcess`` en toegankelijk in de subklassen) te worden aangeroepen.

 * Lokale toestanden worden niet expliciet vastgelegd (neem aan dat dit onderdeel is van methode ``startSnapshot``). Om kanaaltoestanden vast te leggen, dient methode ``record`` (gedefinieerd in klasse ``week34.SnapshotProcess`` en toegankelijk in de subklassen) te worden aangeroepen.

 * In de beschrijving van het algoritme tijdens Hoorcollege 2 is een kunstmatige communicatie van de initiator naar zichzelf toegevoegd (om de beschrijving op de slide in te kunnen korten); negeer deze communicatie in de implementatie.

### 3b: Lai-Yang

**De opdracht:** Implementeer methoden ``init`` en ``receive`` in klassen ``week34.LaiYangProcess``, ``week34.LaiYangInitiator``, en ``week34.LaiYangNonInitiator``, volgens het Lai-Yang-algoritme.

**Voldoende/onvoldoende?** Alle unit-tests in klasse ``week34.LaiYangProcessTest`` dienen te slagen.

**Aanwijzingen:**

 * De eerste twee aanwijzingen bij deelopdracht **3a** zijn ook van toepassing op deze deelopdracht.

 * Methode ``receive`` van ``week34.LaiYangProcess``, ``week34.LaiYangInitiator``, en ``week34.LaiYangNonInitiator`` wordt *als het goed is* (denk aan robuustheid) aangeroepen met een ``LaiYangBasicMessage``-object of een ``LaiYangControlMessage``-object.

 * Neem voor de implementatie van elk proces aan dat hij nul basisberichten heeft verstuurd wanneer het algoritme begint.

 * De implementatie van het piggybacken van ``true`` of ``false`` op basisberichten valt buiten deze deelopdracht.

 * De laatste drie aanwijzingen bij deelopdracht **3a** zijn ook van toepassing op deze deelopdracht.



## Opdracht 4: Waves (week 5-6)

### 4a: Ring

**De opdracht:** Implementeer methoden ``init`` en ``receive`` in klassen ``week56.RingProcess``, ``week56.RingInitiator``, en ``week56.RingNonInitiator``, volgens het ring-algoritme.

**Voldoende/onvoldoende?** Alle unit-tests in klasse ``week56.RingProcessTest`` dienen te slagen.

**Aanwijzingen:**

 * Klasse ``week56.WaveProcess`` is een superklasse die processen representeert die zich gedragen volgens een wave-algoritme. De klasse heeft een attribuut: ``active`` (``true`` totdat het proces lokaal klaar is met het algoritme). Dit attribuut kan geïnspecteerd en gemanipuleerd worden met methoden ``isActive``, ``isPassive``, en ``done``.

 * Klassen ``week56.RingProcess``, ``week56.RingInitiator``, en ``week56.RingNonInitiator`` zijn subklassen van ``week56.WaveProcess``. Strikt genomen is ``week56.RingProcess`` redundant: het gedrag van een initiator en non-initiators in het ring-algoritme kan prima gedefinieerd worden in methoden ``init`` en ``receive`` van klassen ``week56.RingInitiator`` en ``week56.RingNonInitiator``, zonder gebruik te maken van ``week56.RingProcess``. Echter, om codeduplicatie te voorkomen, kan het handig zijn om gemeenschappelijk gedrag (tussen initiator en non-initiators) onder te brengen in ``week56.RingProcess``. Hierdoor kan het gebeuren dat implementaties van ``init`` en/of ``receive`` in de subklassen *leeg* zijn (of alleen ``super.<methode>(...)`` aanroepen); dat is prima.

 * Methode ``receive`` van ``week56.RingProcess``, ``week56.RingInitiator``, en ``week56.RingNonInitiator`` wordt *als het goed is* (denk aan robuustheid) aangeroepen met een ``TokenMessage``-object.

 * Om aan te geven dat een proces lokaal klaar is met het algoritme, dient methode ``done`` (gedefinieerd in klasse ``week56.WaveProcess)`` te worden aangeroepen op het juiste moment.

 * In de beschrijving van het algoritme tijdens Hoorcollege 3 is een kunstmatige communicatie van de initiator naar zichzelf toegevoegd (om de beschrijving op de slide in te kunnen korten); negeer deze communicatie in de implementatie.

### 4b: Tarry

**De opdracht:** Implementeer methoden ``init`` en ``receive`` in klassen ``week56.TarryProcess``, ``week56.TarryInitiator``, en ``week56.TarryNonInitiator``, volgens Tarry's algoritme.

**Voldoende/onvoldoende?** Alle unit-tests in klasse ``week56.TarryProcessTest`` dienen te slagen.

**Aanwijzingen:**

 * Alle aanwijzingen bij deelopdracht **4a** zijn ook van toepassing op deze deelopdracht.

### 4c: Dfs

**De opdracht:** Implementeer methoden ``init`` en ``receive`` in klassen ``week56.DepthFirstSearchProcess``, ``week56.DepthFirstSearchInitiator``, en ``week56.DepthFirstSearchNonInitiator``, volgens het dfs-algoritme.

**Voldoende/onvoldoende?** Alle unit-tests in klasse ``week56.DepthFirstSearchProcessTest`` dienen te slagen.

**Aanwijzingen:**

 * Alle aanwijzingen bij deelopdracht **4a** zijn ook van toepassing op deze deelopdracht.

### 4d: Dfs + extra piggybacking

**De opdracht:** Implementeer methoden ``init`` en ``receive`` in klassen ``week56.DepthFirstSearchExtraPiggybackProcess``, ``week56.DepthFirstSearchExtraPiggybackInitiator``, en ``week56.DepthFirstSearchExtraPiggybackNonInitiator``, volgens het dfs-algoritme + extra piggybacking.

**Voldoende/onvoldoende?** Alle unit-tests in klasse ``week56.DepthFirstSearchExtraPiggybackProcessTest`` dienen te slagen.

**Aanwijzingen:**

 * Alle aanwijzingen bij deelopdracht **4a** zijn ook van toepassing op deze deelopdracht.

 * Methode ``receive`` van ``week56.DepthFirstSearchExtraPiggybackProcess``, ``week56.DepthFirstSearchExtraPiggybackInitiator``, en ``week56.DepthFirstSearchExtraPiggybackNonInitiator`` wordt *als het goed is* (denk aan robuustheid) aangeroepen met een ``TokenWithIdsMessage``-object.

 * Gebruik procesnamen (methode ``getName()``, gedefinieerd in klasse ``framework.Process``) als ids (methode ``addId``, gedefinieerd in klasse ``week56.TokenWithIdsMessage``).

 ### 4e: Dfs + extra controleberichten

**De opdracht:** Implementeer methoden ``init`` en ``receive`` in klassen ``week56.DepthFirstSearchExtraControlProcess``, ``week56.DepthFirstSearchExtraControlInitiator``, en ``week56.DepthFirstSearchExtraControlNonInitiator``, volgens het dfs-algoritme + extra controleberichten.

**Voldoende/onvoldoende?** Alle unit-tests in klasse ``week56.DepthFirstSearchExtraControlProcessTest`` dienen te slagen.

**Aanwijzingen:**

 * Alle aanwijzingen bij deelopdracht **4a** zijn ook van toepassing op deze deelopdracht.

 * Methode ``receive`` van ``week56.DepthFirstSearchExtraControlProcess``, ``week56.DepthFirstSearchExtraControlInitiator``, en ``week56.DepthFirstSearchExtraControlNonInitiator`` wordt *als het goed is* (denk aan robuustheid) aangeroepen met een ``TokenMessage``-object, een ``InfoMessage``-object, of een ``AckMessage``-object.



## Opdracht 5: Deadlock-detectie (week 7-8)

**De opdracht:** Implementeer methoden ``init`` en ``receive`` in klassen ``week78.BrachaTouegProcess``, ``week78.BrachaTouegInitiator``, en ``week78.BrachaTouegNonInitiator``, volgens het Bracha-Toueg-algoritme.

**Voldoende/onvoldoende?** Alle unit-tests in klasse ``week78.BrachaTouegProcessTest`` dienen te slagen.

**Aanwijzingen:**

 * Klasse ``week78.DeadlockDetectionProcess`` is een superklasse die processen representeert die zich gedragen volgens een deadlock-detectie-algoritme. De klasse heeft drie attributen: ``inRequests`` (verzameling van kanalen waarlangs een verzoek ontvangen is); ``outRequests`` (verzameling van kanalen waarlangs een verzoek verstuurd is); ``requests`` (het aantal verstuurde verzoeken dat toegekend dient te worden). Deze attributen zijn ``protected``, dus ze kunnen direct geïnspecteerd en gemanipuleerd worden vanuit subklassen.

 * Klassen ``week78.BrachaTouegProcess``, ``week78.BrachaTouegInitiator``, en ``week78.BrachaTouegNonInitiator`` zijn subklassen van ``week78.DeadlockDetectionProcess``. Strikt genomen is ``week78.BrachaTouegProcess`` redundant: het gedrag van een initiator en non-initiators in het Bracha-Toueg-algoritme kan prima gedefinieerd worden in methoden ``init`` en ``receive`` van klassen ``week78.BrachaTouegInitiator`` en ``week78.BrachaTouegNonInitiator``, zonder gebruik te maken van ``week78.BrachaTouegProcess``. Echter, om codeduplicatie te voorkomen, kan het handig zijn om gemeenschappelijk gedrag (tussen initiator en non-initiators) onder te brengen in ``week78.BrachaTouegProcess``. Hierdoor kan het gebeuren dat implementaties van ``init`` en/of ``receive`` in de subklassen *leeg* zijn (of alleen ``super.<methode>(...)`` aanroepen); dat is prima.

 * Methode ``receive`` van ``week78.BrachaTouegProcess``, ``week78.BrachaTouegInitiator``, en ``week78.BrachaTouegNonInitiator`` wordt *als het goed is* (denk aan robuustheid) aangeroepen met een ``NotifyMessage``-object, een ``DoneMessage``-object, een ``GrantMessage``-object, of een ``AckMessage``-object.

 * De initiator dient, wanneer het algoritme termineert, ``true`` te printen als hij **geen** deadlock heeft gedetecteerd, en anders ``false`` (gebruik methode ``print`` van superklasse ``framework.Process``). 
 
 * In de beschrijving van het algoritme tijdens Hoorcollege 4 is een kunstmatige communicatie van de initiator naar zichzelf toegevoegd (om de beschrijving op de slide in te kunnen korten); negeer deze communicatie in de implementatie.

 * Zorg ervoor dat de implementatie ook om kan gaan met *N*-uit-*M*-verzoeken met 1<*N*<*M*. **NB:** Dit is niet besproken tijdens Hoorcollege 4 (en ook niet in het tekstboek); bedenk de generalisatie zelf (er zijn enkele unit-tests waarin deze situatie zich voordoet).



## Afronding

 Wanneer alle unit-tests van alle opdrachten slagen, kunnen de implementaties ingeleverd worden, volgens deze stappen:

 1. In de VM, open een terminal en voer uit:
    ```
    cd /home/ou
    tar -czf ib2302.tar.gz ib2302
    ```

 2. Op Brightspace, stuur de zojuist gemaakte ``/home/ou/ib2302.tar.gz`` in met het formulier onder ``Cursus`` > ``Inhoud`` > ``Programmeeropdrachten`` > ``Instructie``.
 
 3. Het reflectieverslag kan ingestuurd worden via Brighspace ``Cursus`` > ``Inhoud`` > ``Programmeeropdrachten`` > ``Reflectieverslag``.
