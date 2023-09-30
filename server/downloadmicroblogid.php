<?php
include("db.php");
// 检查数据库是否连接成功
if (!$conn) {
    die("failed to connect mysql" . mysqli_connect_error());
}

$query = "SELECT username FROM userinfo";
$result = mysqli_query($conn, $query);
$usernameArray = [];

if (mysqli_num_rows($result) > 0) {
    while ($row = mysqli_fetch_assoc($result)) {
        $username = array(
            "username" => $row["username"]
        );
        array_push($usernameArray, $username);
    }
}

$usernameArray = array(
    "user_array" => $usernameArray
);
echo json_encode($usernameArray, JSON_UNESCAPED_UNICODE);
mysqli_close($conn);