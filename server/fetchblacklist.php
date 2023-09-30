<?php
include("db.php");

// 检查数据库是否连接成功
if (!$conn) {
    die("failed to connect mysql" . mysqli_connect_error());
}

$username;
mysqli_query($conn, "SET NAMES UTF8");
$username = $_POST['username'];

$query = "SELECT * FROM blacklist WHERE username = '{$username}'";
$result = mysqli_query($conn, $query);

if(mysqli_num_rows($result) != 0) {
    $row = mysqli_fetch_assoc($result);
    $blacklist = array(
        "username" => $row["username"],
        "blacklist" => $row["black_list"]
    );
}

$user_bl = array(
    "userbl" => $blacklist
);

echo json_encode($user_bl, JSON_UNESCAPED_UNICODE);
mysqli_close($conn);
