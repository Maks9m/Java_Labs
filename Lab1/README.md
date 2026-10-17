# DOP: система обробки сповіщень

Навчальний проєкт з предметною моделлю у стилі Data-Oriented Programming.

## Опис

- `Notification` — `sealed interface` з трьома `record`-варіантами:
    - `TextMsg(sender, text)`
    - `ImageMsg(sender, url, sizeKb)`
    - `AudioMsg(sender, durationSeconds)`
- Усі записи валідують свої поля в конструкторі і кидають `IllegalArgumentException` для null, порожніх або пробільних рядків та для значень ≤ 0.
- `NotificationProcessor.process(Notification)` перетворює сповіщення на текстовий опис через `switch` з pattern matching і text block. Для `null` кидає `IllegalArgumentException("Notification cannot be null")`.

Усі класи знаходяться в пакеті `ua.kpi.comsys.dop`.

## Вимоги

- JDK 25 (наприклад, Temurin)
- Maven 3.9+

## Запуск тестів

```bash
cd ./Lab1/
mvn test
```

Тести використовують JUnit 5 і розташовані в `src/test/java/ua/kpi/comsys/dop`.