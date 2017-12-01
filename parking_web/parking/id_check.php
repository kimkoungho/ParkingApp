<?php
    $member_id = $_GET["member_id"];
    //$member_id = 'koung@naver.com';

    $connect=mysqli_connect('localhost','parkuser','parkpass','parking','3306','/tmp/mysql.sock');  

    $query = "select count(*) from member where id = '$member_id'";

    $result=mysqli_query($connect, $query);

    if($data=mysqli_fetch_row($result))
        echo $data[0];//0 or 1

    mysqli_close($connect);
?>