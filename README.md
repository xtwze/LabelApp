# 🎵 LabelApp

**Веб-приложение для управления музыкальным каталогом**  
*Spring Boot + Thymeleaf*

![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?logo=springboot&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?logo=apachemaven&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-005F0F?logo=thymeleaf&logoColor=white)

---

## ✨ О проекте

**LabelApp** — полнофункциональное веб-приложение для создания и управления музыкальным каталогом. Пользователи могут регистрироваться, добавлять треки, просматривать каталог, добавлять в избранное и управлять данными.

Проект разработан как демонстрация навыков **Full-stack Java** разработки.

### Основные возможности

- ✅ Регистрация и авторизация пользователей
- ✅ Добавление, просмотр и редактирование треков
- ✅ Каталог всех треков
- ✅ Система избранного (Favorites)
- ✅ Админ-панель
- ✅ Загрузка файлов (обложки треков)
- ✅ Красивый и удобный интерфейс на Thymeleaf

---

## 🚀 Быстрый запуск

### 1. Клонируйте репозиторий
```bash
git clone https://github.com/xtwze/LabelApp.git
cd LabelApp
2. Запустите приложение
Через Maven:
Bash./mvnw spring-boot:run
Или через IDE:
Откройте проект в IntelliJ IDEA и запустите LabelAppApplication.java
Приложение будет доступно по адресу: http://localhost:8080

📁 Структура проекта
textLabelApp/
├── src/main/java/com/example/LabelApp/
│   ├── controller/      # Контроллеры (MVC)
│   ├── service/         # Бизнес-логика
│   ├── repository/      # Репозитории (Spring Data JPA)
│   ├── models/          # Сущности
│   ├── dto/             # Data Transfer Objects
│   ├── config/          # Конфигурация (Security и т.д.)
│   └── LabelAppApplication.java
├── src/main/resources/
│   ├── templates/       # HTML-шаблоны Thymeleaf
│   ├── static/          # CSS, JS, изображения
│   └── application.properties
├── uploads/             # Загруженные файлы (обложки)
├── pom.xml
└── mvnw

🛠️ Технологии

Backend: Java 21, Spring Boot 3
Шаблонизатор: Thymeleaf
База данных: Spring Data JPA (H2 / PostgreSQL)
Безопасность: Spring Security
Сборка: Maven
Frontend: HTML5, CSS, Bootstrap


🎯 Функционал



Функция,Статус
Регистрация / Логин,✅ Готово
Добавление треков,✅ Готово
Каталог треков,✅ Готово
Избранное,✅ Готово
Админ-панель,✅ Готово
Загрузка обложек,✅ Готово

