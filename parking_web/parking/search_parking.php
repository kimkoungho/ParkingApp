<?php

    $search_type=$_GET["search_type"];
    $distance=$_GET["distance"];
    $cost=$_GET["cost"];//hour_cost < cost
    $park_item=$_GET["park_item"];//"상관없음", "시간주차", "일일주차", "월정기주차"
    $car=$_GET["car"];//"상관없음", "승용(소)", "승용(대)", "승합", "화물(중)", "화물(대)"

    $latitude=$_GET["latitude"];
    $longitude=$_GET["longitude"];


    // $latitude=37.554521;
    // $longitude=126.9684543;
    // $distance=1.0;
    // $search_type = "거리";
    // $cost = "상관없음";
    // $park_item= "상관없음";
    // $car = "상관없음";

    $connect=mysqli_connect('localhost','parkuser','parkpass','parking','3306','/tmp/mysql.sock');  

    $Query="select *, (6371 * acos(cos(radians($latitude)) * cos( radians(latitude)) * cos(radians(longitude) - radians($longitude)) + sin(radians($latitude)) * sin( radians(latitude)))) as distance from park ";

    $first_flag=false;
    if(strcmp($cost, "상관없음")){//상관없음 이 아니면
        $Query .= "where hour_cost <= $cost ";
        $first_flag=true;
    }

    if(!strcmp($park_item,"시간주차")){
        if($first_flag)
            $Query .= "and hour_cost <> 0 ";
        else
            $Query .= "where hour_cost <> 0 ";
        $first_flag=true;
    }else if(!strcmp($park_item,"일일주차")){
        if($first_flag)
            $Query .= "and day_cost <> 0 ";
        else
            $Query .= "where day_cost <> 0 ";
        $first_flag=true;
    }else if(!strcmp($park_item,"월정기주차")){
        if($first_flag)
            $Query .= "and month_cost <> 0 ";
        else
            $Query .= "where month_cost <> 0 ";
        $first_flag=true;
    }

    ////$Query="select *, (6371 * acos(cos(radians($latitude)) * cos( radians(latitude)) * cos(radians(longitude) - radians($longitude)) + sin(radians($latitude)) * sin( radians(latitude)))) as distance from park having distance < $distance order by distance";
    
    $api_flag = false;
    if(!strcmp($search_type,"거리")){
        if($distance != -1.0)
            $Query .= " having distance < $distance";

        $Query .= " order by distance limit 0, 5";
    }else if(!strcmp($search_type,"주차시간")){//제일 
        if($distance != -1.0)
            $Query .= " having distance < $distance";

        $Query .= " order by base_time desc limit 0, 5";
    }else if(!strcmp($search_type,"금액")){//제일 저렴한 것
        if($distance != -1.0)
            $Query .= " having distance < $distance";

        $Query .= " order by hour_cost limit 0, 5";
    }else if(!strcmp($search_type,"주차가능대수")){
        //database 값을 기반으로 검색을 해야 detail 에서 오류가 나지 않음
        $api_key = "7959775045646e7232396877635872";
        $api_server = "http://openapi.seoul.go.kr:8088/$api_key/json/SearchParkingInfoRealtime/1/1000/";

        $contents = file_get_contents($api_server);
        
        $obj = json_decode($contents);
        $obj = $obj->SearchParkingInfoRealtime;
        $list = $obj->row;

        $n_list = array();//중복된 주소 제거
        for($i=0; $i<sizeof($list); $i++){
            $name = $list[$i]->PARKING_NAME;
            $flag = false;
            for($j=0; $j<sizeof($n_list); $j++){
                $n_name = $n_list[$j]->PARKING_NAME;
                if(!strcmp($name,$n_name)){
                    $flag = true;
                }
            }
            if(!$flag)
                array_push($n_list,$list[$i]);
        }

        //자리가 제일 많은것을 앞쪽으로 정렬
        usort($n_list,function($a, $b){
            return $a->CAPACITY > $b->CAPACITY ? -1 : 1;
        });

        $api_flag = true;
    }else if(!strcmp($search_type,"평점")){
        if($distance != -1.0)
            $Query .= " having distance < $distance";
        //recommend 데이터베이스 필요
        if($first_flag)
            $Query .= " and park_no in(select park_no from recommend order by score desc) limit 0, 5;";
        else
            $Query .= " where park_no in(select park_no from recommend order by score desc) limit 0, 5;";
    }   

    //echo($Query);

    //console.log($Query);


    header("Content-Type:text/html;charset=utf-8");
    $park_list=array();
    
    if($api_flag){
        for($i=0; $i<sizeof($n_list); $i++){
            $code = $n_list[$i]->PARKING_CODE;
            $space = $n_list[$i]->CAPACITY;
            $cur_parking = $n_list[$i]->CUR_PARKING;
            
            if(sizeof($park_list)>=5)
                break;

            $select_query = $Query;
            if($first_flag)
                $select_query .= " and park_no = $code";
            else
                $select_query .= " where park_no = $code";

            if($distance != -1.0)
                $select_query .= " having distance < $distance";
            //echo $select_query."<BR>";
            
            if($result = mysqli_query($connect, $select_query)){
                if($row=mysqli_fetch_assoc($result)){
                    $row["cur_parking"] = $cur_parking;
                    array_push($park_list, $row);
                }
            }
        }
    }else{
        if(!$result=@mysqli_query($connect, $Query)){
            die("결과 집합 만들기 실패");
            exit;
        }

        while($row=mysqli_fetch_assoc($result)){
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