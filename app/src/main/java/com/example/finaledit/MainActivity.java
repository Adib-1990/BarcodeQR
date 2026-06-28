package com.example.finaledit;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private TextView tvStatus;
    private TextView tvResult;
    private static final int CAMERA_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvStatus = findViewById(R.id.tvStatus);
        tvResult = findViewById(R.id.tvResult);
    }

    public void HandleClick(View view) {
        // چک کردن مجوز دوربین قبل از اسکن
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            startScan(view);
        }
    }

    private void startScan(View view) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("کد را مقابل دوربین بگیرید");
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(false);
        integrator.setOrientationLocked(true);

        int id = view.getId();
        if (id == R.id.butQR) {
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        } else if (id == R.id.butProd) {
            integrator.setDesiredBarcodeFormats(IntentIntegrator.PRODUCT_CODE_TYPES);
        } else if (id == R.id.butOther) {
            integrator.setDesiredBarcodeFormats(Arrays.asList(
                    "CODE_39", "CODE_93", "CODE_128", "DATA_MATRIX",
                    "ITF", "CODABAR", "AZTEC", "PDF_417"
            ));
        }

        integrator.initiateScan();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "مجوز دوربین تایید شد", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "برای استفاده از اسکنر، مجوز دوربین ضروری است", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (result != null) {
            if (result.getContents() == null) {
                tvStatus.setText("اسکن لغو شد");
            } else {
                tvStatus.setText(result.getFormatName());
                String scanResult = result.getContents();
                tvResult.setText(scanResult);

                if (Patterns.WEB_URL.matcher(scanResult).matches()) {
                    tvResult.setAutoLinkMask(Linkify.WEB_URLS);
                    tvResult.setLinksClickable(true);
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }
}
