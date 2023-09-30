<?php

include("db.php");

// 检查数据库是否连接成功
if (!$conn) {
    die("failed to connect mysql" . mysqli_connect_error());
}

mysqli_query($conn, "SET NAMES UTF8");
$username = $_POST['username'];

$query = "SELECT line, userfrom, title, content FROM directmail WHERE userto = '{$username}'";
$result = mysqli_query($conn, $query);

$dms = [];
if (mysqli_num_rows($result) > 0) {
    while ($row = mysqli_fetch_assoc($result)) {
        $dm = array(
            "id" => $row["line"],
            "userfrom" => $row["userfrom"],
            "title" => $row["title"],
            "content" => $row["content"]
        );
        array_push($dms, $dm);
    }
}

$mydm = array (
    "dms" => $dms
);

echo json_encode($mydm, JSON_UNESCAPED_UNICODE);
mysqli_close($conn);