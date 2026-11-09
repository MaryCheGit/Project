# 📚 КиноКнигоТЕКА

Веб-приложение для любителей книг и кино: каталог, личная библиотека, оценки и рекомендации.

---

## 📋 Описание

**КиноКнигоТЕКА** — это полноценное веб-приложение, которое объединяет книги и их экранизации в одном месте. Пользователи могут:
- Просматривать каталог книг и фильмов.
- Искать и фильтровать по категориям.
- Добавлять книги/фильмы в личную библиотеку.
- Ставить многокритериальные оценки (сюжет, персонажи, общее впечатление).
- Читать отзывы других пользователей.
- Получать рекомендации (похожие книги/фильмы).
- Скачивать и смотреть онлайн (имитация).

---

## 🛠️ Стек технологий

### Backend
- **Java 21**
- **Spring Boot 4.1.1**
- **Spring Security** + **JWT** (авторизация)
- **Spring Data JPA** + **Hibernate 7**
- **PostgreSQL 18**
- **Maven**

### Frontend
- **HTML5**
- **CSS3** (адаптивный дизайн)
- **JavaScript** (ES6+, Fetch API)

### Тестирование
- **JUnit 5**
- **WebTestClient**

---

## 🚀 Функциональность

### Для пользователя
- ✅ Регистрация и вход (JWT-авторизация)
- ✅ Каталог книг и фильмов с обложками
- ✅ Поиск по названию, автору, режиссёру
- ✅ Фильтрация по категориям
- ✅ Личная библиотека с фильтрами
- ✅ Многокритериальные оценки (сюжет, персонажи, общее)
- ✅ Просмотр всех оценок и отзывов
- ✅ Рекомендации (похожие книги/фильмы)
- ✅ Имитация скачивания (PDF, EPUB, MP4, AVI)
- ✅ Онлайн-просмотр (YouTube-плеер)
- ✅ Чтение онлайн

### Для администратора
- ✅ Добавление книг
- ✅ Добавление фильмов
- ✅ Управление контентом

---

### Сущности (6 таблиц)
- **`users`** — пользователи (с ролью USER/ADMIN)
- **`categories`** — категории книг
- **`books`** — книги (связь с категорией)
- **`movies`** — фильмы (связь с книгой)
- **`user_library`** — личная библиотека
- **`ratings`** — оценки с тремя критериями

---

## 📦 Установка и запуск

### Требования
- Java 21+
- PostgreSQL 18+
- Maven 3.8+

## Настройка базы данных

- Создайте базу данных shop_db в PostgreSQL.
- Открыть src/main/resources/application.properties и укажите:
     spring.datasource.url=jdbc:postgresql://localhost:5432/shop_db
     spring.datasource.username=postgres
     spring.datasource.password=.......
     
## Запуск приложения 

  mvn spring-boot:run
  
   
## Открыть в браузере

    http://localhost:8080/
    
 
## 🧪 Тестирование

testRegisterAndLogin	       Регистрация + вход	
testLoginWithWrongPassword	   Неверный пароль → 401	
testGetAllBooks	               Список книг → 200	
testGetBookById	               Книга по id → 200	
testGetBookByIdNotFound	       Несуществующая книга → 404	
contextLoads	               Загрузка Spring-контекста  

## Структура проекта
shop/
├── src/main/java/com/example/shop/
│   ├── config/          # SecurityConfig
│   ├── controller/      # 7 контроллеров
│   ├── dto/             # AuthResponse, LoginRequest, RegisterRequest
│   ├── entity/          # 6 сущностей
│   ├── enums/           # Role, ReadingStatus
│   ├── repository/      # 6 репозиториев
│   ├── security/        # JWT, CustomUserDetailsService
│   └── ShopApplication.java
├── src/main/resources/
│   ├── static/          # HTML, CSS, JS, картинки
│   │   ├── backgrounds/ # фоны страниц
│   │   ├── uploads/     # обложки книг и постеры фильмов
│   │   ├── app.js
│   │   ├── style.css
│   │   ├── index.html
│   │   ├── books.html
│   │   ├── movies.html
│   │   ├── book-detail.html
│   │   ├── movie-detail.html
│   │   ├── login.html
│   │   ├── register.html
│   │   ├── profile.html
│   │   ├── ratings.html
│   │   └── admin.html
│   └── application.properties
├── src/test/java/com/example/shop/
│   ├── AuthControllerTest.java
│   ├── BookControllerTest.java
│   └── ShopApplicationTests.java
└── pom.xml

## 🔐 Безопасность

- BCrypt — хеширование паролей.
- JWT — авторизация через токены (24 часа)
- Spring Security — защита эндпоинтов
- Роли — USER и ADMIN.

## 🎯 Особенности проекта

- Связь «книга → фильм» — двусторонняя навигация.
- Многокритериальные оценки — сюжет, персонажи, общее впечатление.
- Автоматический пересчёт рейтинга — среднее по всем оценкам.
- Система рекомендаций — 2 книги/фильма из той же категории с высоким рейтингом.
- Роль ADMIN — управление контентом.

## 📊 Статистика

- 6 сущностей (таблиц)
- 7 REST-контроллеров
- 6 репозиториев
- 10 HTML-страниц
- 10 книг + 10 фильмов
- 6/6 тестов пройдено












