package com.example.tatset;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.TextView;

public class MainActivity extends Activity {
private TextView mView;
private DatabaseHelper dbhelper = null;
static private String mch1 = "<dt>";
static private String mch2 ="<a href=";
static private String mch22 ="</a>";
static private String mch3="<dd>";
static private String mch33="</dd>";
static private String[] mArticleTitle = new String[100];
static private String[] mArticleLocation = new String[100];
static private String[] mArticleDay = new String[100];
static private String[] mArticleDetail = new String[100];
static private String[] mArticleWeb = new String[100];
static private String tmp_data = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    	StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
    	dbhelper = new DatabaseHelper(this);

        mView=(TextView)findViewById(R.id.view);


        mView.setText(new String(httpGet("http://www.2083.jp/concert/#next")));
    }

    public static String httpGet(String strURL){
     try{
        URL url=new URL(strURL);
        URLConnection connection=url.openConnection();
        connection.setDoInput(true);
        InputStream stream=connection.getInputStream();
        BufferedReader input=new BufferedReader(new InputStreamReader(stream,"SJIS"));
        String data="";
        String tmp="";
        int count = 0;
        String flag_s="<li><dl class=\"detail\">";
        String flag_e="</dl></li>";
        String flag_dt="<dt></dt>";
        String flag_last = "<dd>コンサート情報掲載の依頼は";
        int flag = 0;

        while((tmp=input.readLine()) !=null){
        	if (tmp.indexOf(flag_dt) != -1 || tmp.indexOf(flag_last) != -1){
        	} else {
        		if(flag==1){
        			dataParser(brChecker(tmp),count);
        			data+=tmp;
        		}
        	}

        	if (tmp.indexOf(flag_s) != -1){
        		flag=1;
        		count++;
        	}
        	if (tmp.indexOf(flag_e) != -1){
        		flag=0;
        	}
        }
         stream.close();
         input.close();
         return data;

     }catch (Exception  e){
         return e.toString();
     }
   }

    public static String brChecker(String data){
    	String freecheck= "<span class=\"free\">";
    	//titleの改行チェック
        if(data.indexOf(mch2) != -1){
     	}else if(data.indexOf(mch22) != -1){
        	tmp_data+=data;
        	return tmp_data;
     	}
      //detailの改行チェック
        if(data.indexOf(mch3) != -1){
     	}else if(data.indexOf(mch33) != -1){
     		if(data.indexOf(freecheck) != -1){
     			tmp_data+="</dd>";
     			return tmp_data;
     		}else{
     			tmp_data+=data;
     			return tmp_data;
     		}
     	}
    	tmp_data = data;
    	return data;
    }

    public static void dataParser(String data ,int cnt){
    	int ct = cnt - 1;
    	if (data.indexOf(mch1) != -1){

        	//dayのデータ整形
        	String[] spday = data.split("【");
    		mArticleDay[ct] = spday[0].replaceAll("<dt>","");

    		//locationのデータ整形
    		String[] sploca = spday[1].split("・");
    		mArticleLocation[ct] = sploca[0];
    	}

    	if (data.indexOf(mch22) != -1 && data.indexOf(mch2) != -1){

    		//webのデータ整形
    		String[] spweb = data.split("\"");
    		mArticleWeb[ct] = spweb[1];

    		//titleのデータ整形
    		spweb[2] = spweb[2].replaceAll("<br />"," ");
    		spweb[2] = spweb[2].replaceAll("</a></dt>"," ");
    		spweb[2] = spweb[2].replaceAll(">"," ");
    		mArticleTitle[ct] = spweb[2];
    	}

    	if (data.indexOf(mch3) != -1 && data.indexOf(mch33) != -1){
    		String detail = data;
    		detail = detail.replaceAll("<br />"," ");
    		detail = detail.replaceAll("</dd>","");
    		detail = detail.replaceAll("<dd>","");
    		detail = detail.replaceAll("</span>","");
    		detail = detail.trim();
    		mArticleDetail[ct] = detail;
    	}
    }



    public void doSave(String t) {
        String s_title = "";
        String s_day = "";
        String s_location ="";
        String s_web = "";
        String s_detail ="";

        SQLiteDatabase db = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.TITLE, s_title);
        values.put(DatabaseHelper.DAY, s_day);
        values.put(DatabaseHelper.LOCATION, s_location);
        values.put(DatabaseHelper.WEB, s_web);
        values.put(DatabaseHelper.DETAIL, s_detail);
        db.insert(DatabaseHelper.TABLE_NAME,null,values);
    }

    public void doFind(String v) {
    	String s_title = "";
        SQLiteDatabase db = dbhelper.getReadableDatabase();
    	String findset = DatabaseHelper.TITLE + " = ?";
    	String[] params = {s_title};
    	Cursor c = db.query(DatabaseHelper.TABLE_NAME,DatabaseHelper.COL_ARR,
    		findset,params,null,null,null,null);
    	if (c.moveToFirst()){
    		//name.setText(c.getString(1));
    		//tel.setText(c.getString(2));
    		//content.setText(c.getString(3));
    	} else {


    	}
    }
}

