<?php
include("db.php");
// 检查数据库是否连接成功
if (!$conn) {
    die("failed to connect mysql" . mysqli_connect_error());
}
mysqli_query($conn, "SET NAMES UTF8");
// 获取网络请求
$username = $_POST["username"];
$password = $_POST["password"];
$user_icon = $_POST["iconPath"];
if ($user_icon == "") {
    $user_icon = null;
}
// 进行查询和插入
$query = "SELECT * FROM userinfo WHERE username = '{$username}'";
$insert = "INSERT INTO userinfo (username, password) values ('{$username}', '{$password}')";

$update_blacklist = "INSERT INTO blacklist (username, black_list) values ('{$username}', '@')";

$result = mysqli_query($conn, $query);
$userinfo;
if (mysqli_num_rows($result) > 0) {
    $userinfo = array(
        "username" => $username,
        "password" => $password,
        "status" => 2
    );
} else {
    if (mysqli_query($conn, $insert)) {
        $userinfo = array(
            "username" => $username,
            "password" => $password,
            "status" => 1
        );
    } else {
        $userinfo = array(
            "username" => $username,
            "password" => $password,
            "status" => 0
        );
    }
}

mysqli_query($conn, $update_blacklist);
// 响应数据
echo json_encode($userinfo);
mysqli_close($conn);
