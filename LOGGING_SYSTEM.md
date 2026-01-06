# 📝 SYSTEM LOGOWANIA - AppLogger

## 🎯 Przegląd

Dodałem kompletny system logowania z możliwością oznaczania logów jako **BUSINESS** lub **PERFORMANCE**.

System automatycznie:
- ✅ Oznacza logi w zależności od typu
- ✅ Separuje logi do osobnych plików
- ✅ Śledzi correlation ID dla request tracking
- ✅ Loguje performance metrics automatycznie (AOP)

---

## 📦 Komponenty

### 1. AppLogger.java
Custom logger wrapper dla SLF4J/Logback

**Features**:
- `business()` - Logi biznesowe
- `performance()` - Metryki performance
- `security()` - Zdarzenia bezpieczeństwa
- Correlation ID tracking
- MDC (Mapped Diagnostic Context) support

### 2. logback.xml
Konfiguracja Logback z:
- Console appender
- File appender (main log)
- Odrębne pliki dla biznesu, performance, security
- Rolling policies (rozmiar + czas)
- Development i Production profile'y

### 3. LoggingAspect.java
AOP Aspect do automatycznego logowania:
- Automatyczne logowanie wejścia/wyjścia metod
- Performance tracking (duration)
- Exception logging
- Działa na service, controller, view klasach

### 4. MemoryMonitor.java
Monitorowanie pamięci z AppLogger:
- Memory usage logging (performance)
- GC activity logging
- Thread metrics
- CPU usage monitoring

---

## 🚀 Użycie

### Podstawowe Logowanie

```java
// Utwórz logger
AppLogger logger = AppLogger.getLogger(MyClass.class);

// Business logs
logger.business().info("User {} logged in successfully", username);
logger.business().warn("Transaction {} failed", txId);
logger.business().error("Order processing failed", exception);

// Performance logs
logger.performance().info("Query execution took {} ms", duration);
logger.performance().warn("Slow API response: {} ms", responseTime);

// Security logs
logger.security().warn("Failed login attempt from {}", ipAddress);

// Regular logs
logger.info("Application started");
logger.debug("Debug info");
logger.error("Critical error", exception);
```

### Correlation ID Tracking

```java
// Ustaw correlation ID na początek request'u
AppLogger.setCorrelationId(requestId);

// Wszystkie logi będą zawierać correlation ID
logger.business().info("Processing order");
// Output: [uuid-xxxx-xxxx] [BUSINESS] Processing order

// Na koniec request'u wyczyść
AppLogger.clearCorrelationId();
```

### Performance Timing

```java
AppLogger logger = AppLogger.getLogger(TransactionService.class);

long startTime = System.currentTimeMillis();
try {
    // ... perform operation ...
    long duration = System.currentTimeMillis() - startTime;
    logger.performance().logTiming("saveTransaction", duration, true);
} catch (Exception e) {
    long duration = System.currentTimeMillis() - startTime;
    logger.performance().logTiming("saveTransaction", duration, false);
}
```

### Query Performance Logging

```java
long startTime = System.currentTimeMillis();
List<Transaction> results = repository.findAll();
long duration = System.currentTimeMillis() - startTime;

logger.performance().logQuery(
    "SELECT * FROM transactions", 
    duration, 
    results.size()
);
// Output: [PERFORMANCE] [QUERY] SELECT * FROM transactions - 45 ms - 1000 rows
```

---

## 📁 Pliki Logów

```
logs/
├── lesnazrzutka.log                    # Główny log (wszystkie poziomy)
├── business.log                        # Wszystkie logi biznesowe
├── business-only.log                   # Logi ze znacznikiem [BUSINESS]
├── performance-only.log                # Logi ze znacznikiem [PERFORMANCE]
├── security.log                        # Logi ze znacznikiem [SECURITY]
└── memory.log                          # Logi memory monitoring'u

# Rotacja:
# - Nowy plik co dzień lub każde 10 MB
# - Przechowywanie przez 30 dni
# - Łącznie max 300 MB dla głównego logu
```

---

## 🎯 Log Levels

| Level | Kiedy użyć | Przykład |
|-------|-----------|----------|
| TRACE | Bardzo szczegółowe info | Wejście/wyjście z każdego bloku kodu |
| DEBUG | Debug info (dev only) | Wartości zmiennych, parametry |
| INFO | Istotne informacje | Logowanie użytkownika, procesy |
| WARN | Warningi, anomalie | Wolne zapytania, retries |
| ERROR | Błędy | Wyjątki, nieudane operacje |

---

## 📊 Log Format

### Console Output
```
2025-01-06 15:30:45.123 [main] INFO  TransactionService - [BUSINESS] User login successful
2025-01-06 15:30:46.456 [http-nio-8080-exec-1] INFO  MemoryMonitor - [PERFORMANCE] Memory Status: 150.5 MB / 512 MB (29%)
```

### File Output
```
2025-01-06 15:30:45.123 [uuid-1234-5678] [BUSINESS] - User transaction completed
2025-01-06 15:30:46.456 [uuid-1234-5678] [PERFORMANCE] - Query SELECT * FROM transactions - 45 ms - 1000 rows
```

---

## 🔍 MDC (Mapped Diagnostic Context)

MDC umożliwia śledzenie:
- Correlation ID - dla request tracking
- LOG_TYPE - typ logu (BUSINESS, PERFORMANCE, SECURITY)
- User - zalogowany użytkownik (do implementacji)
- Request ID - identyfikator request'u (do implementacji)

```java
// W logback.xml:
<pattern>
    %d{yyyy-MM-dd HH:mm:ss.SSS} [%X{CORRELATION_ID}] [%X{LOG_TYPE}] - %msg%n
</pattern>
```

---

## 🔄 AOP Aspect - Automatyczne Logowanie

LoggingAspect automatycznie loguje:

```java
@Around("execution(public * pl.ostropa.lesnazrzutka.service.*.*(..))")
public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
    // Loguje: method start, duration, result, exceptions
}
```

**Output**:
```
DEBUG  TransactionService - SERVICE.saveTransaction - START [args: {fromAccount=PL123, amount=1000.00}]
INFO   TransactionService - [PERFORMANCE] SERVICE.saveTransaction - 45 ms [✓]

# W razie błędu:
ERROR  TransactionService - SERVICE.saveTransaction - 45 ms [✗] ERROR: Null pointer exception
```

---

## ⚙️ Konfiguracja

### Development Mode
```
# application.properties
spring.profiles.active=dev

# Logback wyświetli:
- DEBUG level logi do konsoli
- Wszystkie logi do pliku
```

### Production Mode
```
# application.properties
spring.profiles.active=prod

# Logback wyświetli:
- INFO level logi do pliku
- Bez konsoli output'u (faster)
- Performance metrics zbierane
```

---

## 📈 Monitorowanie Performance

Dzięki AppLogger.performance() możesz:

```java
// 1. Logowanie czasu zapytania
logger.performance().logQuery("SELECT * FROM transactions", 45, 1000);

// 2. Logowanie timing'u operacji
logger.performance().logTiming("processOrder", 200, true);

// 3. Custom performance message
logger.performance().info("Cache hit rate: {}%", 85);
```

**Performance log file** (`performance-only.log`):
```
2025-01-06 15:30:46.456 - [QUERY] SELECT * FROM transactions - 45 ms - 1000 rows
2025-01-06 15:30:47.789 - [TIMING] processOrder - 200 ms ✓
2025-01-06 15:31:00.000 - Cache hit rate: 85%
```

---

## 🔐 Business Logging

Logi biznesowe w oddzielnym pliku:

```java
// Wszystkie zdarzenia biznesowe
logger.business().info("User {} completed order #{}", "john@example.com", 12345);
logger.business().warn("Large transaction detected: {} PLN", 50000);
logger.business().error("Payment processing failed for order #{}", 12345);
```

**Output** (`business-only.log`):
```
2025-01-06 15:30:45.123 - User john@example.com completed order #12345
2025-01-06 15:31:00.456 - Large transaction detected: 50000 PLN
2025-01-06 15:32:15.789 - Payment processing failed for order #12345
```

---

## 🛡️ Security Logging

Zdarzenia bezpieczeństwa:

```java
logger.security().info("User login: {}", username);
logger.security().warn("Failed login attempt from {}", ipAddress);
logger.security().error("Unauthorized access attempt: {}", resource);
```

**Output** (`security.log`):
```
2025-01-06 15:30:45.123 - User login: john@example.com
2025-01-06 15:31:00.456 - Failed login attempt from 192.168.1.100
2025-01-06 15:32:15.789 - Unauthorized access attempt: /api/admin
```

---

## 📚 Biblioteki

Dodane do `build.gradle`:
```gradle
implementation 'org.springframework.boot:spring-boot-starter-logging'
implementation 'org.slf4j:slf4j-api'
implementation 'ch.qos.logback:logback-classic'
implementation 'ch.qos.logback:logback-core'
implementation 'ch.qos.logback.contrib:logback-json-classic:0.1.5'
implementation 'ch.qos.logback.contrib:logback-jackson:0.1.5'
implementation 'com.fasterxml.jackson.core:jackson-databind'
```

---

## ✅ Checklist Implementacji

- [x] AppLogger.java - Custom logger wrapper
- [x] logback.xml - Konfiguracja
- [x] LoggingAspect.java - Automatic AOP logging
- [x] MemoryMonitor.java - Updated do AppLogger
- [x] build.gradle - Dependencies
- [x] Dokumentacja

---

## 🎓 Best Practices

1. **Zawsze używaj AppLogger zamiast SLF4J bezpośrednio**
   ```java
   // ✅ DOBRZE
   AppLogger logger = AppLogger.getLogger(MyClass.class);
   logger.business().info("User login");
   
   // ❌ ŹLE
   private static final Logger log = LoggerFactory.getLogger(MyClass.class);
   log.info("User login");
   ```

2. **Oznaczaj logi biznesowe i performance**
   ```java
   // ✅ DOBRZE
   logger.business().info("Transaction completed");
   logger.performance().info("Query took {} ms", duration);
   
   // ❌ ŹLE
   logger.info("Transaction completed");
   logger.info("Query took {} ms", duration);
   ```

3. **Ustaw correlation ID na początku request'u**
   ```java
   @Override
   protected void doFilterInternal(HttpServletRequest request, 
       HttpServletResponse response, FilterChain chain) {
       AppLogger.setCorrelationId(request.getHeader("X-Request-ID"));
       // ...
   }
   ```

4. **Nie loguj sensitive data**
   ```java
   // ❌ ŹLE - Password w logu
   logger.info("Login attempt: username={}, password={}", user, password);
   
   // ✅ DOBRZE
   logger.info("Login attempt: username={}", user);
   ```

---

## 📞 Support

Pytania o logowanie:
- Sprawdź dokumentację AppLogger.java
- Sprawdź logback.xml
- Czytaj logi w `logs/` katalogu
- Używaj MemoryMonitor do performance tracking

---

**System logowania jest gotowy do produkcji! 🚀**

Wszystkie logi będą automatycznie:
- ✅ Oznaczane jako BUSINESS/PERFORMANCE/SECURITY
- ✅ Separowane do osobnych plików
- ✅ Rotowane po rozmiarze/dacie
- ✅ Śledzene za correlation ID

---

*Data implementacji: 06.01.2025*
*Status: ✅ PRODUCTION READY*

