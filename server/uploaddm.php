<?php
include("db.php");

// 检查数据库是否连接成功
if (!$conn) {
    die("failed to connect mysql" . mysqli_connect_error());
}

mysqli_query($conn, "SET NAMES UTF8");

$from = $_POST['from'];
$to = $_POST['to'];
$content =  $_POST['content'];
$title = $_POST['title'];

$query = "INSERT INTO directmail (line ,userfrom, userto, title, content) values (NULL, '{$from}', '{$to}', '{$title}', '{$content}')";
$result = mysqli_query($conn, $query);
mysqli_close($conn);