package com.example.pe_nhansu_se130467;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;

import api.NhanVienRepository;
import api.NhanVienService;
import model.NhanVien;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NhanvienListActivity extends AppCompatActivity implements View.OnClickListener{
    Button btnUpdate, btnMain;
    Spinner spinner;
    EditText edtName, edtDate, edtGender, edtSalary;
    NhanVienService nhanvienService;
    ArrayList<Long> alId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nhanvien_list);

        edtName = findViewById(R.id.edtName);
        edtDate = findViewById(R.id.edtDate);
        edtGender = findViewById(R.id.edtGender);
        spinner = findViewById(R.id.spinner);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnMain = findViewById(R.id.btnMainActivity);
        btnUpdate.setOnClickListener(this);


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

        btnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NhanvienListActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        nhanvienService = NhanVienRepository.getNhanvienService();
        getAllNhanvien();


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                long i = Long.parseLong(spinner.getSelectedItem().toString());
                getNhanvienByID(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void getNhanvienByID(long id) {
        Call<NhanVien> call = nhanvienService.getNhanvienById(id);
        call.enqueue(new Callback<NhanVien>() {
            @Override
            public void onResponse(Call<NhanVien> call, Response<NhanVien> response) {
                NhanVien nhanvien = response.body();
                edtName.setText(nhanvien.getName());
                edtDate.setText(nhanvien.getDate());
                edtGender.setText(nhanvien.getGender());
            }

            @Override
            public void onFailure(Call<NhanVien> call, Throwable t) {
            }
        });
    }

    private void getAllNhanvien() {
        alId = new ArrayList<>();
        Call<NhanVien[]> call = nhanvienService.getAllNhanviens();
        call.enqueue(new Callback<NhanVien[]>() {
            @Override
            public void onResponse(Call<NhanVien[]> call, Response<NhanVien[]> response) {
                NhanVien[] nhanviens = response.body();
                if (nhanviens == null) {
                    return;
                }
                for (NhanVien nhanvien : nhanviens) {
                    alId.add(nhanvien.getId());
                }
                ArrayAdapter<Long> adapter = new ArrayAdapter(NhanvienListActivity.this, android.R.layout.simple_spinner_dropdown_item, alId);
                spinner.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<NhanVien[]> call, Throwable t) {
            }
        });
    }

    private void updateNhanvien(long NhanvienId) {
        String name = edtName.getText().toString();
        String date = edtDate.getText().toString();
        String gender = edtGender.getText().toString();
        int salary = Integer.parseInt(edtSalary.getText().toString());

        NhanVien nhanvien = new NhanVien(name, date, gender, salary);
        Call<NhanVien> call = nhanvienService.updateNhanvien(NhanvienId, nhanvien);
        call.enqueue(new Callback<NhanVien>() {
            @Override
            public void onResponse(Call<NhanVien> call, Response<NhanVien> response) {
                NhanVien product1 = response.body();
                if (product1 != null) {
                    Toast.makeText(NhanvienListActivity.this, "Update successfully", Toast.LENGTH_SHORT).show();
                    getAllNhanvien();
                }
            }

            @Override
            public void onFailure(Call<NhanVien> call, Throwable t) {
            }
        });
    }

    private void deleteNhanvien(long NhanvienId) {
        Call<NhanVien> call = nhanvienService.deleteNhanvienById(NhanvienId);
        call.enqueue(new Callback<NhanVien>() {
            @Override
            public void onResponse(Call<NhanVien> call, Response<NhanVien> response) {
                NhanVien nhanvien1 = response.body();
                if (nhanvien1 != null) {
                    Toast.makeText(NhanvienListActivity.this, "Delete successfully", Toast.LENGTH_SHORT).show();
                    getAllNhanvien();
                }
            }

            @Override
            public void onFailure(Call<NhanVien> call, Throwable t) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        long id = Long.parseLong(spinner.getSelectedItem().toString());
        switch (v.getId()) {
            case R.id.btnUpdate:
                updateNhanvien(id);
                break;
        }

    }
}
