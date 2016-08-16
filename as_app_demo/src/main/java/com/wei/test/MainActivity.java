package com.wei.test;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView listView;

    private ArrayList<String> textList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textList.add("自定义相册图片选择");
        textList.add("系统原生相册图片选择");
        textList.add("二维码扫描");

        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, textList));
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String text = textList.get(position);

        if (text.equals(textList.get(0))) {//图片选择，单张或者多张图片选择
            startActivity(new Intent(this, SelectPicCustomerActivity.class));
        }else if(text.equals(textList.get(1))){
            startActivity(new Intent(this, SelectPicNativeActivity.class));
        } else if (text.equals(textList.get(2))) {
            startActivity(new Intent(this, ScanQrActivity.class));
        }
    }
}
