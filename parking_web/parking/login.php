<?php
    $member_id = $_GET["member_id"];
    $member_pw = $_GET["member_pw"];

    $connect=mysqli_connect('localhost','parkuser','parkpass','parking','3306','/tmp/mysql.sock');  

    $query = "select * from member where id = '$member_id' and password = '$member_pw'";

    $result=mysqli_query($connect, $query);

    $rows = mysqli_num_rows($result);
    
    if($rows == 0)
        echo "fail";
    else{
        $member_info = array();
        while($data=@mysqli_fetch_assoc($result)){
            array_push($member_info, $data);
        }

        $output =  json_encode($member_info, JSON_UNESCAPED_UNICODE);
        echo urldecode($output);
    }
    
    mysqli_close($connect);
?> 
