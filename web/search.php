<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>Kết quả tìm kiếm</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<!-- Header -->
<header>
    <div class="header-container">
        <div class="logo">
            <img src="images/logo.png" alt="OPEN UNIVERSITY Logo">
        </div>
        <div class="navbar">
            <nav>
                <ul>
                    <li><a href="index.html">Trang Chủ</a></li>
                    <li><a href="#">Giới Thiệu</a></li>
                    <li><a href="#">Tuyển Sinh</a></li>
                    <li><a href="#">Đào Tạo</a></li>
                    <li><a href="#">Sinh Viên</a></li>
                    <li><a href="#">Liên Hệ</a></li>
                </ul>
            </nav>
        </div>
        <div class="search-bar">
            <form action="search.php" method="GET">
                <input type="text" name="search" placeholder="Tìm kiếm...">
                <button type="submit">Tìm kiếm</button>
            </form>
        </div>
    </div>
</header>

<!-- Content chính -->
<main>
    <section class="search-results">
        <h1>Kết quả tìm kiếm</h1>
        <?php
            if (isset($_GET['search'])) {
                $searchTerm = $_GET['search'];

                // Hiển thị kết quả tìm kiếm
                echo "<p>Bạn đã tìm kiếm: " . htmlspecialchars($searchTerm, ENT_QUOTES, 'UTF-8') . "</p>";

                // Kiểm tra nếu chuỗi chứa XSS và hiển thị flag
                if (strpos(strtolower($searchTerm), '<script>') !== false) {
                    echo "<p><strong>Flag 2: FLAG{JavaScript_Hidden_Flag_5678}</strong></p>";
                } else {
                    echo "<p>Không tìm thấy flag.</p>";
                }
            } else {
                echo "<p>Vui lòng nhập từ khóa để tìm kiếm.</p>";
            }
        ?>
    </section>
</main>

<!-- Footer -->
<footer>
    <div class="footer-container">
        <div class="footer-info">
            <p>Trường Đại học mở TP.Hồ Chí Minh (OPEN UNIVERSITY)</p>
            <p>475A Điện Biên Phủ, Phường 25, Quận Bình Thạnh, TP. Hồ Chí Minh</p>
            <p>Điện thoại: (028) 5321 7777 | Email: info@university.edu.lab</p>
        </div>
    </div>
</footer>

</body>
</html>

