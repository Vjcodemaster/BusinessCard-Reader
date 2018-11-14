package com.autochip.businesscardocr;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import app_utility.OnFragmentInteractionListener;
import app_utility.PermissionHandler;

import static app_utility.PermissionHandler.hasPermissions;

public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener {

    Button btnScan;
    TextView tvName;
    TextView tvPhoneNo;
    TextView tvEmail;

    private int CAMERA_CODE = 1777;
    public static String[] CAMERA_PERMISSION = {Manifest.permission.CAMERA};
    private int nPermissionFlag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
    }

    @Override
    public void onStart(){
        super.onStart();
        if (!hasPermissions(MainActivity.this, CAMERA_PERMISSION)) {
            ActivityCompat.requestPermissions(MainActivity.this, CAMERA_PERMISSION, CAMERA_CODE);
        }
    }

    void initViews(){
        btnScan = findViewById(R.id.btn_scan);
        tvName = findViewById(R.id.tv_name);
        tvPhoneNo = findViewById(R.id.tv_phone);
        tvEmail = findViewById(R.id.tv_email);

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment newFragment;
                FragmentTransaction transaction;
                newFragment = CameraFragment.newInstance("", "");
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fl_container, newFragment, null);
                transaction.commit();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int PERMISSION_ALL, String permissions[], int[] grantResults) {
        StringBuilder sMSG = new StringBuilder();
        if(PERMISSION_ALL==CAMERA_CODE) {
            for (String sPermission : permissions) {
                switch (sPermission) {
                    case Manifest.permission.CAMERA:
                        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CAMERA)) {
                                //Show permission explanation dialog...
                                //showPermissionExplanation(SignInActivity.this.getResources().getString(R.string.phone_explanation));
                                //Toast.makeText(SignInActivity.this, "not given", Toast.LENGTH_SHORT).show();
                                sMSG.append("CAMERA, ");
                                nPermissionFlag = 0;
                            } else {
                                //Never ask again selected, or device policy prohibits the app from having that permission.
                                //So, disable that feature, or fall back to another situation...
                                //@SuppressWarnings("unused") AlertDialogs alertDialogs = new AlertDialogs(HomeScreen.this, 1, mListener);
                                //Toast.makeText(SignInActivity.this, "permission never ask", Toast.LENGTH_SHORT).show();
                                //showPermissionExplanation(HomeScreenActivity.this.getResources().getString(R.string.phone_explanation));
                                sMSG.append("CAMERA, ");
                                nPermissionFlag = 0;
                            }
                        }
                        break;

                }
            }
            if (!sMSG.toString().equals("") && !sMSG.toString().equals(" ")) {
                PermissionHandler permissionHandler = new PermissionHandler(MainActivity.this, 0, sMSG.toString(), nPermissionFlag);
            }
        }
    }

    @Override
    public void onFragmentMessage(String TAG, Object data, boolean isEnabled) {

    }
}
