-- ============================================================
-- Harvest Hall Online Food Ordering System
-- Database Schema + Seed Data
-- Run against: harvest_hall_db
-- ============================================================

CREATE DATABASE IF NOT EXISTS harvest_hall_db;
USE harvest_hall_db;

-- ---- Menu Items ----
CREATE TABLE IF NOT EXISTS menu_items (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100)    NOT NULL,
    category    VARCHAR(50)     NOT NULL,
    price       DECIMAL(8,2)    NOT NULL,
    description VARCHAR(255),
    available   TINYINT(1)      NOT NULL DEFAULT 1
);

-- ---- Orders ----
CREATE TABLE IF NOT EXISTS orders (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_name        VARCHAR(100)   NOT NULL,
    customer_email       VARCHAR(150)   NOT NULL,
    order_time           DATETIME       NOT NULL,
    pickup_time          DATETIME,
    status               VARCHAR(20)    NOT NULL DEFAULT 'PENDING',
    total_amount         DECIMAL(10,2)  NOT NULL,
    special_instructions TEXT
);

-- ---- Order Items ----
CREATE TABLE IF NOT EXISTS order_items (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id        BIGINT          NOT NULL,
    menu_item_id    BIGINT          NOT NULL,
    quantity        INT             NOT NULL,
    price_at_order  DECIMAL(8,2)    NOT NULL,
    FOREIGN KEY (order_id)     REFERENCES orders(id)     ON DELETE CASCADE,
    FOREIGN KEY (menu_item_id) REFERENCES menu_items(id) ON DELETE RESTRICT
);

-- ============================================================
-- Seed Data – Harvest Hall Menu
-- ============================================================
INSERT INTO menu_items (name, category, price, description, available) VALUES
-- Burgers
('All Beef Burger',        'Burgers',   8.99,  'Classic beef patty with lettuce, tomato, onion',         1),
('Chicken Burger',         'Burgers',   8.49,  'Grilled chicken breast with mayo and pickles',           1),
('Veggie Burger',          'Burgers',   7.99,  'Plant-based patty with fresh vegetables',               1),
-- Sides
('French Fries',           'Sides',     3.49,  'Crispy golden fries',                                   1),
('Onion Rings',            'Sides',     3.99,  'Beer-battered onion rings',                             1),
('Side Salad',             'Sides',     4.29,  'Mixed greens with house dressing',                      1),
-- Wraps
('Chicken Caesar Wrap',    'Wraps',     9.49,  'Grilled chicken, romaine, parmesan in a flour tortilla', 1),
('BLT Wrap',               'Wraps',     8.99,  'Bacon, lettuce, tomato wrap with chipotle sauce',       1),
-- Drinks
('Fountain Drink',         'Drinks',    2.49,  'Pepsi, Diet Pepsi, 7UP, or OJ',                         1),
('Bottled Water',          'Drinks',    1.99,  '500ml spring water',                                    1),
('Coffee',                 'Drinks',    2.99,  'Freshly brewed medium roast',                           1),
-- Specials
('Daily Special',          'Specials',  10.99, 'Ask staff for today\'s special',                        1);
