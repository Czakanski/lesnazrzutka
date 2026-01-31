# PRD – PropertyFlow: Aplikacja do zarządzania nieruchomościami (Resident-First)

## 1. Informacje ogólne

### Nazwa produktu
**PropertyFlow** (resident-oriented property management platform)

### Typ produktu
SaaS (web + mobile)

### Rynek docelowy
- **Geograficznie:** Polska (z możliwością ekspansji EU)
- **Primery segment:** Zarządcy nieruchomości, wspólnoty mieszkaniowe
- **Secondary:** PRS/najem instytucjonalny, nowe inwestycje deweloperskie

### Segmenty klientów
1. **Zarządcy nieruchomości** (małe i średnie firmy: 50–2000 lokali)
2. **Wspólnoty mieszkaniowe** (zarządy, syndycy)
3. **Inwestorzy / PRS** (najem instytucjonalny)
4. **Deweloperzy** (nowe inwestycje)

---

## 2. Problem do rozwiązania

### Aktualny stan rynku
- Systemy skupione na księgowości, nie na User Experience mieszkańca
- Słaby UX aplikacji moblinej (lub jej brak)
- Chaos komunikacyjny (e-mail, telefony, WhatsApp)
- Brak automatyzacji procesów
- Brak przejrzystości dla zarządu wspólnoty

### Główne bóle użytkowników

**Mieszkańcy:**
- "Nie wiem, ile mam zapłacić / czy moje zgłoszenie działa"
- Brak możliwości szybkiego sprawdzenia salda
- Trudne zgłaszanie usterek (telefon, wizyta)

**Zarządcy:**
- Ciągle te same pytania od mieszkańców
- Ręczna obsługa usterek
- Brak kontroli nad procesami
- Migracja z legacy systemów to koszmar

**Zarządy wspólnot:**
- Brak przejrzystości finansowej
- Trudno generować raporty
- Słaba komunikacja z mieszkańcami

---

## 3. Wizja produktu

### Deklaracja wizji
Nowoczesna platforma do zarządzania nieruchomościami, która:
- **Automatyzuje pracę zarządcy** (księgowość, procesy, obsługa)
- **Daje mieszkańcom aplikację prostą jak bank** (sprawdzenie salda w 3 sekundy)
- **Eliminuje chaos komunikacyjny** (push notifications, komunikaty w aplikacji)
- **Zapewnia przejrzystość** dla zarządu wspólnoty

### Kluczowe założenia
1. **Resident-first** — aplikacja mieszkańca jest prioritetem #1
2. **Mobile-first** — najpierw aplikacja na telefon, potem web
3. **Automatyzacja zamiast ręcznej obsługi**
4. **Księgowość jako moduł, nie centrum systemu**
5. **API-first architecture** — serwisy komunikują się przez REST/gRPC i zdarzenia

---

## 4. Cele produktu (MVP)

### Cele biznesowe
1. **Onboarding zarządcy < 7 dni** (bez migracji danych)
2. **Redukcja kontaktów mieszkańców z zarządcą o ≥50%** (poprzez aplikację)
3. **SaaS model:** €2–5 per lokal/miesiąc
4. **First paying customer w ciągu 6 miesięcy**

### Cele produktowe (User-facing)
1. **Mieszkaniec w 3 sekundy widzi swój status**
2. **Zgłoszenie usterki w < 30 sekund** (3 kliknięcia)
3. **Brak telefonów "czy zapłaciłem?"** (aplikacja jest źródłem prawdy)
4. **Zarządca przegląduje todas usterkami w jednym panelu**

---

## 5. Persony (User Personas)

### 🧍 Persona 1: Mieszkaniec (Resident)

**Demographics:**
- Wiek: 25–65 lat
- Tech-savvy: Od średniego do wysokiego

**Goals:**
- Szybko sprawdzić, ile ma zapłacić
- Zgłosić usterkę bez telefonowania
- Dostać informację o remoncie/ogłoszeniu
- Widzieć historię rozliczeń

**Pain Points:**
- Nie wie, ile zapłacić / kiedy
- Ignoruje maile od zarządcy
- Trudno się dodzwonić
- Brak potwierdzenia, że zgłoszenie usterki zostało przyjęte

**Needs:**
- Push notifications o przypomnieniach
- Status zgłoszenia w realtime
- Jasne wyjaśnienie każdej opłaty
- Dokładne informacje o terminie płatności

---

### 🧑‍💼 Persona 2: Zarządca (Property Manager)

**Demographics:**
- Wiek: 35–60 lat
- Obsługuje: 50–2000 lokali
- Tech-savvy: Średni (zależy od firmy)

**Goals:**
- Zmniejszyć ilość telefonów i maili
- Automatycznie naliczać opłaty
- Śledzić usterki i przypisywać wykonawcom
- Wystawić raport dla zarządu

**Pain Points:**
- Ręczne przenoszenie danych między systemami
- Niemożliwość łatwo exportowania raportu
- Czasami mieszkaniec "nie wie" że ma zaległ
- Brak łatwego sposobu na kontakt zbiorowy

**Needs:**
- Automatyczne naliczanie i wysyłanie przypomnień
- Panel do śledzenia usterek per lokal
- Łatwy export raportu finansowego
- Możliwość wysłania komunikatu do grupy lokali

---

### 🧑‍⚖️ Persona 3: Zarząd Wspólnoty (Board of Residents)

**Demographics:**
- Wiek: 45–70 lat (zwykle starsi)
- Tech-savvy: Niski–średni
- Dostęp: Czasami (raz na miesiąc)

**Goals:**
- Wiedzieć, ile jest zaległ
- Mieć raport finansowy do spotkania
- Mieć dostęp do dokumentów (umowy, decyzje)
- Komunikować się z mieszkańcami (ogłoszenia)

**Pain Points:**
- Trudno dostać raport od zarządcy
- Mieszkańcy "nie wiedzą" o zmianach
- Brak centralnego archiwum dokumentów
- Chaos w komunikacji

**Needs:**
- Dashboard z kluczowymi metrykami (zaległ, wpływy, przychody)
- Raporty generowane automatycznie
- Repozytorium dokumentów z wersjonowaniem
- Możliwość wysłania komunikatu do wszystkich

---

## 6. Zakres funkcjonalny – MVP

### 6.1 Aplikacja mieszkańca (Mobile + Web)

#### Ekrany/Sekcje:
1. **Dashboard**
   - Saldo lokalu (status: OK / zaległ)
   - Pinned komunikaty od zarządcy
   - Status ostatnich 3 zgłoszeń usterek
   - Card "Należy zapłacić do XX.XX.20XX"

2. **Opłaty i rozliczenia**
   - Lista opłat za ostatnie 12 miesięcy
   - Kwota, termin, status (opłacono/zaległ/przychód)
   - Możliwość filtracji po roku/miesiacu
   - Eksport do PDF

3. **Historia rozliczeń**
   - Szczegóły każdej opłaty (podatek, opłata zarządcy, media, itp.)
   - Możliwość zobaczenia faktury (jeśli dostępna)
   - Porównanie rok do roku

4. **Zgłoszenia usterek**
   - Formularz: kategoria (hydraulika / elektryka / ogólne), opis, foto (max 5)
   - Status: nowe / w trakcie / zamknięte
   - Komentarze (mieszkaniec + wykonawca)
   - Powiadomienia push przy zmianie statusu

5. **Ogłoszenia/Komunikaty**
   - Wiadomości od zarządcy (ważne zmiany, harmonogram remontów)
   - Filtrable po kategorii (remonty / komunikaty / zarządzenie)
   - Możliwość wciśnięcia "przeczytane"

6. **Dokumenty**
   - Umowa mieszkania, regulamin, decyzje zarządu
   - Pobieranie i podgląd
   - Archiwum (ostatnie 3 lata)

7. **Profil użytkownika**
   - Dane kontaktowe, numer lokalu
   - Zmiana hasła / 2FA
   - Preferencje powiadomień

8. **Powiadomienia Push**
   - Przypomnienie o płatności (7 dni przed terminem)
   - Zmiana statusu zgłoszenia
   - Nowe komunikaty od zarządcy
   - Ustawialne preferencje (on/off dla każdego typu)

#### Out of scope MVP:
- Wykresy/analytics dla mieszkańca
- E-głosowania
- Rozliczenia za wynajęcie pokoju / podlokator

---

### 6.2 Panel zarządcy (Web + Desktop)

#### Sekcje:
1. **Dashboard**
   - Kluczowe metryki (liczba lokali, wpływy/miesiąc, zaległ, liczba usterek)
   - Graf wpływów za ostatnie 12 miesięcy
   - Top zaległ (mieszkańcy z największym zaległością)

2. **Zarządzanie budynkami/lokalami**
   - Lista budynków
   - Dla każdego budynku: lista lokali, właściciele, kontakt
   - Możliwość importu CSV
   - Edycja danych lokalu

3. **Kartoteka mieszkańców**
   - Lista mieszkańców per budynek
   - Dane kontaktowe, info o lokalu
   - Historia rozliczeń (quick view)
   - Status aktywności (aktywny / zaległ / zamorożony)

4. **Naliczanie opłat**
   - Szablony opłat (wynajem, czynsz, opłata zarządcy, media)
   - Automatyczne naliczanie cykliczne (co miesiąc)
   - Edycja opłaty dla indywidualnych lokali
   - Historia zmian

5. **Import przelewów**
   - Upload pliku CSV / MT940 z banku
   - Automatyczne dopasowanie wpłat do lokali
   - Manual matching dla wątpliwych przypadków
   - Historia importów

6. **Saldo i zaległości**
   - Widok sald per lokal
   - Raport zaległości (wiek zaległości)
   - Filtracja po statusie
   - Export do Excel

7. **Zgłoszenia usterek (Ticketing)**
   - Lista usterek per lokal / per kategoria
   - Statusy: nowe / zaakceptowane / w trakcie / zamknięte
   - Przpisanie do wykonawcy
   - Komentarze i historia
   - Możliwość uploadowania foto

8. **Harmonogram przeglądów**
   - Przychodzące przeglądy techniczne
   - Powiadomienia do mieszkańców
   - Historia przeglądów

9. **Komunikaty do mieszkańców**
   - Drafting komunikatu
   - Wybór odbiorców (wszystkie / per budynek / per lokal)
   - Historia wysyłanych komunikatów

10. **Raporty podstawowe**
    - Raport finansowy (wpływy, wydatki, saldo)
    - Raport zaległości (wiek, kwota)
    - Raport usterek (liczba, kategoria, czas rozwiązania)
    - Export do PDF/Excel

---

### 6.3 Finanse i płatności

#### Funkcjonalność:
1. **Cykliczne naliczanie opłat**
   - Szablony opłat (wynajem, opłata zarządcy, media)
   - Automatyczne naliczanie każdego miesiąca
   - Możliwość wznowienia/zawieszenia dla konkretnego lokalu
   - Obsługa rabatów i refund

2. **Import przelewów**
   - Integracja z bankiem (CSV / MT940 / PSD2 - future)
   - Automatyczne dopasowanie (bank match engine)
   - Manual matching dla ambuigius cases
   - Transakcje zavisłe (pending)

3. **Automatyczne dopasowanie wpłat**
   - Algorytm fuzzy match (lokal + kwota + data)
   - Możliwość ręcznego dopasowania
   - Raporty z niedopasowanymi wpłatami

4. **Saldo lokalu**
   - Saldo bieżące (wpływy - wydatki)
   - Historia transakcji
   - Projekcja salda na najbliższe 3 miesiące

5. **Nadpłaty / niedopłaty**
   - Automatyczne przeniesienie nadpłaty na nastepny miesiąc
   - Alerty o niedopłatach
   - Procedura zwrotu nadpłaty (manual approval)

6. **Windykacja miękka (MVP - basic)**
   - Automatyczne reminder email/SMS (7 dni przed terminem)
   - Status "zaległość" w aplikacji
   - Report zaległości dla zarządcy
   - (Post-MVP: twardsza windykacja, listy do komornika)

#### Out of scope MVP:
- Integracja z systemami księgowymi (Enova, Symfonia) — post-MVP
- E-faktury / VAT reporting — post-MVP
- Rozliczenie podatków — post-MVP

---

### 6.4 Obsługa techniczna (Maintenance)

#### Funkcjonalność:
1. **Zgłoszenia usterek**
   - Mieszkaniec wysyła: kategoria, opis, foto (max 5)
   - Zachowywane w bazie danych z timestamp
   - Przypisanie numeru ID (dla referenca)

2. **Statusy usterek**
   - **Nowe** - właśnie przesłane
   - **Zaakceptowane** - zarządca potwierdził
   - **W trakcie** - wykonawca pracuje
   - **Zamknięte** - rozwiązane
   - **Odrzucone** - out of scope / fake
   - Timeline widoczna dla mieszkańca

3. **Komentarze**
   - Mieszkaniec może komentować
   - Zarządca / wykonawca mogą dodawać notatki (wewnętrzne)
   - Powiadomienia push o nowych komentarzach

4. **Historia usterek per lokal**
   - Archiwum zamkniętych usterek
   - Statistyka (liczba / czas rozwiązania)
   - Powtarzające się problemy (alert dla zarządcy)

#### Out of scope MVP:
- Koszty usterek (szacunek / faktura)
- Scheduling (umówienie terminu)
- Ocena wykonawcy (rating)

---

### 6.5 Dokumenty

#### Funkcjonalność:
1. **Repozytorium dokumentów**
   - Przechowywanie dokumentów (umowy, decyzje zarządu, regulaminy)
   - Organizacja per budynek / lokal
   - Full-text search (future)

2. **Przypisanie dokumentów**
   - Do budynku (widoczne dla wszystkich lokali w budynku)
   - Do konkretnego lokalu (widoczne tylko dla tego lokalu)
   - Do zarządu wspólnoty (private)

3. **Podgląd i pobieranie**
   - Preview PDF/images
   - Download do urządzenia
   - Udostępnianie linkiem (future)

4. **Wersjonowanie (basic)**
   - Historia zmian dokumentu (kto, kiedy)
   - Możliwość przywrócenia starszej wersji (admin only)

5. **OCR dla faktur (post-MVP emphasis)**
   - Automatyczne skanowanie faktury PDF
   - Extracting: numer faktury, data, kwota, dostawca
   - Matchowanie z płatościami

#### Out of scope MVP:
- E-podpisy
- DMS (advanced document management)

---

## 7. Wymagania niefunkcjonalne (NFRs)

### Architektura
- **API-first design:** Wszystkie funkcjonalności dostępne przez REST API
- **Multi-tenant SaaS:** Każdy tenant (wspólnota) ma własne dane, bez cross-tenant leakage
- **Monorepo:** 11 mikroserwisów + 3 fronteny w pojedynczym repozytorium
- **Modularna architektura backendu:** Każdy serwis ma własną bazę danych (schemat PostgreSQL)
- **Event-driven:** Asynchroniczne procesy (Kafka/RabbitMQ dla zdarzeń domeny)
- **RWD + Mobile-first:** Responsive web + native mobile apps (React Native / Flutter)

### Bezpieczeństwo
- **RBAC (Role-Based Access Control):**
  - Admin (systemowy)
  - Tenant Admin (zarządca wspólnoty)
  - Property Manager (pracownik zarządcy)
  - Resident
  - Board Member (zarząd wspólnoty)
  
- **Authentication & Authorization:**
  - JWT tokens (accessToken + refreshToken)
  - OAuth 2.0 (future: Google, Facebook login)
  - 2FA (SMS / TOTP) - optional w MVP
  
- **Logi audytowe:**
  - Wszystkie operacje krytyczne logowane (create, update, delete)
  - Timestamp + user ID + IP address
  - 90-dniowe retencja

- **RODO compliance:**
  - Anonimizacja danych przy usunięciu konta
  - GDPR export (data portability)
  - Right to be forgotten (delete all tenant data)
  - Privacy policy + cookie consent

### Wydajność
- **Response time:** API < 300ms (p95)
- **Obsługa:** ≥10 000 lokali w MVP (skalowalne do 1M)
- **Caching:**
  - Redis dla session / user preferences
  - CDN dla static assets
  - Database query caching (Hibernate 2nd-level cache)

- **Database:**
  - PostgreSQL (per-service database pattern)
  - Migrations zarządzane Liquibase
  - Indexes na frequently queried columns
  - Connection pooling (HikariCP)

### Dostępność
- **Uptime:** 99.5% SLA (post-MVP: 99.9%)
- **Graceful degradation:** Jeśli jeden serwis nie działa, inne dalej działają
- **Circuit breaker pattern** dla inter-service communication

### Observability
- **Logging:** Centralized logs (ELK stack / CloudWatch)
- **Monitoring:** Prometheus metrics + Grafana dashboards
- **Tracing:** Distributed tracing (Jaeger / DataDog)
- **Alerting:** Slack / PagerDuty dla critical issues

---

## 8. Integracje

### MVP
1. **Import przelewów bankowych** (CSV / MT940)
2. **Email + Push notifications** (via Firebase Cloud Messaging / APNs)
3. **Mailer:** SMTP (Gmail / SendGrid)

### Post-MVP (3–6 miesięcy)
1. **PSD2** (Open Banking API - dostęp do konta mieszkańca dla automatycznych wpłat)
2. **Systemy księgowe** (Enova, Symfonia, MSSQL export)
3. **E-płatności** (BLIK, Przelewy24, PayPal)
4. **Liczniki IoT** (Shelly, Sonoff dla automatycznego czytania mediów)
5. **SMS Gateway** (SMS Lab / Plus SMS)

### Future (6–12 miesięcy+)
1. **Video call integration** (Jitsi / Twilio dla konsultacji)
2. **Chatbot AI** (OpenAI dla FAQ odpowiedzi)
3. **Integracja z CRM** (Pipedrive / HubSpot)

---

## 9. Metryki sukcesu (KPIs)

### Metryki produktowe
1. **MAU (Monthly Active Users):** Rezydenci logujący się min. 1x/miesiąc
   - Target MVP: 1000 MAU (100 wspólnot x 10 av. residenti)
   
2. **% zgłoszeń przez aplikację**
   - Baseline: 0% (wszyscy telefonują)
   - Target MVP: >50%
   - Target: >80%

3. **Czas zamknięcia zgłoszenia**
   - Baseline: 5–7 dni (czy telefon, czy papier)
   - Target: 2–3 dni (automatyczne routing)

4. **Adoption rate zarządcy**
   - % funkcjonalności używanych
   - Target: >60% funkcji używane aktywnie

### Metryki biznesowe
1. **Churn zarządców** (monthly)
   - Target: <5% MRR churn
   
2. **Koszt onboardingu**
   - Target: < €200 per tenant (bez customizacji)
   
3. **Przychód per lokal**
   - Model: €2–5 per lokal/miesiąc
   - Target: €50k MRR w ciągu 12 miesięcy

4. **NPS (Net Promoter Score)**
   - Target: >50 (good SaaS = >40)

---

## 10. Ryzyka i mitygacja

| Ryzyko | Wpływ | Mitygacja |
|--------|--------|-----------|
| Opór zarządców przed zmianą systemu | WYSOKI | Prosty onboarding, wsparcie on-site, free trial, case studies |
| Migracja danych z legacy systemów | WYSOKI | Dedykowany tool do importu, CSM support, phased rollout |
| Zbyt szeroki zakres MVP | WYSOKI | Strict MVP definition, no "feature creep", prioritized backlog |
| Bezpieczeństwo / GDPR breach | KRYTYCZNE | Penetration testing, SOC 2 audit, data encryption, GDPR lawyer |
| Technical debt z architektury monorepo | ŚREDNI | Code reviews, modular design, migration strategy dla legacy |
| Skalowanie (10k -> 100k lokali) | ŚREDNI | Database sharding, microservices, event sourcing dla critical paths |

---

## 11. Roadmapa (4-Faza)

### Faza 1: MVP (0–3 miesiące) ✅ **START HERE**
**Deliverables:**
- ✅ API Gateway + 4 core services (Resident, Accounting, Maintenance, Notification)
- ✅ Mobile app (React Native) - login, dashboard, payments, tickets
- ✅ Web portal (React) - admin panel, import przelewów, ticketing
- ✅ PostgreSQL schemas + Liquibase migrations
- ✅ Email + push notifications
- ✅ Basic RBAC (Resident, Manager, Admin)
- ✅ Docker Compose setup
- ✅ Jest CI/CD (GitHub Actions)

**Metrics:**
- Time to market < 12 weeks
- Boarding 5 test customers
- 1000 test residents

---

### Faza 2: Early Adoption (3–6 miesięcy)
**Deliverables:**
- 7 remaining microservices (Community, Property, Document, Analytics, Integration, Audit, Payment)
- Advanced dashboards + reporting
- Automation workflows (cron-based)
- Windykacja (soft + hard)
- Integracje z bankami (PSD2 pilot)
- Elasticsearch + analytics

**Metrics:**
- 20 paid customers
- 10k active residents
- >50% adoption rate

---

### Faza 3: Scale (6–12 miesięcy)
**Deliverables:**
- Multi-currency support
- Advanced analytics (predykcja zaległości, churn)
- E-voting module
- Mobile push campaigns
- Integracja z kontrahentami (system ticketingu)
- Kubernetes deployment

**Metrics:**
- 100 paid customers
- €50k MRR
- <5% churn

---

### Faza 4: Enterprise (12–24 miesiące)
**Deliverables:**
- White-label solution
- API marketplace
- Advanced integrations (ERP, CRM)
- AI/ML features (anomaly detection, predictive maintenance)
- International expansion

---

## 12. Definicja sukcesu MVP

### ✅ Sukces to gdy:

**Dla mieszkańca:**
- "Czekaj, mam aplikację? Mogę zobaczyć moje saldo w sekunde? To jest świetne!"
- Rezygnacja z dzwoniania do zarządcy dla zapytań o saldo

**Dla zarządcy:**
- "Już nie dostaję 50 maili dziennie od mieszkańców pytających 'ile mam zapłacić?'"
- "Panel jest intuicyjny i nie potrzebuję 3-dniowego szkolenia"
- "Migracja z mojego starego systemu zajęła 2 dni, a data flowed automatically"

**Dla zarządu wspólnoty:**
- "Wreszcie mam raport finansowy w jeden klik, zamiast prosić zarządcę na dwa dni wcześniej"

**Dla biznsu:**
- >50% adoption rate wśród early users
- <3 dniowy onboarding
- First €100k ARR w ciągu 6 miesięcy

---

## 13. Technology Stack

### Backend
- **Framework:** Spring Boot 3.x
- **Database:** PostgreSQL (per-service schema pattern)
- **Cache:** Redis
- **Event Streaming:** RabbitMQ / Kafka (post-MVP)
- **API Gateway:** Spring Cloud Gateway
- **Build:** Gradle (monorepo)

### Frontend
- **Web:** React 18 + TypeScript + TailwindCSS
- **Mobile:** React Native / Flutter (TBD based on team)
- **State Management:** Redux / Zustand
- **HTTP Client:** Axios / Tanstack Query

### DevOps
- **Containerization:** Docker
- **Orchestration:** Docker Compose (dev), Kubernetes (prod - post-MVP)
- **CI/CD:** GitHub Actions
- **Monitoring:** Prometheus + Grafana
- **Logging:** ELK Stack

### Third-party
- **Payments:** Stripe / Przelewy24
- **SMS:** SMS Lab / Plus SMS
- **Email:** SendGrid / AWS SES
- **Push notifications:** Firebase Cloud Messaging

---

## Kolejne sensowne kroki

1. **📋 User Stories + Acceptance Criteria** — Break down do 2-tygodniowych sprintów
2. **🏗️ System Design Document** — API contracts, database schemas, event flows
3. **🧪 Test Strategy** — Unit, integration, E2E, performance tests
4. **🧑‍💻 Backend Technical Roadmap** — 11 serwisów, zależności, migration path
5. **📱 Frontend Technical Roadmap** — Komponent library, design system, mobile stack decision
6. **💰 Pricing Model + Financial Projections** — Unit economics, CAC, LTV, break-even
7. **🚀 Go-to-Market Strategy** — Sales motion, partnership channels, customer acquisition

---

**Status:** ✅ PRD Complete
**Wersja:** 1.0
**Ostatnia aktualizacja:** 2026-01-26
**Autor:** GitHub Copilot
