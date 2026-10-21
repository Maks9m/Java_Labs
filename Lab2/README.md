# Частотний аналізатор тексту

Java-проєкт (Gradle, JUnit 5). Клас `WordCounter` пакету `ua.kpi.comsys.collections`
рахує частоту слів у текстових рядках і повертає унікальні слова.

## Специфікація

```java
public class WordCounter {
    public Map<String, Integer> countWords(List<String> textLines);
    public Set<String> getUniqueWords(List<String> textLines);
}
```

### Правила обробки тексту

- Словом вважається послідовність буквено-цифрових символів латиниці (`[a-zA-Z0-9]+`).
- Усі інші символи (пробіли, пунктуація, кирилиця тощо) є роздільниками.
  Наприклад, `don't` дає слова `don` і `t`.
- Усі слова приводяться до нижнього регістру.
- Порожні слова не утворюються і тому ігноруються.
- Результати повертаються відсортованими за алфавітом (`TreeMap` / `TreeSet`).
- Якщо `textLines == null` або один з його елементів `null`, кидається `IllegalArgumentException`.

## Розрахунок варіанта

Номер варіанта: **2**.
Номер в списку = 17
Розрахунок варіанта: 17 % 3 = 2

## Вимоги

- JDK 21 (Gradle сам використає toolchain, якщо JDK 21 встановлено)

## Запуск тестів

Збірка і тести:

```bash
cd ./Lab2/
./gradlew build
```

Лише тести:

```bash
cd ./Lab2/
./gradlew test
```

Або якщо результат вже кешований, то:
```bash
./gradlew test --rerun
```

## Структура проєкту

```
lib/
├──src/
├  ├── main/java/ua/kpi/comsys/collections/WordCounter.java
├  └── test/java/ua/kpi/comsys/collections/WordCounterTest.java
└── build.gradle.kts
gradle/libs.versions.toml
settings.gradle.kts
```
