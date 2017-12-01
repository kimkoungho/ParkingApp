<?php
    $park_no_list_str  = $_GET["park_no_list_str"];
    //987137-1042405
    //$park_no_list_str = "987137-1042405";
    $park_no_list = explode("-", $park_no_list_str);

    $connect=mysqli_connect('localhost','parkuser','parkpass','parking','3306','/tmp/mysql.sock');

    $query = "select * from park where park.park_no = ";

    header("Content-Type:text/html;charset=utf-8");
    $park_list=array();

    for($i=0; $i<count($park_no_list); $i++){
        $Query = $query . $park_no_list[$i];

        //echo $Query."<br>";
        $result=mysqli_query($connect, $Query);

        if($row=mysqli_fetch_assoc($result)){
            array_push($park_list, $row);
        }
    }

    //여기서 부터 이미지 검색
    $query = "select url from image where park_no = ";
    for($i=0; $i<sizeof($park_list); $i++){
        $park_no = $park_list[$i]["park_no"];

        $url_list = array();
        if(!$result = mysqli_query($connect, $query . $park_no)){
            array_push($url_list,'');
        }else{
            while($row = mysqli_fetch_array($result)){
                //$imagedata = file_get_contents($row[0]);
                // alternatively specify an URL, if PHP settings allow
                //$base64 = base64_encode($imagedata);
                array_push($url_list, $row[0]);
            }
        }

        $park_list[$i]["url"] = $url_list;
    }

    $output =  json_encode($park_list, JSON_UNESCAPED_UNICODE);

    echo urldecode($output);

    mysqli_close($connect);
?>