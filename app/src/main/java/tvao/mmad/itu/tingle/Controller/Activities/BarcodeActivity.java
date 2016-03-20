package tvao.mmad.itu.tingle.Controller.Activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import tvao.mmad.itu.tingle.R;

//code adopted from http://www.mysamplecode.com/2011/09/android-barcode-scanner-using-zxing.html

@Deprecated
public class BarcodeActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tingle);

        try
        {
            Button scanner = (Button)findViewById(R.id.barcode_scanner);
            scanner.setOnClickListener(new View.OnClickListener()
            {

                public void onClick(View v)
                {
                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
//                  intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                    startActivityForResult(intent, 0);
                }
            });

//            Button scanner2 = (Button)findViewById(R.id.scanner2);
//            scanner2.setOnClickListener(new View.OnClickListener() {
//
//                public void onClick(View v) {
//                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
//                    intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
//                    startActivityForResult(intent, 0);
//                }
//
//            });

        } catch (ActivityNotFoundException anfe)
        {
            Log.e("onCreate", "Scanner Not Found", anfe);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        if (requestCode == 0)
        {
            if (resultCode == RESULT_OK)
            {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                // Handle successful scan
                Toast toast = Toast.makeText(this, "Content:" + contents + " Format:" + format , Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 25, 400);
                toast.show();
            }
            else if (resultCode == RESULT_CANCELED)
            {
                // Handle cancel
                Toast toast = Toast.makeText(this, "Scan was Cancelled!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 25, 400);
                toast.show();
            }
        }
    }

}

