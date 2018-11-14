package app_utility;

/*
 * Created by Vj on 05-12-2017.
 */

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;

public class PermissionHandler {

    protected static final int REQUEST_CHECK_SETTINGS = 3;

    private Activity aActivity;
    private String sMSG;
    private int nPermissionFlag = 0; //0=from onStart of HomeScreenActivity, 1= from other source
    /*nPermissionFlag is set to zero when we need to quit application on alert dialog cancel button
        nPermissionFlag is set to one when we just need to cancel Alert dialog.
     */

    /*public static String[] SIGN_IN_PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE
            , Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static String[] MY_ACCOUNT_PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.READ_CONTACTS};

    public static String[] LOCATION_PERMISSION = {Manifest.permission.ACCESS_COARSE_LOCATION};

    public static String[] SOS_PERMISSION = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.SEND_SMS, Manifest.permission.READ_CONTACTS};

    public static String[] HOME_PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE
            , Manifest.permission.WRITE_EXTERNAL_STORAGE};*/

    public static String[] WRITE_PERMISSION = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static String[] LOCATION_PERMISSION = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    public PermissionHandler(Activity aActivity, int nCaseToExecute, String sMSG, int nPermissionFlag) {
        this.aActivity = aActivity;
        this.sMSG = sMSG; //msg consist of all the permissions that needs to be displayed
        this.nPermissionFlag = nPermissionFlag;
        showDialog(nCaseToExecute);
    }

    public PermissionHandler(Activity aActivity, int nCaseToExecute) {
        this.aActivity = aActivity;
        showDialog(nCaseToExecute);
    }

    private void showDialog(int nCaseToExecute) {
        switch (nCaseToExecute) {
            case 0:
                showPermissionExplanation(" "+sMSG.substring(0,sMSG.length()-2)+" ", nPermissionFlag);
                //showBluetoothDialog();
                break;
            case 1:
                //requestLocationPermission();
                //showLocationPermissionExplanation();
                break;
            case 2:
                //showEnableDeviceLocation();
                break;
        }
    }

    private void showPermissionExplanation(String sMSG, final int nPermissionFlag) {
        AlertDialog.Builder alertPermission = new AlertDialog.Builder(aActivity);
        alertPermission.setCancelable(false);
        alertPermission.setMessage("To assist you better, TrackPro app requires" + sMSG+"permission. Would you like to grant these permissions?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", aActivity.getPackageName(), null);
                        intent.setData(uri);
                        aActivity.startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //aActivity.onBackPressed();
                        if(nPermissionFlag==0){
                            aActivity.finish();
                        } else {
                            dialog.dismiss();
                        }
                    }
                })
                .create()
                .show();
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
