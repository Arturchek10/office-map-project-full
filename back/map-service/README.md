# Office Map Booking System

Веб-приложение для управления офисным пространством с возможностью бронирования рабочих мест.

## 📌 Описание

Система позволяет:
- отображать офис на интерактивной карте
- управлять этажами и рабочими местами
- бронировать рабочие места
- разграничивать роли пользователей (user / admin)

Проект разрабатывается как дипломная работа.

---

## 🧱 Архитектура

Frontend → Backend → Database

### Backend (Spring Boot)
- controller — обработка HTTP-запросов
- service — бизнес-логика
- repository — работа с БД
- entity — модели данных
- security — JWT авторизация

### Frontend (React + TypeScript)
- отображение карты офиса
- работа с API
- управление состоянием (effector)

---

## 🔄 Поток запроса

Пример:

GET /api/v1/offices

→ OfficeController  
→ OfficeService  
→ OfficeRepository  
→ PostgreSQL  
→ ответ возвращается на frontend

---

## 🔐 Авторизация

- JWT access token — для запросов
- refresh token — для обновления access token

---

## 📡 Основные API

### Auth
- POST /api/v1/auth/sign-in
- POST /api/v1/auth/sign-up

### Offices
- GET /api/v1/offices
- DELETE /api/v1/offices/{id}

### Floors
- GET /api/v1/floors/{id}

---

## 🗂 Структура проекта
