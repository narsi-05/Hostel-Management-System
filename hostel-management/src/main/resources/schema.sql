-- ============================================================
--  HOSTEL MANAGEMENT SYSTEM — MySQL Schema (Reference)
--  The app uses Spring JPA with ddl-auto=update, so this file
--  is for documentation / manual setup only.
--  Database: hostel_db
-- ============================================================

CREATE DATABASE IF NOT EXISTS hostel_db
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE hostel_db;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name    VARCHAR(50)  NOT NULL,
    last_name     VARCHAR(50)  NOT NULL,
    email         VARCHAR(100) NOT NULL UNIQUE,
    phone_number  VARCHAR(15)  NOT NULL UNIQUE,
    password      VARCHAR(255) NOT NULL,
    user_type     ENUM('OWNER','VISITOR') NOT NULL,
    status        ENUM('ACTIVE','INACTIVE','BLOCKED') DEFAULT 'ACTIVE',
    created_at    DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Hostels table
CREATE TABLE IF NOT EXISTS hostels (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    hostel_name      VARCHAR(150) NOT NULL,
    hostel_type      ENUM('GIRLS_HOSTEL','BOYS_HOSTEL','MIXED') NOT NULL,
    location         VARCHAR(255) NOT NULL,
    city             VARCHAR(100),
    state            VARCHAR(100),
    availability     ENUM('STUDENTS_ONLY','ANYONE') NOT NULL,
    sharing_type     ENUM('TWO_SHARING','THREE_SHARING','FOUR_SHARING','SINGLE') NOT NULL,
    amount_per_month DECIMAL(10,2) NOT NULL,
    phone_number     VARCHAR(15)  NOT NULL,
    description      TEXT,
    photos           TEXT,
    average_rating   DOUBLE DEFAULT 0.0,
    total_reviews    INT    DEFAULT 0,
    status           ENUM('ACTIVE','INACTIVE','PENDING_REVIEW') DEFAULT 'ACTIVE',
    owner_id         BIGINT NOT NULL,
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Reviews table
CREATE TABLE IF NOT EXISTS reviews (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    rating      INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment     TEXT NOT NULL,
    hostel_id   BIGINT NOT NULL,
    visitor_id  BIGINT NOT NULL,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (hostel_id)  REFERENCES hostels(id) ON DELETE CASCADE,
    FOREIGN KEY (visitor_id) REFERENCES users(id)   ON DELETE CASCADE,
    UNIQUE KEY uq_review (hostel_id, visitor_id)
);

-- Messages table
CREATE TABLE IF NOT EXISTS messages (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    content     TEXT NOT NULL,
    sender_id   BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    hostel_id   BIGINT,
    is_read     BOOLEAN DEFAULT FALSE,
    sent_at     DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id)   REFERENCES users(id)   ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES users(id)   ON DELETE CASCADE,
    FOREIGN KEY (hostel_id)   REFERENCES hostels(id) ON DELETE SET NULL
);

-- Saved Hostels table
CREATE TABLE IF NOT EXISTS saved_hostels (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    visitor_id  BIGINT NOT NULL,
    hostel_id   BIGINT NOT NULL,
    saved_at    DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (visitor_id) REFERENCES users(id)   ON DELETE CASCADE,
    FOREIGN KEY (hostel_id)  REFERENCES hostels(id) ON DELETE CASCADE,
    UNIQUE KEY uq_saved (visitor_id, hostel_id)
);

-- ============================================================
--  REFERENCE QUERIES
--  These used to live as @Query annotations inside the
--  repository interfaces. The repositories now use only plain
--  Spring Data derived-method-name queries (no @Query, no
--  @Procedure) — the equivalent SQL is documented here instead
--  for anyone who needs to run it directly or as a sanity check
--  against the generated JPQL.
-- ============================================================

-- HostelRepository.findByStatusAndLocationContainingIgnoreCase(status, location)
SELECT * FROM hostels
WHERE status = 'ACTIVE'
  AND LOWER(location) LIKE LOWER(CONCAT('%', :location, '%'));

-- HostelRepository.findByStatusAndCityContainingIgnoreCase(status, city)
SELECT * FROM hostels
WHERE status = 'ACTIVE'
  AND LOWER(city) LIKE LOWER(CONCAT('%', :city, '%'));

-- HostelRepository.findByStatusAndHostelType(status, hostelType)
SELECT * FROM hostels
WHERE status = 'ACTIVE'
  AND hostel_type = :type;

-- HostelService.searchHostels(location, type) — location/city match AND type
-- (fetched via findByStatusAndHostelType, then filtered in Java)
SELECT * FROM hostels
WHERE status = 'ACTIVE'
  AND hostel_type = :type
  AND (LOWER(location) LIKE LOWER(CONCAT('%', :keyword, '%'))
       OR LOWER(city) LIKE LOWER(CONCAT('%', :keyword, '%')));

-- HostelService.searchHostels(keyword) — free-text search across name/location/city
-- (fetched via findByStatus, then filtered in Java)
SELECT * FROM hostels
WHERE status = 'ACTIVE'
  AND (LOWER(hostel_name) LIKE LOWER(CONCAT('%', :keyword, '%'))
       OR LOWER(location)    LIKE LOWER(CONCAT('%', :keyword, '%'))
       OR LOWER(city)        LIKE LOWER(CONCAT('%', :keyword, '%')));

-- HostelService.updateHostelRating(hostel) — average rating for a hostel
-- (fetched via ReviewRepository.findByHostel, averaged in Java)
SELECT AVG(rating) FROM reviews WHERE hostel_id = :hostelId;

-- MessageRepository.findBySenderOrReceiverOrderBySentAtDesc(user, user)
SELECT * FROM messages
WHERE sender_id = :userId OR receiver_id = :userId
ORDER BY sent_at DESC;

-- MessageService.getConversation(user1, user2)
-- (fetched as two directional derived-method calls, merged and sorted in Java)
SELECT * FROM messages
WHERE (sender_id = :user1Id AND receiver_id = :user2Id)
   OR (sender_id = :user2Id AND receiver_id = :user1Id)
ORDER BY sent_at ASC;

-- UserRepository.findByEmailOrPhoneNumber(email, phoneNumber)
SELECT * FROM users
WHERE email = :email OR phone_number = :phoneNumber;

-- ============================================================
--  REFERENCE QUERIES
--  The repositories no longer contain @Query / @Procedure code —
--  every lookup is either a Spring Data derived method name or a
--  small default method that composes derived finders in Java.
--  The statements below are the plain-SQL equivalent of each one,
--  kept here purely as documentation of what each repository
--  method does under the hood.
-- ============================================================

-- UserRepository.findByEmailOrPhoneNumber(emailOrPhone)
SELECT * FROM users WHERE email = :emailOrPhone OR phone_number = :emailOrPhone;

-- HostelRepository.findByLocationContainingIgnoreCase(location)
SELECT * FROM hostels
WHERE status = 'ACTIVE' AND LOWER(location) LIKE LOWER(CONCAT('%', :location, '%'));

-- HostelRepository.findByCityContainingIgnoreCase(city)
SELECT * FROM hostels
WHERE status = 'ACTIVE' AND LOWER(city) LIKE LOWER(CONCAT('%', :city, '%'));

-- HostelRepository.findByHostelType(type)
SELECT * FROM hostels WHERE status = 'ACTIVE' AND hostel_type = :type;

-- HostelRepository.findByLocationAndType(location, type)
SELECT * FROM hostels
WHERE status = 'ACTIVE' AND hostel_type = :type
  AND (LOWER(location) LIKE LOWER(CONCAT('%', :location, '%'))
       OR LOWER(city) LIKE LOWER(CONCAT('%', :location, '%')));

-- HostelRepository.searchHostels(keyword)
SELECT * FROM hostels
WHERE status = 'ACTIVE'
  AND (LOWER(location) LIKE LOWER(CONCAT('%', :keyword, '%'))
       OR LOWER(city) LIKE LOWER(CONCAT('%', :keyword, '%'))
       OR LOWER(hostel_name) LIKE LOWER(CONCAT('%', :keyword, '%')));

-- ReviewRepository.findAverageRatingByHostel(hostel)
SELECT AVG(rating) FROM reviews WHERE hostel_id = :hostelId;

-- MessageRepository.findAllMessagesForUser(user)
SELECT * FROM messages
WHERE sender_id = :userId OR receiver_id = :userId
ORDER BY sent_at DESC;

-- MessageRepository.findConversation(user1, user2)
SELECT * FROM messages
WHERE (sender_id = :user1Id AND receiver_id = :user2Id)
   OR (sender_id = :user2Id AND receiver_id = :user1Id)
ORDER BY sent_at ASC;
