<?php
    $member_id = $_GET["member_id"];
    $park_no = $_GET["park_no"];
    $score = $_GET["score"];

    $connect=mysqli_connect('localhost','parkuser','parkpass','parking','3306','/tmp/mysql.sock');  

    $default_query = "select score from recommend where member_id = '$member_id' and park_no = $park_no";

    $query = "";
    if($result=@mysqli_query($connect, $default_query)){//점수를 준 적이 있으면 update
        $query = "update recommend set score = $score where member_id = '$member_id' and park_no = $park_no";
    }else{//점수를 준 적이 없으면 insert
        $query = "insert into recommend values ('$member_id', $park_no, $score)";
    }

    if(mysqli_query($connect, $query))
        echo "success";
    else
        echo "fail";
        
    mysqli_close($connect);
?>