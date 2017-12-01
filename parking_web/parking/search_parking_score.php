<?php
    $member_id = $_GET["member_id"];
    $park_no = $_GET["park_no"];

    //$member_id = "koung1@naver.com";
    //$park_no = 1042405;

    $connect=mysqli_connect('localhost','parkuser','parkpass','parking','3306','/tmp/mysql.sock');  

    $default_score = -1.0;//내가 준 점수
    if(strcmp($member_id, "")){
        //내가 주차장에 준 점수 추출
        $default_query = "select score from recommend where member_id = '$member_id' and park_no = $park_no";
        //echo $default_query;
        if($result=@mysqli_query($connect, $Query)){
            if($row = mysqli_fetch_array($result)){
                $default_score = $row['score'];
            }  
        }else{
            $default_score = -1.0;
        }
    }
    
    //이 주차장에 투표한 사람의 수를 추출

    $query = "select count(score), avg(score) from recommend where park_no = $park_no group by park_no";
    //echo $query;

    $total_score = 0.0;//전체 평점
    $vote_num = 0;//평점을 준 사람의 수
    
    if($result=@mysqli_query($connect, $query)){
        if($row = mysqli_fetch_array($result)){
            $vote_num = $row['count(score)'];
            $total_score = $row['avg(score)'];
        }    
    }
    

    //echo ($default_score."...".$total_score."....".$vote_num);

    header("Content-Type:text/html;charset=utf-8");
    $score_object = array("default_score"=>$default_score, "total_score"=>$total_score, "vote_num"=>$vote_num);

    $output =  json_encode($score_object, JSON_UNESCAPED_UNICODE);

    echo urldecode($output);

    mysqli_close($connect);
?>