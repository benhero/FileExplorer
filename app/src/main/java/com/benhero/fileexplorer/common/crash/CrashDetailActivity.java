package com.benhero.fileexplorer.common.crash;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.TextView;

import com.benhero.fileexplorer.R;

/**
 * 报错日志详情界面
 *
 * @author benhero
 */
public class CrashDetailActivity extends AppCompatActivity {
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash_detail);
        mTextView = (TextView) findViewById(R.id.activity_crash_detail_tv);
        String crash = getIntent().getStringExtra("Crash");
        if (!TextUtils.isEmpty(crash)) {
            mTextView.setText(crash);
        }
    }
}
