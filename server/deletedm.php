<?php
include("db.php");

if (!$conn) {
    die("failed to connect mysql" . mysqli_connect_error());
}
mysqli_query($conn, "SET NAMES UTF8");

$id = $_POST['dm_id'];

$delete = "DELETE FROM directmail WHERE line = {$id}";

$result = mysqli_query($conn, $delete);
mysqli_close($conn);
