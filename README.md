# Task Management System - Сервис управления задачами на Spring Boot

## Описание
Task Management System — это веб-приложение для управления задачами. Приложение поддерживает ролевую модель с разделением доступа между администраторами и пользователями.

[JAVADOC](./apidocs/index.html)
---

## Содержание

- [Технологии](#технологии)
- [Запуск](#запуск)
- [API](#api)

---

## Технологии

### Язык и фреймворки
- **Java 21**: Язык программирования.
- **Spring Boot**: Фреймворк для веб-приложений.
- **Spring Security**: Аутентификация и авторизация.
- **Spring Data JPA**: Работа с базой данных.

### База данных
- **PostgreSQL**: Реляционная база данных.

### Аутентификация
- **JSON Web Token (JWT)**

### Контейнеризация
- **Docker**, **Docker Compose**

### Документация API
- **Swagger/OpenAPI**

### Тестирование
- **JUnit**, **Mockito**

### Утилиты
- **Lombok**

---

## Запуск

### Подготовка

1. Клонируйте репозиторий:
    ```bash
    git clone <URL_репозитория>
    cd <имя_папки_репозитория>
    ```

2. Создайте файл `.env` на основе `.env.example`:
    - Переименуйте `.env.example`:
        ```bash
        mv .env.example .env
        ```

    - Пример содержимого `.env` файла:
        ```dotenv
        # DB parameters
        DB_HOST=db
        DB_PORT=5432
        DB_NAME=postgres
        DB_USER=postgres
        DB_PASSWORD=postgres

        # Security parameters
        JWT_SECRET=MTEyMjM5cG9wYW11cmF2eWE0MjEzDJFHfj51ikfF%INWeP
        JWT_ACCESS=3600000
        JWT_REFRESH=259200000

        # App parameters
        SERVER_PORT=5000
        ```

### Запуск с использованием Docker

1. Соберите и запустите контейнеры:
    ```bash
    docker-compose up -d
    ```

2. Проверьте, что приложение доступно по адресу:
    ```
    http://localhost:5000
    ```

---

## API

### Эндпоинты

#### Аутентификация

- **POST /api/v1/auth/register**
    - **Описание**: Регистрация нового пользователя.
    - **Доступ**: Только администраторы.
    - **Пример тела запроса**:
        ```json
        {
          "email": "admin@example.com",
          "name": "Admin",
          "password": "securepassword",
          "roles": ["ROLE_ADMIN"]
        }
        ```

    - **Пример ответа**:
        ```json
        {
          "id": 1,
          "email": "admin@example.com",
          "accessToken": "jwtAccessToken",
          "refreshToken": "jwtRefreshToken"
        }
        ```

#### Задачи

- **PUT /api/v1/task/create**
    - **Описание**: Создание новой задачи.
    - **Доступ**: Только администраторы.
    - **Пример тела запроса**:
        ```json
        {
          "title": "Новая задача",
          "description": "Описание задачи",
          "priority": "HIGH",
          "status": "TODO",
          "authorEmail": "admin@example.com",
          "assigneeEmail": "user@example.com"
        }
        ```

    - **Пример ответа**:
        ```json
        {
          "message": "Task created successfully"
        }
        ```

### Полный список эндпоинтов и примеров доступен через Swagger UI по адресу:

- host:port/swagger-ui.html

---

## Запуск тестов

Чтобы запустить тесты, выполните:
```bash
./mvnw test
```