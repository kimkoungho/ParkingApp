<?php
    $cmd = $_GET["cmd"];
    $member_id = $_GET["member_id"];

    $connect=mysqli_connect('localhost','parkuser','parkpass','parking','3306','/tmp/mysql.sock');  

    $query = "";
    if(!strcmp($cmd,"withdrawl")){
        $query = "delete from member where id = '$member_id'";
    }else if(!strcmp($cmd, "car_category")){
        $car_category = $_GET["car_category"];
        $query = "update member set car_category = '$car_category' where id = '$member_id'";
    }else if(!strcmp($cmd, "car_type")){
        $car_type = $_GET["car_type"];
        $query = "update member set car_type = '$car_type' where id = '$member_id'";
    }else if(!strcmp($cmd, "car_num")){
        $car_num = $_GET["car_num"];
        $query = "update member set car_num = '$car_num' where id = '$member_id'";
    }else if(!strcmp($cmd, "password")){
        $password = $_GET["password"];
        $query = "update member set password = '$password' where id = '$member_id'";
    }else{

    }

    if(mysqli_query($connect, $query))
        echo "success";
    else
        echo "fail";

    mysqli_close($connect);
?>