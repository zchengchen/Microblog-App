<?php
include("db.php");
// 检查连通性
if (!$conn) {
    die("failed to connect mysql" . mysqli_connect_error());
}
mysqli_query($conn, "SET NAMES UTF8");
// 获取POST数据
$username = $_POST["username"];
$password = $_POST["password"];
// 进行查询
$query1 = "SELECT * FROM userinfo WHERE username = '{$username}'";
$query2 = "SELECT * FROM userinfo WHERE username = '{$username}' AND password = '{$password}'";
$result = mysqli_query($conn, $query1);
$userinfo;
if (mysqli_num_rows($result) > 0) {
    $result = mysqli_query($conn, $query2);
    if (mysqli_num_rows($result) > 0) {
        $row = mysqli_fetch_assoc($result);
        $userinfo = array(
            "username" => $row["username"],
            "password" => $row["password"],
            "status" => 1
        );
    } else {
        $userinfo = array(
            "username" => $username,
            "password" => $password,
            "status" => 2
        );
    }
} else {
    $userinfo = array(
        "username" => $username,
        "password" => $password,
        "status" => 0
    );
}
// 响应数据
echo json_encode($userinfo);
mysqli_close($conn);
