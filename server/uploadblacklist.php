<?php
include("db.php");

// 检查数据库是否连接成功
if (!$conn) {
    die("failed to connect mysql" . mysqli_connect_error());
}

$username;
$newblacklist;
mysqli_query($conn, "SET NAMES UTF8");
$username = $_POST['username'];
$newblacklist = $_POST['blacklist'];

$query = "UPDATE blacklist SET black_list = '{$newblacklist}' WHERE username = '{$username}'";
$result = mysqli_query($conn, $query);
mysqli_close($conn);