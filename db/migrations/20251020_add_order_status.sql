-- Migration: thêm cột status và đảm bảo có order_date (nếu DB chưa có)
ALTER TABLE orders
  ADD COLUMN IF NOT EXISTS status VARCHAR(50) DEFAULT 'Chưa xử lý';

ALTER TABLE orders
  ADD COLUMN IF NOT EXISTS order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP;