# 🏠 Hostel Management System

A full-featured web application built with **Spring Boot 3**, **Thymeleaf**, **Spring Security**, and **MySQL**.

---

## 📋 Project Structure

```
hostel-management/
├── src/main/java/com/hostel/management/
│   ├── HostelManagementApplication.java     ← Main entry point
│   ├── config/
│   │   ├── SecurityConfig.java              ← Spring Security config
│   │   ├── MvcConfig.java                   ← Static resource / upload config
│   │   └── DataInitializer.java             ← Seeds demo data on first run
│   ├── controller/
│   │   ├── HomeController.java              ← Public pages
│   │   ├── AuthController.java              ← Login / Register
│   │   ├── AdminController.java             ← Admin module
│   │   ├── OwnerController.java             ← Owner module
│   │   └── VisitorController.java           ← Visitor module
│   ├── model/
│   │   ├── User.java
│   │   ├── Hostel.java
│   │   ├── Review.java
│   │   ├── Message.java
│   │   └── SavedHostel.java
│   ├── repository/                          ← Spring Data JPA repos
│   └── service/
│       ├── UserService.java
│       ├── HostelService.java
│       ├── MessageService.java
│       └── CustomUserDetailsService.java
├── src/main/resources/
│   ├── application.properties               ← DB + server config
│   ├── schema.sql                           ← Reference SQL schema
│   ├── static/css/style.css                 ← Global styles
│   └── templates/
│       ├── home.html, login.html, register.html
│       ├── search-results.html, hostel-detail.html
│       ├── admin/  (dashboard, users, hostels, ...)
│       ├── owner/  (dashboard, add-hostel, edit-hostel, ...)
│       └── visitor/(dashboard, search, hostel-detail, ...)
└── pom.xml
```

---

## ⚙️ Prerequisites

| Tool           | Version     |
|----------------|-------------|
| Java (JDK)     | 17 or above |
| Maven          | 3.8+        |
| MySQL          | 8.0+        |
| STS (Eclipse)  | 4.x         |
| Tomcat         | 10.x        |

---

## 🚀 Setup & Run

### Step 1 — MySQL Database

```sql
CREATE DATABASE hostel_db;
```

> The app auto-creates tables via Spring JPA (`ddl-auto=update`).

---

### Step 2 — Configure `application.properties`

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/hostel_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

Also set the upload directory (where hostel photos are stored):

```properties
hostel.upload.dir=C:/uploads/hostel-photos/
# Linux/Mac: hostel.upload.dir=/home/user/uploads/hostel-photos/
```

---

### Step 3A — Run in STS (Recommended)

1. Open STS → **File → Import → Maven → Existing Maven Projects**
2. Browse to the `hostel-management` folder → **Finish**
3. Wait for dependencies to download
4. Right-click project → **Run As → Spring Boot App**
5. Access: **http://localhost:8080/hostel**

---

### Step 3B — Deploy on Tomcat 10 in STS

1. Build WAR: **Right-click project → Run As → Maven Build → Goals: `package`**
2. WAR file generated at `target/hostel-management-1.0.0.war`
3. In STS: **Window → Show View → Servers**
4. Add **Tomcat 10** server
5. Right-click server → **Add/Remove** → Add your project
6. Start Tomcat → Access: **http://localhost:8080/hostel**

---

### Step 3C — Run via Maven (Terminal)

```bash
cd hostel-management
mvn spring-boot:run
```

---

## 🔑 Default Login Credentials

| Role    | Email / Phone        | Password   |
|---------|----------------------|------------|
| Admin   | admin@hostel.com     | admin123   |
| Owner   | owner@hostel.com     | owner123   |
| Visitor | visitor@hostel.com   | visitor123 |

> Demo data (3 sample hostels) is auto-seeded on first startup.

---

## 🌐 URL Routes

| URL                     | Access       | Description              |
|-------------------------|--------------|--------------------------|
| `/hostel/`              | Public       | Home page                |
| `/hostel/register`      | Public       | Registration             |
| `/hostel/login`         | Public       | Login                    |
| `/hostel/search`        | Public       | Search hostels           |
| `/hostel/admin/**`      | Admin only   | Admin panel              |
| `/hostel/owner/**`      | Owner only   | Owner dashboard          |
| `/hostel/visitor/**`    | Visitor only | Visitor dashboard        |

---

## ✅ Features Implemented

### Admin Module
- Default login (admin@hostel.com / admin123)
- Dashboard with stats (owners, visitors, hostels)
- View / Block / Unblock / Delete users
- View all hostels with detail view
- Delete any hostel

### Owner Module
- Register and login
- Dashboard with hostel count and unread messages
- Add hostel with all details + multiple photo upload
- Edit and delete own hostels
- View and reply to visitor messages
- Profile page

### Visitor Module
- Register and login
- Dashboard with latest hostels
- Search hostels by location and/or type (Boys / Girls / Mixed)
- View full hostel details with photo gallery
- Save / Unsave hostels (like/bookmark)
- Contact hostel owner via messaging system
- Rate and review hostels (one review per hostel)
- View all messages / conversations
- Profile page

---

## 🛠 Technologies Used

- **Spring Boot 3.2** — Core framework
- **Spring Security 6** — Authentication & Authorization
- **Spring Data JPA** — Database ORM
- **Thymeleaf** — Server-side HTML templates
- **MySQL 8** — Database
- **BCrypt** — Password hashing
- **HTML5 / CSS3 / JavaScript** — Frontend
- **Maven** — Build tool
- **Tomcat 10** — Application server

---

## 📁 Photo Uploads

Uploaded hostel photos are stored in the directory specified by `hostel.upload.dir`.
Make sure this directory exists and the app has write permissions.

> **Important:** `hostel.upload.dir` must be an **absolute path**. The default
> in `application.properties` now resolves to `${user.home}/hostel-uploads/hostel-photos/`
> so it works the same way whether you run the app via STS "Run As Spring Boot App"
> or as a WAR deployed inside Tomcat 10 — a relative path here previously caused
> owner-uploaded photos to be saved in a folder the visitor-facing image URLs
> weren't actually pointing at. Override it with the `HOSTEL_UPLOAD_DIR`
> environment variable if you want a custom location.

---

## 💡 Notes

- The `DataInitializer` only runs when the `users` table is empty (first startup).
- Admin credentials are **hardcoded** in `CustomUserDetailsService` and cannot be changed via the UI.
- For production, enable CSRF protection in `SecurityConfig` and use HTTPS.
