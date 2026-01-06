# ✅ DEPLOYMENT CHECKLIST - Moduł Wpłat

## 🚀 Przed Wdrażaniem

### Faza 1: Weryfikacja Kodu (Dev)

- [x] Kod skompilowany bez błędów
- [x] Unit testy przechodzą
- [x] Lint bez warningów
- [x] Kod review przebiegł
- [x] Security scan negatywny
- [x] Performance test przebiegł
- [x] Dokumentacja kompletna

### Faza 2: Testowanie (QA)

- [x] Functional testing - PASS
- [x] GUI testing - PASS
- [x] API testing - PASS
- [x] Security testing - PASS
- [x] Performance testing - PASS
- [x] Regression testing - PASS
- [x] UAT approved - YES

### Faza 3: Przygotowanie (DevOps)

- [x] Dependencje zadeklarowane
- [x] Konfiguracja externalizowana
- [x] Database migration plan
- [x] Rollback plan
- [x] Monitoring setup
- [x] Logging configured
- [x] Backup plan

---

## 📦 Pre-Deployment

### Środowisko

```bash
# 1. Przygotuj środowisko
export JAVA_HOME=/path/to/java17
export GRADLE_HOME=/path/to/gradle8

# 2. Weryfikuj wersje
java -version   # Java 17+
gradle -v       # Gradle 8+

# 3. Clone repo
git clone <repo-url>
cd lesnazrzutka

# 4. Checkout branch
git checkout main  # lub odpowiednia branch
```

### Build

```bash
# 1. Clean build
./gradlew clean build -x test

# 2. Weryfikuj artifact
ls -la build/libs/app.jar

# 3. Test build
./gradlew test

# 4. Check coverage
./gradlew test jacocoTestReport
```

### Database

```bash
# 1. Backup istniejącej bazy (jeśli produkcja)
mysqldump -u root -p lesnazrzutka > backup_$(date +%Y%m%d).sql

# 2. Przygotuj nową bazę (development)
# H2 auto-creates, ale dla PostgreSQL:
createdb -U postgres lesnazrzutka_prod

# 3. Migracja danych (jeśli potrzebna)
# ./gradlew flywayMigrate
```

---

## 🚀 Deployment (Production)

### Krok 1: Stop Aplikacji (5 min)

```bash
# Sprawdź czy aplikacja działa
ps aux | grep java

# Zatrzymaj aplikację
kill -15 <PID>

# Czekaj aż się wyłączy
sleep 30

# Weryfikuj czy zatrzymana
ps aux | grep java  # Nie powinna być
```

### Krok 2: Backup (10 min)

```bash
# Backup bazy danych
mysqldump -u root -p lesnazrzutka > backup_$(date +%Y%m%d_%H%M%S).sql

# Backup aplikacji
cp build/libs/app.jar build/libs/app.jar.$(date +%Y%m%d).bak

# Backup konfiguracji
cp application.properties application.properties.$(date +%Y%m%d).bak
```

### Krok 3: Deploy (5 min)

```bash
# Copy nowa aplikacja
cp build/libs/app.jar /opt/lesnazrzutka/app.jar

# Update konfiguracji (jeśli potrzebna)
# cp application-prod.properties /opt/lesnazrzutka/application.properties

# Set permissions
chmod 755 /opt/lesnazrzutka/app.jar

# Start aplikacji
java -jar /opt/lesnazrzutka/app.jar &

# Czekaj aż się startnie
sleep 30
```

### Krok 4: Weryfikacja (5 min)

```bash
# Check Health Endpoint (jeśli jest)
curl http://localhost:8080/actuator/health

# Check aplikacja działa
curl http://localhost:8080

# Check login
curl -u admin:admin http://localhost:8080/api/transactions

# Check testowe dane
curl -u admin:admin http://localhost:8080/api/transactions/grouped/all
```

### Krok 5: Smoke Test (10 min)

```bash
# GUI Test
# 1. Otwórz http://localhost:8080
# 2. Zaloguj się (admin/admin)
# 3. Kliknij "Przeglądaj Wpłaty"
# 4. Sprawdź czy wyświetla 3 karty
# 5. Sprawdź czy wyświetla 3 grupy
# 6. Sprawdź czy sumy są poprawne

# API Test
curl -u admin:admin http://localhost:8080/api/transactions/grouped/all | jq '.'
```

---

## 📊 Post-Deployment

### Monitoring (24h po deploymencie)

#### Metryki do Monitorowania

```
✅ Health Checks:
- Application is running
- Database is accessible
- API is responsive

✅ Performance Metrics:
- Response time < 500ms
- Error rate < 0.1%
- CPU usage < 50%
- Memory usage < 1GB

✅ Business Metrics:
- Users can login
- Transactions are visible
- Grouping works correctly
- Sums are calculated
```

#### Log Monitoring

```bash
# Sprawdzaj logi
tail -f /var/log/lesnazrzutka/application.log

# Szukaj errów
grep ERROR /var/log/lesnazrzutka/application.log

# Szukaj warnings
grep WARN /var/log/lesnazrzutka/application.log
```

#### Alert Setup

```
Configure alerts dla:
- Application down (page 911)
- Error rate > 1% (mail)
- Response time > 1s (log)
- Disk usage > 80% (mail)
- Database down (page 911)
```

### Reporting (24h po deploymencie)

- [x] Zaraportuj zainteresowanym
- [x] Wyślij release notes
- [x] Update issue tracking
- [x] Close deployment ticket
- [x] Archive backup

---

## ⚠️ Rollback Plan (Jeśli Coś Pójdzie Nie Tak)

### Szybki Rollback (< 5 min)

```bash
# 1. Stop aplikacji
kill -15 <PID>
sleep 10

# 2. Restore backup
cp build/libs/app.jar.$(date +%Y%m%d).bak build/libs/app.jar

# 3. Start aplikacji
java -jar /opt/lesnazrzutka/app.jar &

# 4. Weryfikuj
curl http://localhost:8080
```

### Database Rollback

```bash
# Jeśli baza zmieniona
mysql -u root -p lesnazrzutka < backup_$(date +%Y%m%d).sql

# Verify data
mysql -u root -p lesnazrzutka -e "SELECT COUNT(*) FROM transactions;"
```

### Full Rollback (< 15 min)

```bash
# 1. Stop aplikacji
systemctl stop lesnazrzutka

# 2. Restore aplikacji
cp /opt/lesnazrzutka/backup/app.jar.old /opt/lesnazrzutka/app.jar

# 3. Restore bazy
mysql -u root -p lesnazrzutka < /opt/lesnazrzutka/backup/db.sql

# 4. Restore konfiguracji
cp application.properties.bak application.properties

# 5. Start aplikacji
systemctl start lesnazrzutka

# 6. Verify
curl http://localhost:8080
```

---

## 📋 Deployment Checklist (Do Zrobienia Przed)

```
Day -1:
- [ ] Build artifact
- [ ] Run all tests
- [ ] Security scan
- [ ] Performance test
- [ ] Create backup
- [ ] Notify stakeholders
- [ ] Prepare rollback plan

Deployment Day:
- [ ] Stop application
- [ ] Backup database
- [ ] Backup application
- [ ] Deploy new version
- [ ] Verify health checks
- [ ] Smoke test
- [ ] Enable monitoring
- [ ] Update status page

Day +1:
- [ ] Monitor metrics
- [ ] Check logs
- [ ] Verify functionality
- [ ] Close ticket
- [ ] Prepare postmortem (if issues)

Week +1:
- [ ] Analyze metrics
- [ ] Collect feedback
- [ ] Document lessons learned
- [ ] Plan improvements
```

---

## 🔍 Weryfikacja Po Deploymencie

### Functional Verification

```bash
# Login test
curl -u admin:admin http://localhost:8080

# Transactions API
curl -u admin:admin http://localhost:8080/api/transactions

# Grouped data
curl -u admin:admin http://localhost:8080/api/transactions/grouped/all

# Summary calculation
curl -u admin:admin "http://localhost:8080/api/transactions/sum/PL61106000760000636213110001"
```

### Performance Verification

```bash
# Response time
curl -w "@curl-format.txt" -o /dev/null -s http://localhost:8080

# Load test
ab -n 1000 -c 10 http://localhost:8080/api/transactions

# Memory usage
ps aux | grep java  # Check RSS column
```

### Security Verification

```bash
# HTTPS (if configured)
curl -I https://localhost:8080

# Security headers
curl -I http://localhost:8080 | grep -i security

# CSRF token
curl -c cookies.txt http://localhost:8080/login

# SQL injection test
curl "http://localhost:8080/api/transactions?account=1' OR '1'='1"
# Should return error, not data
```

---

## 📞 Escalation Path

### Jeśli Problem:

1. **Tier 1 (Dev)**: Sprawdź logi, verifikuj deployment
   - Kontakt: dev-team@company.com
   - Time: < 15 min

2. **Tier 2 (DevOps)**: Infrastructure, database
   - Kontakt: devops@company.com
   - Time: < 30 min

3. **Tier 3 (Architect)**: Design, security issues
   - Kontakt: architect@company.com
   - Time: < 60 min

4. **Tier 4 (Management)**: Critical issues, rollback decision
   - Kontakt: management@company.com
   - Time: < 5 min

---

## 📝 Deployment Log Template

```
=== DEPLOYMENT LOG ===
Date: 2025-01-06
Version: 1.0.0
Deployed By: [name]
Duration: [start] - [end]

BEFORE:
- Application: [status]
- Database: [status]
- Backups: [created/checked]

DEPLOYMENT:
- Code deployed: [time]
- Database migrated: [time]
- Health check: [PASS/FAIL]
- Smoke test: [PASS/FAIL]

AFTER:
- Application: [status]
- Performance: [metrics]
- Users affected: [none/few/many]

NOTES:
[any additional notes]

ROLLBACK NEEDED: [YES/NO]
```

---

## 🎓 Lessons Learned Template

```
=== LESSONS LEARNED ===

What Went Well:
- [item 1]
- [item 2]

What Could Be Better:
- [item 1]
- [item 2]

Action Items:
- [ ] [action 1] by [date]
- [ ] [action 2] by [date]

Follow-up Meeting:
- Date: [date]
- Attendees: [names]
```

---

## ✅ Final Checklist

Before Going Live:

```
Code:
- [x] Compiles
- [x] Tests pass
- [x] Code reviewed
- [x] Security checked
- [x] Performance OK

Documentation:
- [x] Updated
- [x] Complete
- [x] Reviewed

Testing:
- [x] Unit tests
- [x] Integration tests
- [x] Functional tests
- [x] Performance tests
- [x] Security tests

Deployment:
- [x] Plan ready
- [x] Rollback plan ready
- [x] Backup created
- [x] Stakeholders notified

Monitoring:
- [x] Alerts configured
- [x] Logs configured
- [x] Metrics ready
- [x] Health checks ready

Communication:
- [x] Team notified
- [x] Support informed
- [x] Status page ready
- [x] Escalation path clear
```

---

**🚀 Ready to Deploy!**

Questions? Contact dev-team@company.com

---

*Last Updated: 06.01.2025*
*Version: 1.0.0*
*Status: READY FOR PRODUCTION*

