# DocuShield - Система безпечного документообігу та контролю конфіденційних файлів

Єдиний комплексний проект для курсу «Веб-безпека» (Лабораторні роботи №1, №2, №3 та №4).

---

##  Технологічний стек

* **ОС**: Ubuntu 24 LTS / macOS
* **Мова & Платформа**: Java 21/25, Spring Boot 3.4.x (Spring Security 6)
* **База даних**: MongoDB
* **API REST Клієнт**: Postman + IntelliJ HTTP Client (`.http`)
* **Фронтенд**: Angular 19 (Standalone компоненти)
* **Контейнеризація**: Docker & Docker Compose
* **Ідентифікація**: Вбудований Basic Auth / RBAC + Keycloak 24.0.2

---

##  Структура репозиторію

```text
securityweb/
├── backend/                       # Spring Boot додаток
│   ├── src/main/java/com/docushield/
│   │   ├── config/                # SecurityConfig (Basic Auth, CSRF, CORS) + DataLoader
│   │   ├── controller/            # DocumentController, AuditLogController, CsrfController
│   │   ├── model/                 # DocumentItem, AuditLog, ClassificationLevel
│   │   ├── repository/            # DocumentRepository, AuditLogRepository
│   │   └── service/               # DocumentService, AuditService
│   ├── Dockerfile
│   └── pom.xml
├── frontend/                      # Angular 19 клієнт
│   ├── src/app/                   # Компоненти управління документами та аудитом
│   └── package.json
├── requests/                      # Запити для здачі лабораторних
│   ├── basic-authorization.http   # 10 тестових запитів (Лабораторна №3)
│   ├── csrf-audit.http            # 5 обов'язкових CRUD запитів з асертами (Лабораторна №4)
│   └── DocuShield.postman_collection.json # Повна Postman колекція
├── docker/
│   └── keycloak/
│       └── realm-export.json      # Готовий реалм для наступних лаб (Keycloak 24)
├── docker-compose.yml             # Оркестрація MongoDB, Keycloak, Backend, Frontend
└── README.md
```

---

##  Інструкція із запуску

### Варіант 1. Швидкий запуск через Docker Compose
```bash
docker compose up -d
```
Сервіси будуть доступні за адресами:
* **Frontend (Angular)**: [http://localhost:4200](http://localhost:4200)
* **Backend API (Spring Boot)**: [http://localhost:8080](http://localhost:8080)
* **Keycloak Admin**: [http://localhost:8081](http://localhost:8081) (`admin` / `admin`)
* **MongoDB**: `localhost:27017`

### Варіант 2. Локальний запуск Backend
1. Запустіть MongoDB локально або через docker:
   ```bash
   docker run -d -p 27017:27017 --name mongo mongo:7.0
   ```
2. Збірка та старт Spring Boot:
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```

---

##  Виконання лабораторних робіт

### Лабораторна №1: Архітектура та ініціалізація
* Створено модульний веб-додаток з чітким розділенням шарів (`Controller` -> `Service` -> `Repository` -> `MongoDB`).
* Налаштовано `DataLoader`, який автоматично наповнює базу початковими тестовими документами та записами аудиту.

### Лабораторна №2: HTTP CRUD запити
Реалізовано REST ендпоінти для моделі `DocumentItem`:
* `GET /api/v1/documents` - отримати всі документи (підтримує фільтр `?classification=SECRET`).
* `GET /api/v1/documents/{id}` - перегляд одного документа.
* `POST /api/v1/documents` - створення нового конфіденційного документа.
* `PUT /api/v1/documents/{id}` - оновлення метаданих документа.
* `DELETE /api/v1/documents/{id}` - видалення документа (доступно тільки адміністратору).

### Лабораторна №3: Basic Authorization
* Захист усіх ресурсів через Spring Security `httpBasic()`.
* **Користувачі за замовчуванням**:
  * `admin` : `admin123` (ролі: `ADMIN`, `USER`)
  * `user` : `user123` (роль: `USER`)
  * `guest` : `guest123` (роль: `GUEST`)
* У файлі [requests/basic-authorization.http](file:///Users/vlkorolenko/Desktop/securityweb/requests/basic-authorization.http) реалізовано **10 запитів** (перевірка 401 для анонімного доступу, валідація ролей, 403 для несанкціонованого видалення, 200/201 для успішних операцій).

### Лабораторна №4: CSRF & Audit
* **Аудит безпеки**: клас `AuditService` та колекція `audit_logs` у MongoDB фіксують кожен запит: користувача, дію (`CREATE`, `READ`, `UPDATE`, `DELETE`), IP-адресу клієнта, HTTP статус-код та часову мітку.
* **CSRF захист**: налаштовано підтримку `CookieCsrfTokenRepository` та ендпоінт `/api/v1/csrf`.
* У файлі [requests/csrf-audit.http](file:///Users/vlkorolenko/Desktop/securityweb/requests/csrf-audit.http) підготовлено **5 обов'язкових сценаріїв**:
  1. `GET` show all
  2. `POST` create one
  3. `GET` read one
  4. `PUT` update one
  5. `DELETE` delete one
  Всі запити містять автоматичні JavaScript-асерти `client.test(...)` для тестування в IntelliJ IDEA (ідентично до скріншоту викладача).

### Лабораторна №5: Multi-user
* Створено 3 користувачів з різними рівнями доступу:
  * `admin` (пароль: `admin123`, роль: `ADMIN`) - повний доступ до всіх операцій та журналу аудиту.
  * `manager` (пароль: `manager123`, роль: `MANAGER`) - створення, читання та оновлення документів. Видалення та аудит заборонено (403 Forbidden).
  * `user` (пароль: `user123`, роль: `USER`) - лише читання документів. Створення, оновлення та видалення заборонено (403 Forbidden).
* У файлі [requests/task5-multi-user.http](file:///Users/vlkorolenko/Desktop/securityweb/requests/task5-multi-user.http) реалізовано набір із 10 запитів з автоматичними асертами статус-кодів (401, 200, 201, 403) для підтвердження матриці прав доступу.

### Лабораторна №6: Annotations
* Увімкнено Method Security через `@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)`.
* Захищено маршрути контролера за допомогою анотацій `@PreAuthorize`:
  * `@PreAuthorize("hasRole('ADMIN')")` для секретного сховища (`/documents/secret-vault`) та видалення (`DELETE /documents/{id}`).
  * `@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")` для панелі управління (`/documents/management-overview`), створення (`POST`) та оновлення (`PUT`).
  * `@PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")` для профілю користувача (`/documents/user-profile`) та читання списку документів.
* У файлі [requests/task6-annotations.http](file:///Users/vlkorolenko/Desktop/securityweb/requests/task6-annotations.http) створено 10 запитів від різних користувачів (anonymous, user, manager, admin) з перевіркою коректності спрацьовування анотацій (401, 403, 200).

### Лабораторна №7: Access testing (JUnit)
* Створено клас інтеграційних тестів [backend/src/test/java/com/docushield/AccessTests.java](file:///Users/vlkorolenko/Desktop/securityweb/backend/src/test/java/com/docushield/AccessTests.java) на базі Spring Boot Test та Spring Security Test (`MockMvc`, `@WithMockUser`, `@WithAnonymousUser`).
* Реалізовано 10 автоматичних JUnit тестів, що перевіряють статус-коди доступу:
  1. `whenAnonymAccessDocumentsThenStatusUnauthorized` (401)
  2. `whenAnonymAccessSecretVaultThenStatusUnauthorized` (401)
  3. `whenUserAccessDocumentsThenStatusOk` (200)
  4. `whenUserAccessUserProfileThenStatusOk` (200)
  5. `whenUserAccessHelloUserThenStatusOk` (200)
  6. `whenUserAccessHelloAdminThenStatusForbidden` (403)
  7. `whenUserAccessSecretVaultThenStatusForbidden` (403)
  8. `whenManagerAccessManagementOverviewThenStatusOk` (200)
  9. `whenManagerAccessSecretVaultThenStatusForbidden` (403)
  10. `whenAdminAccessSecretVaultThenStatusOk` (200)
