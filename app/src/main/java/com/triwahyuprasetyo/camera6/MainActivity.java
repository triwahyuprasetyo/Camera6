package com.triwahyuprasetyo.camera6;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.triwahyuprasetyo.camera6.permission.PermissionUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "MainActivity";
    private static final int REQUEST_CAMERA = 0;
    private static final int REQUEST_STORAGE = 1;
    private static final int REQUEST_LOCATION = 2;
    private final int REQUEST_TAKE_PHOTO = 40;
    private final int REQUEST_PICKFILE = 41;
    private Button buttonCamera, buttonStorage, buttonMap;
    private String imageFileName, pictureImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonCamera = (Button) findViewById(R.id.button_camera_main);
        buttonCamera.setOnClickListener(this);
        buttonStorage = (Button) findViewById(R.id.button_storage);
        buttonStorage.setOnClickListener(this);
        buttonMap = (Button) findViewById(R.id.button_map);
        buttonMap.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == buttonCamera.getId()) {
            showCamera();
        } else if (v.getId() == buttonStorage.getId()) {
            showStorage();
        } else if (v.getId() == buttonMap.getId()) {
            showMap();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    Log.i("SDP SUCCESSS", "SUCCESS TAKE POTO");
                    galleryAddPic();
                }
                break;
            case REQUEST_PICKFILE:
                if (resultCode == RESULT_OK) {
                    Uri selectedImageURI = data.getData();
                    File imageFile = new File(getRealPathFromURI(selectedImageURI));
                    String path = imageFile.getPath();
                    if (path.contains("Exception")) {
                        Log.i("SDP UPLOAD", "Gagal Ambil Path");
                    } else {
                        Log.i("SDP UPLOAD", path);
                    }
                }
                break;

        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result = "";
        try {
            Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
            if (cursor == null) { // Source is Dropbox or other similar local file path
                result = contentURI.getPath();
            } else {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(idx);
                cursor.close();
            }
        } catch (IllegalStateException e) {
            result = "Exception " + e.getMessage();
        } catch (RuntimeException e) {
            result = "Exception " + e.getMessage();
        } catch (Exception e) {
            result = "Exception " + e.getMessage();
        }
        return result;
    }

    /**
     * Called when the 'show camera' button is clicked.
     * Callback is defined in resource layout definition.
     */
    public void showCamera() {
        Log.i(TAG, "Show camera button pressed. Checking permission.");
        // Check if the Camera permission is already available.
        if (ActivityCompat.checkSelfPermission(this, PermissionUtils.PERMISSIONS_CAMERA[0])
                != PackageManager.PERMISSION_GRANTED) {
            // Camera permission has not been granted.
            requestCameraPermission();
        } else {
            // Camera permissions is already available, show the camera preview.
            Log.i(TAG,
                    "CAMERA permission has already been granted. Displaying camera preview.");
            openBackCamera();
        }
    }

    /**
     * Called when the 'show camera' button is clicked.
     * Callback is defined in resource layout definition.
     */
    public void showStorage() {
        Log.i(TAG, "Show storage button pressed. Checking permission.");
        // Check if the Camera permission is already available.
        if (ActivityCompat.checkSelfPermission(this, PermissionUtils.PERMISSIONS_STORAGE[0])
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this,
                PermissionUtils.PERMISSIONS_STORAGE[1]) != PackageManager.PERMISSION_GRANTED) {
            // Camera permission has not been granted.
            requestStoragePermission();
        } else {
            // Camera permissions is already available, show the camera preview.
            Log.i(TAG,
                    "STORAGE permission has already been granted. Displaying File Manager.");
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("file/*");
            startActivityForResult(intent, REQUEST_PICKFILE);
        }
    }

    public void showMap() {
        Log.i(TAG, "Show map button pressed. Checking permission.");
        // Check if the Camera permission is already available.
        if (ActivityCompat.checkSelfPermission(this, PermissionUtils.PERMISSIONS_LOCATION[0])
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this,
                PermissionUtils.PERMISSIONS_LOCATION[1]) != PackageManager.PERMISSION_GRANTED) {
            // Camera permission has not been granted.
            requestLocationPermission();
        } else {
            // Camera permissions is already available, show the camera preview.
            Log.i(TAG,
                    "Location permission has already been granted. Displaying Map.");
            Intent i = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(i);
        }
    }

    /**
     * Requests the Camera permission.
     * If the permission has been denied previously, a SnackBar will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    private void requestCameraPermission() {
        Log.i(TAG, "CAMERA permission has NOT been granted. Requesting permission.");
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                PermissionUtils.PERMISSIONS_CAMERA[0])) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Log.i(TAG,
                    "Displaying camera permission rationale to provide additional context.");
            PermissionUtils.RationaleDialog.newInstance(REQUEST_CAMERA, true)
                    .show(this.getSupportFragmentManager(), "dialog");
        } else {
            // Camera permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(MainActivity.this,
                    PermissionUtils.PERMISSIONS_CAMERA, REQUEST_CAMERA);
            Log.i(TAG,
                    "Camera permission has not been granted yet. Request it directly.");
        }
    }

    /**
     * Requests the Camera permission.
     * If the permission has been denied previously, a SnackBar will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    private void requestStoragePermission() {
        Log.i(TAG, "STORAGE permission has NOT been granted. Requesting permission.");
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, PermissionUtils.PERMISSIONS_STORAGE[0]) ||
                ActivityCompat.shouldShowRequestPermissionRationale(this, PermissionUtils.PERMISSIONS_STORAGE[1])) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Log.i(TAG,
                    "Displaying storage permission rationale to provide additional context.");
            PermissionUtils.RationaleDialog.newInstance(REQUEST_STORAGE, true)
                    .show(this.getSupportFragmentManager(), "dialog");
        } else {
            // Camera permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(MainActivity.this,
                    PermissionUtils.PERMISSIONS_STORAGE, REQUEST_STORAGE);
            Log.i(TAG,
                    "Storage permission has not been granted yet. Request it directly.");
        }
    }

    private void requestLocationPermission() {
        Log.i(TAG, "Location permission has NOT been granted. Requesting permission.");
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, PermissionUtils.PERMISSIONS_LOCATION[0]) ||
                ActivityCompat.shouldShowRequestPermissionRationale(this, PermissionUtils.PERMISSIONS_LOCATION[1])) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Log.i(TAG,
                    "Displaying location permission rationale to provide additional context.");
            PermissionUtils.RationaleDialog.newInstance(REQUEST_LOCATION, true)
                    .show(this.getSupportFragmentManager(), "dialog");
        } else {
            // Camera permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(MainActivity.this,
                    PermissionUtils.PERMISSIONS_LOCATION, REQUEST_LOCATION);
            Log.i(TAG,
                    "Location permission has not been granted yet. Request it directly.");
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "Received response permission request.");
        if (requestCode == REQUEST_CAMERA) {
            // Received permission result for camera permission.
            Log.i(TAG, "Received response for Camera permission request.");
            Log.i(TAG, "grantResults.length " + grantResults.length + "");
            // Check if the only required permission has been granted
            if (PermissionUtils.verifyPermissions(grantResults)) {
                // Camera permission has been granted, preview can be displayed
                Log.i(TAG, "CAMERA permission has now been granted. Showing preview.");
                showCamera();
            } else {
                Log.i(TAG, "CAMERA permission was NOT granted.");
                Toast.makeText(getApplicationContext(), R.string.camera_permission_denied,
                        Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == REQUEST_STORAGE) {
            // Received permission result for camera permission.
            Log.i(TAG, "Received response for Storage permission request.");
            Log.i(TAG, "grantResults.length " + grantResults.length + "");

            // We have requested multiple permissions for contacts, so all of them need to be
            // checked.
            if (PermissionUtils.verifyPermissions(grantResults)) {
                // All required permissions have been granted, display contacts fragment.
                Log.i(TAG, "STORAGE permission has now been granted. Showing preview.");
                showStorage();
            } else {
                Log.i(TAG, "STORAGE permissions were NOT granted.");
                Toast.makeText(getApplicationContext(), R.string.storage_permission_denied,
                        Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == REQUEST_LOCATION) {
            // Received permission result for camera permission.
            Log.i(TAG, "Received response for Location permission request.");
            Log.i(TAG, "grantResults.length " + grantResults.length + "");

            // We have requested multiple permissions for contacts, so all of them need to be
            // checked.
            if (PermissionUtils.verifyPermissions(grantResults)) {
                // All required permissions have been granted, display contacts fragment.
                Log.i(TAG, "LOCATION permission has now been granted. Showing preview.");
                showMap();
            } else {
                Log.i(TAG, "LOCATION permissions were NOT granted.");
                Toast.makeText(getApplicationContext(), R.string.location_permission_denied,
                        Toast.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            Log.i(TAG, "Else");
        }
    }

    private void openBackCamera() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = timeStamp + ".jpg";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        pictureImagePath = storageDir.getAbsolutePath() + "/" + imageFileName;
        File file = new File(pictureImagePath);
        Uri outputFileUri = Uri.fromFile(file);
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            startActivityForResult(cameraIntent, REQUEST_TAKE_PHOTO);
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File f = new File(storageDir.getAbsolutePath() + "/" + imageFileName);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
        Log.i("SDP Path", f.getPath());
    }
}
