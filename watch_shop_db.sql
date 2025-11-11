-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Máy chủ: 127.0.0.1
-- Thời gian đã tạo: Th10 05, 2025 lúc 03:16 PM
-- Phiên bản máy phục vụ: 10.4.32-MariaDB
-- Phiên bản PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Cơ sở dữ liệu: `watch_shop_db`
--

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `carts`
--

CREATE TABLE `carts` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `carts`
--

INSERT INTO `carts` (`id`, `user_id`) VALUES
(2, 2),
(1, 3),
(3, 4),
(4, 5);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `cart_items`
--

CREATE TABLE `cart_items` (
  `id` int(11) NOT NULL,
  `cart_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  `quantity` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `categories`
--

CREATE TABLE `categories` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `categories`
--

INSERT INTO `categories` (`id`, `name`) VALUES
(1, 'Tất cả sản phẩm'),
(4, 'Đồng Hồ Nữ'),
(5, 'Đồng Hồ Nam'),
(6, 'Đồng Hồ Thông Minh');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `contacts`
--

CREATE TABLE `contacts` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `message` text NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `contacts`
--

INSERT INTO `contacts` (`id`, `name`, `email`, `message`, `created_at`) VALUES
(1, 'Nguyễn Danh Thái', 'hatakekakashi.lufyy@gmail.com', 'hehehe', '2025-10-23 09:10:08'),
(2, 'nguyen thai', 'hatakekakashi.lufyy@gmail.com', '412', '2025-10-23 11:40:30'),
(3, 'nguyen thai', 'khachhang1@example.com', '2123545u', '2025-10-23 17:07:39');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `orders`
--

CREATE TABLE `orders` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `order_date` timestamp NOT NULL DEFAULT current_timestamp(),
  `total_money` double NOT NULL,
  `customer_name` varchar(100) NOT NULL,
  `customer_address` varchar(255) NOT NULL,
  `customer_phone` varchar(15) NOT NULL,
  `payment_method` enum('COD','BANK','MOMO') NOT NULL,
  `status` enum('Chưa thanh toán','Đang xử lý','Đang giao hàng','Đã giao thành công','Đã hủy') NOT NULL DEFAULT 'Chưa thanh toán'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `orders`
--

INSERT INTO `orders` (`id`, `user_id`, `order_date`, `total_money`, `customer_name`, `customer_address`, `customer_phone`, `payment_method`, `status`) VALUES
(1, 2, '2025-10-17 11:49:02', 6000000, 'Nguyễn Văn A', 'TP. Hồ Chí Minh', '0987654321', 'COD', 'Đã giao thành công'),
(2, 2, '2025-10-20 15:45:12', 4200000, 'Nguyễn Văn A', 'TP. Hồ Chí Minh', '0987654321', 'COD', 'Đã giao thành công'),
(3, 2, '2025-10-21 12:40:50', 46200000, 'Nguyễn Văn A', 'TP. Hồ Chí Minh', '0987654321', 'COD', 'Đã giao thành công'),
(4, 2, '2025-10-21 14:25:30', 4200000, 'Nguyễn Văn A', 'TP. Hồ Chí Minh', '0987654321', 'MOMO', 'Đã hủy'),
(5, 2, '2025-10-21 14:39:45', 4500000, 'Nguyễn Văn A', 'TP. Hồ Chí Minh', '0987654321', 'BANK', 'Đã giao thành công'),
(6, 2, '2025-10-21 15:17:11', 22200000, 'Nguyễn Văn A', 'TP. Hồ Chí Minh', '0987654321', 'BANK', 'Đã giao thành công'),
(7, 2, '2025-10-21 15:22:03', 4200000, 'Nguyễn Văn A', 'TP. Hồ Chí Minh', '0987654321', 'COD', 'Đã giao thành công'),
(8, 4, '2025-10-22 05:53:39', 350000000, 'Nguyễn Danh Thái', 'Hiệp Hòa', '0972055143', 'COD', 'Đã giao thành công'),
(9, 2, '2025-10-22 06:09:51', 4200000, 'Nguyễn Văn A', 'TP. Hồ Chí Minh', '0987654321', 'COD', 'Đã giao thành công'),
(10, 4, '2025-10-22 06:24:04', 4200000, 'Nguyễn Danh Thái', 'Hiệp Hòa', '0972055143', 'COD', 'Đã giao thành công'),
(11, 4, '2025-10-22 06:24:18', 12000000, 'Nguyễn Danh Thái', 'Hiệp Hòa', '0972055143', 'MOMO', 'Đã giao thành công'),
(12, 2, '2025-10-22 09:37:35', 2500000000, 'Nguyễn Văn A', 'TP. Hồ Chí Minh', '0987654321', 'COD', 'Đã giao thành công'),
(13, 2, '2025-10-22 10:01:04', 350000000, 'Nguyễn Văn A', 'TP. Hồ Chí Minh', '0987654321', 'COD', 'Đã giao thành công'),
(14, 2, '2025-10-22 10:50:14', 6500000, 'Nguyễn Văn A', 'TP. Hồ Chí Minh', '0987654321', 'COD', 'Đã hủy'),
(15, 2, '2025-10-22 10:50:41', 4200000, 'Nguyễn Văn A', 'TP. Hồ Chí Minh', '0987654321', 'COD', 'Đã hủy'),
(16, 2, '2025-10-22 10:51:05', 5500000, 'Nguyễn Văn A', 'TP. Hồ Chí Minh', '0987654321', 'MOMO', 'Đã hủy'),
(17, 2, '2025-10-22 11:22:08', 14000000, 'Nguyễn Văn A', 'TP. Hồ Chí Minh', '0987654321', 'BANK', 'Đã hủy'),
(18, 2, '2025-10-22 11:30:01', 21000000, 'Nguyễn Văn A', 'TP. Hồ Chí Minh', '0987654321', 'COD', 'Đã giao thành công'),
(19, 2, '2025-10-23 07:43:34', 4200000, 'Nguyễn Văn A', 'TP. Hồ Chí Minh', '0987654321', 'COD', 'Đã giao thành công'),
(20, 2, '2025-10-23 16:41:30', 468350000, 'Nguyễn Văn A', 'TP. Hồ Chí Minh', '0987654321', 'COD', 'Đã giao thành công'),
(21, 2, '2025-10-24 05:58:10', 18200000, 'Nguyễn Văn A', 'TP. Hồ Chí Minh', '0987654321', 'MOMO', 'Đã giao thành công');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `order_details`
--

CREATE TABLE `order_details` (
  `id` int(11) NOT NULL,
  `order_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  `quantity` int(11) NOT NULL,
  `price` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `order_details`
--

INSERT INTO `order_details` (`id`, `order_id`, `product_id`, `quantity`, `price`) VALUES
(1, 1, 25, 1, 6000000),
(2, 2, 30, 1, 4200000),
(3, 3, 29, 3, 14000000),
(4, 3, 30, 1, 4200000),
(5, 4, 30, 1, 4200000),
(6, 5, 26, 1, 4500000),
(7, 6, 30, 1, 4200000),
(8, 6, 17, 1, 18000000),
(9, 7, 30, 1, 4200000),
(10, 8, 1, 1, 350000000),
(11, 9, 30, 1, 4200000),
(12, 10, 30, 1, 4200000),
(13, 11, 25, 2, 6000000),
(14, 12, 4, 1, 2500000000),
(15, 13, 1, 1, 350000000),
(16, 14, 27, 1, 6500000),
(17, 15, 30, 1, 4200000),
(18, 16, 28, 1, 5500000),
(19, 17, 29, 1, 14000000),
(20, 18, 30, 5, 4200000),
(21, 19, 30, 1, 4200000),
(22, 20, 29, 7, 14000000),
(24, 21, 29, 1, 14000000),
(25, 21, 30, 1, 4200000);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `password_resets`
--

CREATE TABLE `password_resets` (
  `email` varchar(255) NOT NULL,
  `token` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `password_resets`
--

INSERT INTO `password_resets` (`email`, `token`, `created_at`) VALUES
('hatakekakashi.lufyy@gmail.com', '_t52W0OE-D_25mWP933oOw0HH2HuvlVhblsuBSQasQ0', '2025-10-24 05:57:46'),
('danhthai07032004ndt@gmail.com', 'Rbv_oQx5Rc_x5p__eUIIxB2NNEN7sjDPpPFI8-8TOsk', '2025-10-31 04:23:56');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `products`
--

CREATE TABLE `products` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` text DEFAULT NULL,
  `price` double NOT NULL,
  `cost_price` decimal(13,2) DEFAULT 0.00,
  `image` varchar(255) DEFAULT NULL,
  `category_id` int(11) DEFAULT NULL,
  `stock` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `products`
--

INSERT INTO `products` (`id`, `name`, `description`, `price`, `cost_price`, `image`, `category_id`, `stock`) VALUES
(1, 'Rolex Submariner Date', 'Biểu tượng của sự sang trọng và chính xác. Chống nước đến 300 mét.', 350002000, 340001000.00, 'images/rolex-sub.jpg', 5, 10),
(2, 'Omega Seamaster Diver 300M', 'Đồng hồ của điệp viên James Bond. Mạnh mẽ, nam tính và đáng tin cậy.', 155001000, 145000000.00, 'images/omega-sea.jpg', 5, 15),
(3, 'TAG Heuer Carrera Chronograph', 'Lấy cảm hứng từ những cuộc đua xe huyền thoại. Thiết kế thể thao và đẳng cấp.', 130002000, 129002000.00, 'images/tag-carrera.jpg', 5, 100),
(4, 'Patek Philippe Nautilus', 'Thiết kế độc đáo với vành bezel hình bát giác bo tròn.', 2500001000, 2490001000.00, 'images/patek-nau.jpg', 5, 49),
(5, 'Audemars Piguet Royal Oak', 'Một trong những chiếc đồng hồ thể thao xa xỉ đầu tiên trên thế giới.', 800001000, 790001000.00, 'images/ap-royal.jpg', 5, 8),
(6, 'Longines HydroConquest', 'Kết hợp giữa sự thanh lịch và hiệu suất cao. Phù hợp cho các hoạt động dưới nước.', 40002000, 39002000.00, 'images/longines-hydro.jpg', 5, 30),
(7, 'Tissot PRX Powermatic 80', 'Thiết kế cổ điển thập niên 70 với bộ máy tự động có khả năng trữ cót 80 giờ.', 20004000, 19003000.00, 'images/tissot-prx.jpg', 5, 50),
(8, 'Seiko Prospex SPB143', 'Sự tái hiện của chiếc đồng hồ lặn đầu tiên của Seiko năm 1965.', 27999000, 26999000.00, 'images/seiko-prospex.jpg', 5, 40),
(9, 'Hamilton Khaki Field Mechanical', 'Đồng hồ quân đội cổ điển, bền bỉ và dễ đọc.', 15002000, 14002000.00, 'images/hamilton-khaki.jpg', 5, 60),
(10, 'Casio G-Shock GW-5000U-1JF', 'Bền bỉ tuyệt đối, chống sốc và chống nước 200M, có pin năng lượng mặt trời.', 8001000, 7969000.00, 'images/gshock-gw5000.jpg', 5, 100),
(11, 'Cartier Tank Must SolarBeat', 'Thiết kế thanh lịch, cổ điển của dòng Tank với bộ máy năng lượng mặt trời.', 75001000, 74957000.00, 'images/cartier-tank.jpg', 4, 12),
(12, 'Rolex Lady-Datejust', 'Vẻ đẹp vượt thời gian, là biểu tượng của sự nữ tính và sang trọng.', 280001000, 279967000.00, 'images/rolex-lady.jpg', 4, 18),
(13, 'Omega Constellation Manhattan', 'Thiết kế đặc trưng với bốn vấu \"griffes\" và mặt số tinh xảo.', 180001000, 170000000.00, 'images/omega-constellation.jpg', 4, 22),
(14, 'Jaeger-LeCoultre Reverso Classic', 'Thiết kế mặt số lật độc đáo, mang tính biểu tượng của nghệ thuật chế tác đồng hồ.', 210001000, 200001000.00, 'images/jlc-reverso.jpg', 4, 9),
(15, 'Chopard Happy Sport', 'Những viên kim cương nhảy múa tự do giữa hai lớp kính sapphire.', 15000000000, 14900000000.00, 'images/chopard-happy.jpg', 4, 15),
(16, 'Longines La Grande Classique', 'Thiết kế siêu mỏng, thanh lịch và tinh tế.', 34976000, 34981000.00, 'images/longines-grande.jpg', 4, 40),
(17, 'Tissot Le Locle Automatic Lady', 'Vẻ đẹp cổ điển mang tên thị trấn nơi Tissot được khai sinh.', 14005000, 10851000.00, 'images/tissot-lelocle.jpg', 4, 55),
(18, 'Michael Kors Parker', 'Thiết kế thời trang, lấp lánh với viền đá pha lê.', 7000000, 6000000.00, 'images/mk-parker.jpg', 4, 80),
(19, 'Daniel Wellington Petite', 'Thiết kế tối giản, hiện đại và dễ dàng phối đồ.', 4000000, 3900000.00, 'images/dw-petite.jpg', 4, 120),
(20, 'Fossil Jacqueline', 'Phong cách vintage với chữ số La Mã và mặt số tinh tế.', 3500000, 3300000.00, 'images/fossil-jac.jpg', 4, 90),
(21, 'Apple Watch Series 9', 'Đồng hồ thông minh hàng đầu với các tính năng sức khỏe và kết nối tiên tiến.', 11008000, 10851000.00, 'images/apple-watch9.jpg', 6, 200),
(22, 'Samsung Galaxy Watch 6 Classic', 'Thiết kế cổ điển với viền bezel xoay vật lý, hệ điều hành Wear OS mạnh mẽ.', 9000000, 8900000.00, 'images/galaxy-watch6.jpg', 6, 180),
(23, 'Garmin Fenix 7 Pro', 'Đồng hồ thể thao chuyên dụng, pin năng lượng mặt trời, GPS đa băng tần.', 20399000, 2100000.00, 'images/garmin-fenix7.jpg', 6, 70),
(24, 'Fitbit Sense 2', 'Tập trung vào theo dõi sức khỏe toàn diện: giấc ngủ, căng thẳng, và hoạt động.', 7500000, 7050000.00, 'images/fitbit-sense2.jpg', 6, 150),
(25, 'Huawei Watch GT 4', 'Thời lượng pin ấn tượng, thiết kế thời trang và nhiều chế độ luyện tập.', 6000000, 4998000.00, 'images/huawei-gt4.jpg', 6, 158),
(26, 'Amazfit GTR 4', 'GPS băng tần kép chính xác, thiết kế đẹp và giá cả phải chăng.', 4500000, 40000000.00, 'images/amazfit-gtr4.jpg', 6, 130),
(27, 'Citizen Eco-Drive Chandler', 'Công nghệ Eco-Drive chuyển hóa mọi nguồn sáng thành năng lượng.', 6500000, 6000000.00, 'images/citizen-eco.jpg', 5, 75),
(28, 'Orient Bambino Version 7', 'Đồng hồ dress watch cổ điển với mặt kính cong quyến rũ.', 5500000, 4500000.00, 'images/orient-bambino.jpg', 5, 85),
(29, 'Bulova Lunar Pilot', 'Tái hiện chiếc đồng hồ đã cùng phi hành gia Apollo 15 lên mặt trăng.', 14000000, 13837000.00, 'images/bulova-lunar.jpg', 5, 37),
(30, 'Skagen Grenen Solar Halo', 'Thiết kế tối giản theo phong cách Đan Mạch, sử dụng năng lượng mặt trời.', 4200000, 3998000.00, 'images/skagen-grenen.jpg', 4, 58);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `fullname` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `phone` varchar(15) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `role` int(1) NOT NULL DEFAULT 0 COMMENT '0: User, 1: Admin'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `fullname`, `email`, `phone`, `address`, `role`) VALUES
(2, 'khachhang1', '$2a$10$0cEBxn4hxpkqkzk/lapLXemB53m15UIxz5QsUBO4TEembb.L/Ftq6', 'Nguyễn Văn A', 'khachhang1@example.com', '0987654321', 'TP. Hồ Chí Minh', 1),
(3, 'admin1', '$2a$10$dT50kxEaubWKz5r5aSLmU.YqIO0DX0A0RrLGHlNmrLmaxTrpa62KS', 'Nguyễn Danh Thái', 'danhthai07032004ndt@gmail.com', '0972055143', 'Hiệp Hòa', 1),
(4, 'khachhang2', '$2a$10$076Oltaltj9rBLiEwoXbmuId4/6gYNyfDiQ2.msT1i4NVNAXdqGZS', 'Nguyễn Danh Thái', 'hatakekakashi.lufyy@gmail.com', '0972055143', 'Hiệp Hòa', 0),
(5, 'khachhang3', '$2a$10$oxEuvhFks7uv0o/r16H/VemdknyhB7WWslIz9A8ISRmPbx3k5E3uW', 'Nguyễn Danh Thái', 'nguyenthihuonght93@gmail.com', '0972055142', 'Hiệp Hòa', 0);

--
-- Chỉ mục cho các bảng đã đổ
--

--
-- Chỉ mục cho bảng `carts`
--
ALTER TABLE `carts`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uk_user_id` (`user_id`);

--
-- Chỉ mục cho bảng `cart_items`
--
ALTER TABLE `cart_items`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uk_cart_product` (`cart_id`,`product_id`),
  ADD KEY `fk_item_product` (`product_id`);

--
-- Chỉ mục cho bảng `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`id`);

--
-- Chỉ mục cho bảng `contacts`
--
ALTER TABLE `contacts`
  ADD PRIMARY KEY (`id`);

--
-- Chỉ mục cho bảng `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_order_user` (`user_id`);

--
-- Chỉ mục cho bảng `order_details`
--
ALTER TABLE `order_details`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_detail_order` (`order_id`),
  ADD KEY `fk_detail_product` (`product_id`);

--
-- Chỉ mục cho bảng `password_resets`
--
ALTER TABLE `password_resets`
  ADD KEY `password_resets_email_index` (`email`),
  ADD KEY `password_resets_token_index` (`token`);

--
-- Chỉ mục cho bảng `products`
--
ALTER TABLE `products`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_product_category` (`category_id`);

--
-- Chỉ mục cho bảng `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT cho các bảng đã đổ
--

--
-- AUTO_INCREMENT cho bảng `carts`
--
ALTER TABLE `carts`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT cho bảng `cart_items`
--
ALTER TABLE `cart_items`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=27;

--
-- AUTO_INCREMENT cho bảng `categories`
--
ALTER TABLE `categories`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT cho bảng `contacts`
--
ALTER TABLE `contacts`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT cho bảng `orders`
--
ALTER TABLE `orders`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=22;

--
-- AUTO_INCREMENT cho bảng `order_details`
--
ALTER TABLE `order_details`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=26;

--
-- AUTO_INCREMENT cho bảng `products`
--
ALTER TABLE `products`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=37;

--
-- AUTO_INCREMENT cho bảng `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- Các ràng buộc cho các bảng đã đổ
--

--
-- Các ràng buộc cho bảng `carts`
--
ALTER TABLE `carts`
  ADD CONSTRAINT `fk_cart_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Các ràng buộc cho bảng `cart_items`
--
ALTER TABLE `cart_items`
  ADD CONSTRAINT `fk_item_cart` FOREIGN KEY (`cart_id`) REFERENCES `carts` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_item_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE;

--
-- Các ràng buộc cho bảng `orders`
--
ALTER TABLE `orders`
  ADD CONSTRAINT `fk_order_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Các ràng buộc cho bảng `order_details`
--
ALTER TABLE `order_details`
  ADD CONSTRAINT `fk_detail_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_detail_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Các ràng buộc cho bảng `products`
--
ALTER TABLE `products`
  ADD CONSTRAINT `fk_product_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
