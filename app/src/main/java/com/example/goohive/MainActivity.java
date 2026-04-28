package com.example.goohive;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<String> fileList = new ArrayList<>();
    Button btnUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_Goohive);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        btnUpload = findViewById(R.id.idBtnUpload);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadLocalFiles();

        btnUpload.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent, 1);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            try {
                Uri uri = data.getData();

                InputStream inputStream = getContentResolver().openInputStream(uri);
                File file = new File(getCacheDir(), "upload_" + System.currentTimeMillis());

                FileOutputStream outputStream = new FileOutputStream(file);

                byte[] buffer = new byte[1024];
                int length;

                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }

                outputStream.close();
                inputStream.close();

                uploadToServer(file);  // 🔥 THIS IS THE KEY LINE

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void uploadToServer(File file) {
        ApiService apiService = RetrofitClient.getApiService();

        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part body =
                MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        Call<Void> call = apiService.uploadFile(body);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(MainActivity.this, "Uploaded ✅", Toast.LENGTH_SHORT).show();
                loadLocalFiles(); // refresh list
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Upload Failed ❌", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadLocalFiles() {
        fileList.clear();

        File dir = getFilesDir();
        File[] files = dir.listFiles();

        if (files != null) {
            for (File f : files) {
                fileList.add(f.getName());
            }
        }

        FileAdapter adapter = new FileAdapter(fileList, filename -> {
            File f = new File(getFilesDir(), filename);
            f.delete();
            loadLocalFiles();
        });

        recyclerView.setAdapter(adapter);
    }
}
