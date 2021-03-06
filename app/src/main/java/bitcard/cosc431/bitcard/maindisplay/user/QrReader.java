package bitcard.cosc431.bitcard.maindisplay.user;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import bitcard.cosc431.bitcard.R;

public class QrReader extends AppCompatActivity {

        SurfaceView cameraPreview;
        TextView txtResult;
        BarcodeDetector barcodeDetector;
        CameraSource cameraSource;
        final int RequestCameraPermissionID = 1001;
        String Qrval;

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            switch (requestCode) {
                case RequestCameraPermissionID: {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        try {
                            cameraSource.start(cameraPreview.getHolder());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            }
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {


            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_qr_reader);

            cameraPreview = (SurfaceView) findViewById(R.id.cameraPreview);
            txtResult = (TextView) findViewById(R.id.txtResult);

            barcodeDetector = new BarcodeDetector.Builder(this)
                    .setBarcodeFormats(Barcode.QR_CODE)
                    .build();
            cameraSource = new CameraSource
                    .Builder(this, barcodeDetector)
                    .setRequestedPreviewSize(640, 480)
                    .build();
            //Add Event
            cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        //Request permission
                        ActivityCompat.requestPermissions(QrReader.this,
                                new String[]{Manifest.permission.CAMERA},RequestCameraPermissionID);
                        return;
                    }
                    try {
                        cameraSource.start(cameraPreview.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                    cameraSource.stop();

                }
            });

            barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(Detector.Detections<Barcode> detections) {
                    final SparseArray<Barcode> qrcodes = detections.getDetectedItems();
                    if(qrcodes.size() != 0)
                    {
                        txtResult.post(new Runnable() {
                            @Override
                            public void run() {

                                txtResult.setText(qrcodes.valueAt(0).displayValue);
                                Qrval = qrcodes.valueAt(0).displayValue;
                                Intent sendQrBack = new Intent(QrReader.this, UserDisplay.class);
                                sendQrBack.putExtra("ScannedKey", Qrval);
                                startActivity(sendQrBack);
                                finish();


                            }
                        });


                    }
                }
            });
        }
    }



