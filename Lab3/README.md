# Лабораторна робота №3: Опрацювання нескінченних послідовностей даних

Аналізатор журналів безпеки на Java Stream API: ліниві операції над потоками, що можуть бути нескінченними, `Optional` і власний `Gatherer` (JDK 24+).

## Варіант

- Номер залікової книжки: N = 17
- Варіант: V = 17 mod 3 = 2, тобто обробка безпекових подій (`SecurityLog`)

## Середовище

- JDK 25. Gradle вибере його через toolchain у `../../../../files/build.gradle.kts`.
- Gradle wrapper (`./gradlew`) у папці проєкту.

## Запуск

Усі команди виконувати з папки `Lab3/`:

```bash
cd ./Lab3/
./gradlew test
./gradlew cleanTest test
```

Звіт про тести: `build/reports/tests/test/index.html`.

## Структура

- `src/main/java/ua/kpi/comsys/streams/SecurityLog.java` — модель запису журналу
- `src/main/java/ua/kpi/comsys/streams/SecurityLogAnalyzer.java` — методи `findFirstCriticalEvent`, `getLogsBeforeDeadline`, `detectIpBursts`
- `src/test/java/ua/kpi/comsys/streams/SecurityLogAnalyzerTest.java` — звичайні сценарії на скінченних потоках
- `src/test/java/ua/kpi/comsys/streams/SecurityLogAnalyzerInfiniteStreamTest.java` — тести на нескінченних потоках, кожен із `@Timeout(2)`

## Ключові рішення

`detectIpBursts` додає кожну IP-адресу у звіт не більше одного разу і зупиняє обробку, щойно у звіті накопичилося `maxIpsToReport` адрес. Зупинка йде через інтегратор Gatherer, що повертає `false`, тому нескінченний потік не зависає, якщо потрібна кількість серій у ньому є.

`getLogsBeforeDeadline` використовує строгу межу: події з timestamp, що не раніше дедлайну, не потрапляють у результат, і обробка зупиняється на першій такій події.
