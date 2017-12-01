package com.parking.kani.parking.utility;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.parking.kani.parking.connection.AsynctaskFinishListener;
import com.parking.kani.parking.connection.ServerConnection;

import java.util.ArrayList;

/**
 * Created by kimkyeongho on 2017. 1. 4..
 */

public class DBHelper extends SQLiteOpenHelper
{
    // DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }

    // DB를 새로 생성할 때 호출되는 함수
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        sqLiteDatabase.execSQL("create table favorite ( park_no integer primary key );");
    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int old_version, int new_version)
    {

    }

    public boolean insert(int park_no)
    {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();

        String result = "";
        Cursor cursor = db.rawQuery("select * from favorite where park_no = "+park_no+";", null);

        if(cursor.getColumnCount() > 0) {
            if(cursor.moveToNext()){
                Log.d("insert select",cursor.getInt(0)+"...");
                return false;
            }
        }

        // DB에 입력한 값으로 행 추가
        db.execSQL("insert into favorite values ("+park_no+");");
        db.close();
        return true;
    }

    public void delete(int park_no)
    {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행 삭제
        db.execSQL("delete from favorite where park_no = "+park_no+";");
        db.close();
    }

    public void deleteAll()
    {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from favorite");
        db.close();
    }

    public String select()
    {
        SQLiteDatabase db = getWritableDatabase();
        String result = "";

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("select * from favorite;", null);

        if(cursor.getColumnCount()>0)
        {
            while (cursor.moveToNext())
            {
                result += cursor.getInt(0) + "-";
            }
        }
        Log.d("Result - ",result);


        return result;
    }
}
