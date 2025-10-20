/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */
$(document).ready(function() {
    var slider = $('.banner-slider');
    var slides = slider.find('.banner-item');
    var totalSlides = slides.length;
    var currentSlide = 0;
    var slideInterval = 5000; // Tự động chuyển sau 5 giây (5000ms)
    var slideTimer;

    // Hàm chuyển đến slide cụ thể
    function showSlide(index) {
        // Loại bỏ class active khỏi slide hiện tại
        $(slides[currentSlide]).removeClass('active');
        
        // Cập nhật chỉ mục mới
        currentSlide = (index + totalSlides) % totalSlides;
        
        // Thêm class active cho slide mới
        $(slides[currentSlide]).addClass('active');
    }

    // Hàm chuyển đến slide tiếp theo
    function nextSlide() {
        showSlide(currentSlide + 1);
    }

    // Hàm chuyển đến slide trước
    function prevSlide() {
        showSlide(currentSlide - 1);
    }

    // Tự động chạy slideshow
    function startTimer() {
        slideTimer = setInterval(nextSlide, slideInterval);
    }
    
    // Dừng timer
    function stopTimer() {
        clearInterval(slideTimer);
    }
    
    // Khởi động timer lần đầu
    startTimer();
    
    // Gán sự kiện cho các nút điều hướng
    $('.prev-btn').on('click', function() {
        stopTimer();
        prevSlide();
        startTimer(); // Tiếp tục chạy sau khi người dùng tương tác
    });

    $('.next-btn').on('click', function() {
        stopTimer();
        nextSlide();
        startTimer(); // Tiếp tục chạy sau khi người dùng tương tác
    });

    // Tùy chọn: Dừng khi rê chuột vào, tiếp tục khi chuột rời đi
    slider.hover(stopTimer, startTimer);
});

