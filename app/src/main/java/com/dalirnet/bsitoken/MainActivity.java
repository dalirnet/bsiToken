package com.dalirnet.bsitoken;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    Button getTokenBtn;
    Button getLoginTokenBtn;
    Button getSmsBtn;
    Button convertBtn;
    EditText smsTxt;
    EditText num1Txt;
    EditText num2Txt;
    EditText num3Txt;
    EditText passTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getTokenBtn = this.findViewById(R.id.getToken);
        getLoginTokenBtn = this.findViewById(R.id.getLoginToken);
        getSmsBtn = this.findViewById(R.id.getSms);
        convertBtn = this.findViewById(R.id.convert);
        smsTxt = this.findViewById(R.id.sms);
        num1Txt = this.findViewById(R.id.num1);
        num2Txt = this.findViewById(R.id.num2);
        num3Txt = this.findViewById(R.id.num3);
        passTxt = this.findViewById(R.id.pass);

        getTokenBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (num1Txt.getText().length() != 5 || num2Txt.getText().length() != 5 || num3Txt.getText().length() != 5 || passTxt.getText().length() != 4) {
                    Toast.makeText(MainActivity.this, R.string.input_error, Toast.LENGTH_SHORT).show();
                    return;
                }
                String ussd = passTxt.getText().toString() + "*" + num1Txt.getText().toString() + "*" + num2Txt.getText().toString() + "*" + num3Txt.getText().toString();
                Intent ussdCall = new Intent(Intent.ACTION_CALL);
                String encodedHash = Uri.encode("#");
                ussdCall.setData(Uri.parse("tel:" + "*719*5*2*" + ussd + encodedHash));
                if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, R.string.please_grant_permission, Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(ussdCall);
                Toast.makeText(MainActivity.this, R.string.starting_process, Toast.LENGTH_SHORT).show();
            }
        });

        getLoginTokenBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (passTxt.getText().length() != 4) {
                    Toast.makeText(MainActivity.this, R.string.input_error, Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent ussdCall = new Intent(Intent.ACTION_CALL);
                String encodedHash = Uri.encode("#");
                ussdCall.setData(Uri.parse("tel:" + "*719*5*1*" + passTxt.getText().toString() + encodedHash));
                if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, R.string.please_grant_permission, Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(ussdCall);
                Toast.makeText(MainActivity.this, R.string.starting_process, Toast.LENGTH_SHORT).show();
            }
        });


        getSmsBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, R.string.please_grant_permission, Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean findSms = false;
                String[] projection = new String[]{"address", "body", "date"};
                @SuppressLint("Recycle") Cursor cursor = getContentResolver().query(Uri.parse("content://sms"), projection, "address LIKE '%9820004008%'", null, "date desc");

                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            Matcher m = Pattern.compile("([1|2|3])([\\.])([0-9]{5})").matcher(cursor.getString(1).trim());
                            if (m.find()) {
                                findSms = true;
                                smsTxt.setText(cursor.getString(1));
                                break;
                            }
                        } while (cursor.moveToNext());
                    }
                }

                if (findSms) {
                    MainActivity.this.convertSms();
                } else {
                    Toast.makeText(MainActivity.this, R.string.not_found, Toast.LENGTH_SHORT).show();
                }
            }
        });

        convertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (smsTxt.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, R.string.input_error, Toast.LENGTH_SHORT).show();
                    return;
                }

                MainActivity.this.convertSms();
            }
        });
    }

    public void convertSms() {
        num1Txt.setText("");
        num2Txt.setText("");
        num3Txt.setText("");

        Matcher m = Pattern.compile("([1|2|3])([\\.])([0-9]{5})").matcher(smsTxt.getText().toString().trim());
        while (m.find()) {
            if (Integer.valueOf(m.group(1)) == 1) {
                num1Txt.setText(m.group(3));
            }
            if (Integer.valueOf(m.group(1)) == 2) {
                num2Txt.setText(m.group(3));
            }
            if (Integer.valueOf(m.group(1)) == 3) {
                num3Txt.setText(m.group(3));
            }
        }

        Toast.makeText(MainActivity.this, R.string.parse_sms, Toast.LENGTH_SHORT).show();
    }
}
