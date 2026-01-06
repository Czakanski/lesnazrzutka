# 🔧 Instrukcja: Błędy IDE - Cannot resolve symbol

## Problem

Widzisz błędy w IDE takie jak:
```
Error: Cannot resolve symbol 'DisplayName'
Error: Cannot resolve symbol 'Test'
Error: Cannot resolve symbol 'Autowired'
```

Ale kod się kompiluje bez problemów! (`./gradlew build` działa)

## Przyczyna

Jest to **problem IDE cache/indexing**, nie problem kodu.

## Rozwiązanie

### Opcja 1: Invalidate IDE Cache (REKOMENDOWANE)

#### IntelliJ IDEA / JetBrains IDE:
1. Kliknij menu: `File` → `Invalidate Caches...`
2. Zaznacz:
   - ✅ `Clear file system cache`
   - ✅ `Clear VCS log caches`
   - ✅ `Clear compiled bytecode`
3. Kliknij `Invalidate and Restart`
4. IDE restartuje i przeindeksuje projekt

### Opcja 2: Manualnie wyczyść cache

```bash
# macOS / Linux
cd ~/Documents/lesnazrzutka
rm -rf .idea
rm -rf .gradle
./gradlew clean --refresh-dependencies
```

### Opcja 3: Rebuild Gradle Index

1. Kliknij `View` → `Tool Windows` → `Gradle`
2. Kliknij refresh ikonę (⟲) w panelu Gradle
3. Czekaj na reload

## Jak wiedzieć, że problem się rozwiązał?

- Czerwone kwadraty przy importach znikną
- IDE pokaże poprawne autocomplete
- Hover over symbol pokaże dokumentację

## Kod jest prawidłowy! ✅

Wszystkie testy się kompilują:
```bash
./gradlew compileTestJava  # ✅ SUCCESS
./gradlew test              # ✅ SUCCESS
```

Błędy IDE to tylko problem **visualizacji w editore**, a nie realny problem kodu.

## Jeśli problem trwa:

1. **Upgrade IDE** do wersji 2025.2 lub nowszej
2. **Zamknij i otwórz IDE** ponownie
3. **Sprawdź Java SDK** - powinno być Java 17+
4. **Skonfiguruj Gradle SDK** w IDE

---

**Wniosek**: Twoje testy są poprawne! To tylko cache IDE. 🎉

