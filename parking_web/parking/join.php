<?php
    $member_id = $_GET["member_id"];
    $password = $_GET["password"];
    $car_category = $_GET["car_category"];
    $car_type = $_GET["car_type"];
    $car_num = $_GET["car_num"];

    $connect=mysqli_connect('localhost','parkuser','parkpass','parking','3306','/tmp/mysql.sock');  

    if(!strcmp($car_type,""))
        $car_type = "없음";

    if(!strcmp($car_num, ""))
        $car_num = "없음";

    $query = "insert into member values ('$member_id', '$password', '$car_category', '$car_type', '$car_num')";

    //echo $query;

    if(mysqli_query($connect, $query))
        echo "success";
    else
        echo "fail";

    mysqli_close($connect);
?>