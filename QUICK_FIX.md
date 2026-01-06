# 🚀 SZYBKA INSTRUKCJA: Napraw błędy IDE (30 sekund)

## Problem

Widzisz czerwone błędy "Cannot resolve symbol" w IDE, ale:
- ✅ `./gradlew test` działa
- ✅ `./gradlew build` działa
- ✅ Kod się kompiluje

## Rozwiązanie

### ⚡ METODA 1 (Najszybsza - 20 sekund)

**JetBrains IDE (IntelliJ, PyCharm, WebStorm, itd):**

1. Kliknij menu: `File`
2. Kliknij: `Invalidate Caches...`
3. Zaznacz obie opcje
4. Kliknij: `Invalidate and Restart`
5. Czekaj 10 sekund na restart

**GOTOWE!** ✅

---

### 🔧 METODA 2 (Jeśli Metoda 1 nie zadziała)

Otwórz terminal i wpisz:

```bash
cd /Users/dawidczakanski/Documents/lesnazrzutka
rm -rf .idea
./gradlew clean refresh-dependencies
```

Potem otwórz projekt ponownie w IDE.

---

### 📊 METODA 3 (Gradle Refresh)

Jeśli masz Gradle Tool Window otwarte:

1. Kliknij ikonę refresh (⟲) w Gradle panelu
2. Czekaj na refresh
3. Testy powinny być teraz OK

---

## ✅ Jak sprawdzić, że problem się rozwiązał?

- Znikły czerwone kwadraty przy importach
- IDE pokazuje autocomplete
- Hover over symbol pokazuje dokumentację

## 🎯 FAQ

**P: Czy kod jest poprawny?**  
O: TAK! Wszystkie testy się kompilują i uruchamiają.

**P: Dlaczego IDE pokazuje błędy?**  
O: Cache IDE nie został odświeżony. To czysty problem IDE.

**P: Czy muszę coś zmieniać?**  
O: NIE! Nie rób nic z kodem. Tylko odśwież IDE cache.

**P: Co jeśli nadal widzę błędy?**  
O: Upgrade IDE do wersji 2025.2 lub nowszej.

---

**Gotowe!** 🎉

Teraz Twoje testy powinny być zielone i bez błędów! ✅

