package sg.edu.rp.c347.id19007966.demoshowsms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    TextView tvSms;
    Button btnRetrieve;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvSms = findViewById(R.id.tv);
        btnRetrieve = findViewById(R.id.btnRetrieve);
        btnRetrieve.setOnClickListener((View v) -> {

            // PERMISSIONS
            int permissionCheck = PermissionChecker.checkSelfPermission(this, Manifest.permission.READ_SMS);

            if (permissionCheck != PermissionChecker.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, 0);
                return;
            }


            Uri uri = Uri.parse("content://sms");
            String[] reqCols = new String[]{"date", "address", "body", "type"};

            ContentResolver contentResolver = getContentResolver();
            Cursor cursor = contentResolver.query(uri, reqCols, null, null, null);
            String smsBody = "";
            if (cursor.moveToFirst()) {
                do {
                    long dateInMillis = cursor.getLong(0);
                    String date = (String) DateFormat.format("dd MMM yyy h:mm:ss aa", dateInMillis);
                    String address = cursor.getString(1);
                    String body = cursor.getString(2);
                    String type = cursor.getString(3);
                    type = type.equalsIgnoreCase("1") ? "Inbox: " : "Sent: ";

                    smsBody += type;
                    smsBody += address;
                    smsBody += "\nat ";
                    smsBody += date;
                    smsBody += "\n\"";
                    smsBody += body;
                    smsBody += "\"\n\n";
                }
                while (cursor.moveToNext());
            }
            tvSms.setText(smsBody);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    btnRetrieve.performClick();
                }
                else {
                    Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
                }
        }
    }
}