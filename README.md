# Hướng dẫn tạo môi trường lab CTF với ứng dụng Flag Search

## Mô tả
Hướng dẫn này sẽ giúp bạn thiết lập một môi trường lab CTF sử dụng Docker với Jenkins, tạo một ứng dụng web đơn giản có khả năng hiển thị các flag dựa trên thao tác của người dùng.

## Cấu trúc dự án

- `index.html`: File HTML chính chứa giao diện và các script JavaScript.
- `style.css`: Tệp chứa các quy tắc CSS để tạo phong cách cho giao diện.
- `Dockerfile`: File để cấu hình Docker cho môi trường.
- Thư mục `flags`: Chứa các flag cho từng challenge.

## Bước 1: Tạo Docker container cho Jenkins

Chạy lệnh sau để tạo và khởi động Docker container Jenkins:

```bash
docker run -p 8080:8080 -p 50000:50000 --restart=on-failure jenkins/jenkins:2.441-jdk17
```
## Tạo ứng dụng web

Tạo file index.html với nội dung sau:

```
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="style.css">
    <title>Flag Search</title>
</head>
<body>
    <h1>Chào mừng đến với Flag Search</h1>
    <input type="text" id="searchInput" placeholder="Nhập từ khóa...">
    <button id="searchButton">Tìm kiếm</button>

    <div id="flagContainer"></div>

    <script>
        // Flag 1: Hiển thị ngay sau 5 giây
        setTimeout(function() {
            var flag1 = "FLAG{Hidden_Flag_1234}";
            document.getElementById('flagContainer').innerHTML += "<p>Bạn đã tìm thấy flag: " + flag1 + "</p>";
        }, 5000);

        // Kích hoạt flag khi người dùng nhấn vào nút
        document.getElementById('searchButton').onclick = function() {
            var flag2 = "FLAG{Button_Click_Flag_5678}";
            alert("Bạn đã tìm thấy flag: " + flag2);
        };
    </script>
</body>
</html>
```
Tạo file style.css để thêm phong cách cho ứng dụng:

```
body {
    font-family: Arial, sans-serif;
    background-color: #f4f4f4;
    padding: 20px;
}

h1 {
    color: #333;
}

input {
    padding: 10px;
    width: 200px;
    margin-right: 10px;
}

button {
    padding: 10px 20px;
    background-color: #5cb85c;
    color: white;
    border: none;
    cursor: pointer;
}

button:hover {
    background-color: #4cae4c;
}

#flagContainer {
    margin-top: 20px;
}
```
## Bước 3: Tạo thư mục chứa flag cho từng challenge
Tạo thư mục flags trong thư mục dự án của bạn và thêm các file chứa flag cho từng challenge.

## Bước 4: Giấu flag thứ ba trong JavaScript

Chỉnh sửa script JavaScript để flag thứ ba chỉ hiển thị khi người dùng nhập từ khóa tìm kiếm cụ thể. Dưới đây là ví dụ cho flag thứ ba:

```
<script>
    // Flag 3: Sử dụng JavaScript để ẩn flag
    function showFlagOnSearch() {
        var input = document.getElementById('searchInput').value;
        if (input === "tukhoa123") { // Thay đổi từ khóa theo yêu cầu
            var flag3 = "FLAG{JavaScript_Secret_Flag_9012}";
            alert("Bạn đã tìm thấy flag: " + flag3);
        }
    }

    // Kích hoạt khi nhấn nút tìm kiếm
    document.getElementById('searchButton').onclick = function() {
        showFlagOnSearch();
    };
</script>
```
## Bước 5: Chạy ứng dụng

Mở file index.html trong trình duyệt của bạn để chạy ứng dụng.

Thực hiện thao tác tìm kiếm và tìm các flag đã được chỉ định.

## Kết luận

Bây giờ bạn đã có một môi trường lab CTF đơn giản với ứng dụng web có khả năng hiển thị các flag khác nhau. Hãy thử nghiệm và cải thiện ứng dụng theo ý muốn của bạn!

```
Hy vọng nội dung này giúp bạn ghi lại quá trình thiết lập một môi trường lab CTF cho học viên. Nếu cần thêm thông tin, bạn có thể bổ sung vào file này.
```
