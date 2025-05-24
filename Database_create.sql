-- Trước tiên, bảo đảm extension uuid-ossp được cài đặt
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Tạo database (giữ nguyên)
-- CREATE DATABASE hotel_booking;

-- Chuyển vào database (giữ nguyên)
\c hotel_booking;

-- Tạo schemas (giữ nguyên)
CREATE SCHEMA IF NOT EXISTS "user_service";
CREATE SCHEMA IF NOT EXISTS "booking_service";
CREATE SCHEMA IF NOT EXISTS "payment_service";
CREATE SCHEMA IF NOT EXISTS "notification_service";
CREATE SCHEMA IF NOT EXISTS "recommendation_service";

-- ========================
-- USER SERVICE
-- ========================
CREATE TABLE user_service.users (
                              id UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
                              email VARCHAR(255) UNIQUE NOT NULL,
                              password_hash TEXT NOT NULL,
                              first_name VARCHAR(100) ,
                              last_name VARCHAR(100) ,
                              phone_number VARCHAR(20),
                              profile_picture VARCHAR(255),
                              date_of_birth DATE,
                              bio TEXT,
                              role VARCHAR(20),
                              gender VARCHAR(10),
                              identification VARCHAR(30),
                              created_at TIMESTAMP DEFAULT NOW(),
                              created_by VARCHAR(50),
                              updated_at TIMESTAMP,
                              updated_by VARCHAR(50)
);

CREATE TABLE user_service.reviews (
                                id UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
                                booking_id UUID NOT NULL,
                                user_id UUID NOT NULL,
                                property_id UUID NOT NULL,
                                host_id UUID NOT NULL,
                                rating INT CHECK (rating BETWEEN 1 AND 5) NOT NULL,
                                comment TEXT,
                                created_at TIMESTAMP DEFAULT NOW(),
                                created_by VARCHAR(50),
                                updated_at TIMESTAMP,
                                updated_by VARCHAR(50)
);

-- ========================
-- BOOKING SERVICE
-- ========================
CREATE TABLE booking_service.properties (
                                    id UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
                                    host_id UUID NOT NULL,
                                    title VARCHAR(255) NOT NULL,
                                    description TEXT NOT NULL,
                                    property_type VARCHAR(50) NOT NULL,
                                    room_type VARCHAR(50) NOT NULL,
                                    address VARCHAR(255) NOT NULL,
                                    province_id VARCHAR(100) NOT NULL,
                                    district_id VARCHAR(100) NOT NULL,
                                    ward_id VARCHAR(100) NOT NULL,
                                    zip_code VARCHAR(20) NOT NULL,
                                    latitude DECIMAL(10,8) NOT NULL,
                                    longitude DECIMAL(11,8) NOT NULL,
                                    price_per_night DECIMAL(10,2) NOT NULL,
                                    service_fee DECIMAL(10,2) DEFAULT 0,
                                    max_guests INTEGER NOT NULL,
                                    bedrooms INTEGER NOT NULL,
                                    beds INTEGER NOT NULL,
                                    bathrooms DECIMAL(3,1) NOT NULL,
                                    check_in_time TIME DEFAULT '15:00:00',
                                    check_out_time TIME DEFAULT '11:00:00',
                                    status VARCHAR(30),
                                    created_at TIMESTAMP DEFAULT NOW(),
                                    created_by VARCHAR(50),
                                    updated_at TIMESTAMP,
                                    updated_by VARCHAR(50)
);

CREATE TABLE booking_service.bookings (
                                  id UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
                                  property_id UUID NOT NULL,
                                  user_id UUID NOT NULL,
                                  check_in_date DATE ,
                                  check_out_date DATE ,
                                  guests_count INTEGER ,
                                  total_night DECIMAL(10,2) ,
                                  price_per_night DECIMAL(10,2) ,
                                  booking_status VARCHAR(50),
                                  vat numeric(4, 2),
                                  total_amount     numeric(20, 2),
                                  subtotal_amount  numeric(20, 2),
                                  special_requests TEXT,
                                  expires_at       timestamp,
                                  created_at TIMESTAMP DEFAULT NOW(),
                                  created_by VARCHAR(50),
                                  updated_at TIMESTAMP,
                                  updated_by VARCHAR(50)
);

CREATE TABLE booking_service.property_images (
                                         id UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
                                         property_id UUID NOT NULL,
                                         image_url VARCHAR(255) NOT NULL,
                                         caption TEXT,
                                         is_primary BOOLEAN DEFAULT FALSE,
                                         display_order INTEGER DEFAULT 0,
                                         created_at TIMESTAMP DEFAULT NOW(),
                                         created_by VARCHAR(50),
                                         updated_at TIMESTAMP,
                                         updated_by VARCHAR(50)
);

CREATE TABLE booking_service.amenities (
                                   id UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
                                   name VARCHAR(100) UNIQUE NOT NULL,
                                   description TEXT,
                                   icon VARCHAR(50),
                                   created_at TIMESTAMP DEFAULT NOW(),
                                   created_by VARCHAR(50),
                                   updated_at TIMESTAMP,
                                   updated_by VARCHAR(50)
);

CREATE TABLE booking_service.property_amenities (
                                            property_id UUID NOT NULL,
                                            amenity_id UUID NOT NULL,
                                            created_at TIMESTAMP DEFAULT NOW(),
                                            created_by VARCHAR(50),
                                            updated_at TIMESTAMP,
                                            updated_by VARCHAR(50)
);

CREATE TABLE booking_service.categories (
                                    id UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
                                    name VARCHAR(100) UNIQUE NOT NULL,
                                    description TEXT,
                                    icon VARCHAR(255),
                                    created_at TIMESTAMP DEFAULT NOW(),
                                    created_by VARCHAR(50),
                                    updated_at TIMESTAMP,
                                    updated_by VARCHAR(50)
);

CREATE TABLE booking_service.property_categories (
                                             property_id UUID NOT NULL,
                                             category_id UUID NOT NULL,
                                             created_at TIMESTAMP DEFAULT NOW(),
                                             created_by VARCHAR(50),
                                             updated_at TIMESTAMP,
                                             updated_by VARCHAR(50)
);

CREATE TABLE booking_service.holiday (
                                 id UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
                                 property_id UUID NOT NULL,
                                 start_date DATE NOT NULL,
                                 end_date DATE NOT NULL,
                                 is_available BOOLEAN DEFAULT TRUE NOT NULL,
                                 price_override DECIMAL(10,2),
                                 created_at TIMESTAMP DEFAULT NOW(),
                                 created_by VARCHAR(50),
                                 updated_at TIMESTAMP,
                                 updated_by VARCHAR(50)
);

CREATE TABLE booking_service.property_day_prices (
                                             id UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
                                             property_id UUID NOT NULL,
                                             day_of_week INT CHECK (day_of_week BETWEEN 0 AND 6) NOT NULL,
                                             price DECIMAL(10,2) NOT NULL,
                                             created_at TIMESTAMP DEFAULT NOW(),
                                             created_by VARCHAR(50),
                                             updated_at TIMESTAMP,
                                             updated_by VARCHAR(50)
);

-- ========================
-- PAYMENT SERVICE
-- ========================
CREATE TABLE payment_service.payments (
                                  id UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
                                  booking_id UUID NOT NULL,
                                  amount NUMERIC(10, 2) NOT NULL,
                                  payment_method VARCHAR(50) NOT NULL,
                                  payment_status VARCHAR(50),
                                  created_at TIMESTAMP DEFAULT NOW(),
                                  created_by VARCHAR(50),
                                  updated_at TIMESTAMP,
                                  updated_by VARCHAR(50)
);

-- ========================
-- NOTIFICATION SERVICE
-- ========================
CREATE TABLE notification_service.notifications (
                                            id UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
                                            sender_id UUID NOT NULL,
                                            receiver_id UUID,
                                            template_id UUID,
                                            created_at TIMESTAMP DEFAULT NOW(),
                                            created_by VARCHAR(50),
                                            updated_at TIMESTAMP,
                                            updated_by VARCHAR(50)
);

CREATE TABLE notification_service.template (
                                       id UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
                                       type VARCHAR(50),
                                       subject VARCHAR(255),
                                       content TEXT,
                                       status BOOLEAN,
                                       created_at TIMESTAMP DEFAULT NOW(),
                                       created_by VARCHAR(50),
                                       updated_at TIMESTAMP,
                                       updated_by VARCHAR(50)
);

-- ========================
-- RECOMMENDATION SERVICE
-- ========================
CREATE TABLE recommendation_service.tourist_attractions (
                                                    id UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
                                                    name VARCHAR(255) NOT NULL,
                                                    description TEXT,
                                                    address VARCHAR(255) NOT NULL,
                                                    city VARCHAR(100) NOT NULL,
                                                    state VARCHAR(100) NOT NULL,
                                                    country VARCHAR(100) NOT NULL,
                                                    zip_code VARCHAR(20),
                                                    latitude DECIMAL(10,8) NOT NULL,
                                                    longitude DECIMAL(11,8) NOT NULL,
                                                    category VARCHAR(50),
                                                    image_url VARCHAR(255),
                                                    website VARCHAR(255),
                                                    opening_hours TEXT,
                                                    admission_fee DECIMAL(10,2),
                                                    rating DECIMAL(3,1),
                                                    status VARCHAR(20) DEFAULT 'active' NOT NULL,
                                                    created_at TIMESTAMP DEFAULT NOW(),
                                                    created_by VARCHAR(50),
                                                    updated_at TIMESTAMP,
                                                    updated_by VARCHAR(50)
);

CREATE TABLE recommendation_service.property_attractions (
                                                     id UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
                                                     property_id UUID NOT NULL,
                                                     attraction_id UUID NOT NULL,
                                                     distance DECIMAL(10,2),
                                                     travel_time INTEGER,
                                                     recommended BOOLEAN DEFAULT TRUE,
                                                     notes TEXT,
                                                     created_at TIMESTAMP DEFAULT NOW(),
                                                     created_by VARCHAR(50),
                                                     updated_at TIMESTAMP,
                                                     updated_by VARCHAR(50)
);

CREATE TABLE recommendation_service.places (
                                       id UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
                                       name VARCHAR(255) NOT NULL,
                                       description TEXT,
                                       category VARCHAR(50) NOT NULL,
                                       sub_category VARCHAR(50),
                                       price_range VARCHAR(20),
                                       latitude DECIMAL(10,8) NOT NULL,
                                       longitude DECIMAL(11,8) NOT NULL,
                                       open_time TIME,
                                       close_time TIME,
                                       rating DECIMAL(2,1),
                                       image_url TEXT,
                                       created_at TIMESTAMP DEFAULT NOW(),
                                       created_by VARCHAR(50),
                                       updated_at TIMESTAMP,
                                       updated_by VARCHAR(50)
);

-- ========================
-- FOREIGN KEYS (cross-schema)
-- ========================

-- Booking
ALTER TABLE booking_service.bookings
    ADD CONSTRAINT fk_booking_user FOREIGN KEY (user_id) REFERENCES user_service.users(id),
    ADD CONSTRAINT fk_booking_property FOREIGN KEY (property_id) REFERENCES booking_service.properties(id);

-- Reviews
ALTER TABLE user_service.reviews
    ADD CONSTRAINT fk_reviews_user FOREIGN KEY (user_id) REFERENCES user_service.users(id),
    ADD CONSTRAINT fk_reviews_property FOREIGN KEY (property_id) REFERENCES booking_service.properties(id),
    ADD CONSTRAINT fk_reviews_booking FOREIGN KEY (booking_id) REFERENCES booking_service.bookings(id),
    ADD CONSTRAINT fk_reviews_host FOREIGN KEY (host_id) REFERENCES user_service.users(id);

-- Properties
ALTER TABLE booking_service.properties
    ADD CONSTRAINT fk_properties_host FOREIGN KEY (host_id) REFERENCES user_service.users(id);

-- Payments
ALTER TABLE payment_service.payments
    ADD CONSTRAINT fk_payment_booking FOREIGN KEY (booking_id) REFERENCES booking_service.bookings(id);

-- Notifications
ALTER TABLE notification_service.notifications
    ADD CONSTRAINT fk_notifications_sender FOREIGN KEY (sender_id) REFERENCES user_service.users(id),
    ADD CONSTRAINT fk_notifications_receiver FOREIGN KEY (receiver_id) REFERENCES user_service.users(id),
    ADD CONSTRAINT fk_notifications_template FOREIGN KEY (template_id) REFERENCES notification_service.template(id);

-- Property Amenities & Categories
ALTER TABLE booking_service.property_amenities
    ADD CONSTRAINT fk_prop_amenity_property FOREIGN KEY (property_id) REFERENCES booking_service.properties(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_prop_amenity_amenity FOREIGN KEY (amenity_id) REFERENCES booking_service.amenities(id) ON DELETE CASCADE;

ALTER TABLE booking_service.property_categories
    ADD CONSTRAINT fk_prop_cat_property FOREIGN KEY (property_id) REFERENCES booking_service.properties(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_prop_cat_category FOREIGN KEY (category_id) REFERENCES booking_service.categories(id) ON DELETE CASCADE;

-- Property Images & Prices
ALTER TABLE booking_service.property_images
    ADD CONSTRAINT fk_images_property FOREIGN KEY (property_id) REFERENCES booking_service.properties(id) ON DELETE CASCADE;

ALTER TABLE booking_service.property_day_prices
    ADD CONSTRAINT fk_price_property FOREIGN KEY (property_id) REFERENCES booking_service.properties(id);

-- Holiday
ALTER TABLE booking_service.holiday
    ADD CONSTRAINT fk_holiday_property FOREIGN KEY (property_id) REFERENCES booking_service.properties(id) ON DELETE CASCADE;

-- Property Attractions
ALTER TABLE recommendation_service.property_attractions
    ADD CONSTRAINT fk_pa_property FOREIGN KEY (property_id) REFERENCES booking_service.properties(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_pa_attraction FOREIGN KEY (attraction_id) REFERENCES recommendation_service.tourist_attractions(id) ON DELETE CASCADE;