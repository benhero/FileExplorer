package com.benhero.fileexplorer.function.fileexplorer.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.TextView;

import com.benhero.fileexplorer.R;
import com.benhero.fileexplorer.common.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * 文本界面
 */
public class ReaderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        TextView textView = (TextView) findViewById(R.id.activity_reader_text);
        String text = getData();
        if (TextUtils.isEmpty(text)) {
            finish();
        } else {
            textView.setText(text);
        }
    }

    private String getData() {
        Intent intent = getIntent();
        if (intent == null) {
            return null;
        }
        Uri uri = intent.getData();
        String path = uri.getPath();
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        try {
            return FileUtils.readFileToString(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
