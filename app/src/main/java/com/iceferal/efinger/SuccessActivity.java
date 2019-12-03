package com.iceferal.efinger;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.pm.PermissionInfoCompat;

import java.security.spec.InvalidKeySpecException;

public class SuccessActivity extends AppCompatActivity {

    private TextView textView;
    private EditText editText;
    private Button saveBtn;
    private String text;
    public static final String PASS = "pass";
    public static final String SHARED_PREFS = "SharedPrefs";
    public static final String TEXT = "text";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        textView = (TextView) findViewById(R.id.textView);
        editText = (EditText) findViewById(R.id.editText);
        saveBtn = (Button) findViewById(R.id.saveBtn);

        try {
            loadData();
        } catch (Exception e) { e.printStackTrace(); }
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    saveData();
                    updateViews();
                } catch (Exception e) { e.printStackTrace(); }
                updateViews();
                textView.setText(editText.getText().toString());
            }
        });
    }

    private void saveData() throws Exception {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Protection protection = new Protection("salt", "password", "iv");

        editor.putString(TEXT, protection.encrypt(editText.getText().toString()));
        editor.apply();
        Toast.makeText(getApplicationContext(), "notatka zostala dodana", Toast.LENGTH_LONG).show();}


    private  void loadData() throws Exception {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Protection protection = new Protection(sharedPreferences.getString("salt", ""),
                sharedPreferences.getString(PASS, ""),
                sharedPreferences.getString("iv", ""));

        text = protection.decrypt(sharedPreferences.getString(TEXT, ""));
        try {
            textView.setVisibility(View.VISIBLE);
            textView.setText(text);
        } catch (Exception e) {}
    }

    private void updateViews() {
        textView.setText(text);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_notes, menu);
        return true; }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                userLogout();
                return true;
            default:
                return super.onOptionsItemSelected(item);         }
    }

    public void userLogout() {
        startActivity(new Intent(this, MainActivity.class));
        finish();    }
}
