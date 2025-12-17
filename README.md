#  Календарь и Заметки (CalendarApp)

![Java](https://img.shields.io/badge/Java-JDK_24-orange)
![Gradle](https://img.shields.io/badge/Gradle-8.x-elephant)
![JavaFX](https://img.shields.io/badge/UI-JavaFX-blue)
![Build](https://img.shields.io/badge/Build-ShadowJar-green)

Десктопное приложение для планирования личных задач. Позволяет вести заметки и автоматически определять статус дня (рабочий, выходной, государственный праздник РФ) с использованием гибридного алгоритма (API + Справочник).

---

##  Как запустить приложение 
### 1) Первый способ
### Шаг 1.1) Сборка проекта
Откройте терминал в папке проекта и выполните команду для создания JAR-файла (со всеми библиотеками внутри):

**Windows:**
```cmd
gradlew.bat shadowJar
```

**Linux**
```cmd
./gradlew shadowJar
```
### Шаг 1.2) Запуск
Собранный файл появится в папке build/libs. Запустите его командой:
```cmd
java -jar build/libs/CalendarApp-1.0-SNAPSHOT-all.jar

```
### 2) Запуск через IntelliJ IDEA
Самый простой способ, если вы открыли исходный код проекта.
Откройте панель Gradle (справа).
Перейдите по пути: CalendarApp -> Tasks -> application.
Дважды кликните по задаче run.

Альтернативно:
Найдите файл src/main/java/com/calendar/Launcher.java.
Нажмите зеленый треугольник рядом с методом main.
