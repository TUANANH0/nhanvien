package com.example.pe_nhansu_se130467;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

import api.NhanVienRepository;
import api.NhanVienService;
import model.NhanVien;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    NhanVienService nhanvienService;
    EditText edtName, edtDate, edtGender, edtSalary;
    Button btnSave;
    Button btnChangeActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edtName = findViewById(R.id.edtTextTen);
        edtDate = findViewById(R.id.edtTextDate);
        edtGender = findViewById(R.id.edtTextGender);
        edtSalary = findViewById(R.id.edtTextSalary);
        btnSave = findViewById(R.id.btnSaveData);
        btnChangeActivity = findViewById(R.id.btnViewList);

        edtDate.addTextChangedListener(new TextWatcher() {
            private String current = "";
            private final String ddmmyyyy = "DDMMYYYY";
            private final Calendar cal = Calendar.getInstance();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                    String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8) {
                        clean = clean + ddmmyyyy.substring(clean.length());
                    } else {
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day = Integer.parseInt(clean.substring(0, 2));
                        int mon = Integer.parseInt(clean.substring(2, 4));
                        int year = Integer.parseInt(clean.substring(4, 8));

                        mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                        cal.set(Calendar.MONTH, mon - 1);
                        year = (year < 1900) ? 1900 : (year > 2100) ? 2100 : year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : day;
                        clean = String.format("%02d%02d%02d", day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    edtDate.setText(current);
                    edtDate.setSelection(sel < current.length() ? sel : current.length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        nhanvienService = NhanVienRepository.getNhanvienService();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        btnChangeActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NhanvienListActivity.class);
                startActivity(intent);
            }
        });


    }

    // Add
    private void save() {
        String name = edtName.getText().toString();
        String date = edtDate.getText().toString();
        String gender = edtGender.getText().toString();
        int salary = Integer.parseInt(edtSalary.getText().toString());

        // Create Trainee (Trainee Object)
        NhanVien nhanvien = new NhanVien(name, date, gender, salary);
        try {
            Call<NhanVien> call = nhanvienService.createNhanvien(nhanvien);
            call.enqueue(new Callback<NhanVien>() {
                @Override
                public void onResponse(Call<NhanVien> call, Response<NhanVien> response) {
                    NhanVien nhanvien1 = response.body();
                    if (nhanvien1 != null) {
                        Toast.makeText(MainActivity.this, "Save successfully", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<NhanVien> call, Throwable t) {
                    System.out.println("Loi roi" + call);
                    Toast.makeText(MainActivity.this, "Save failed", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.d("Lỗi", e.getMessage());
        }

    }
}