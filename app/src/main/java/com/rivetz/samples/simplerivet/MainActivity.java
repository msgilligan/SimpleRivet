package com.rivetz.samples.simplerivet;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void buttonOnClick(View v) {
        TextView output = (TextView) findViewById(R.id.output);

        Intent intent = new Intent(Rivet.RIVET_INTENT)
                .putExtra(Rivet.EXTRA_INSTRUCT, Rivet.INSTRUCT_CREATEKEY)
                .putExtra(Rivet.EXTRA_SPID, "98f88054-f98c-440c-81aa-77fa70a31116-fbca7c00-0602-4c1f-a354-820ae9ec46b9")
                .putExtra(Rivet.EXTRA_KEYTYPE, Rivet.KEYTYPE_ECDSA_DEFAULT)
                .putExtra(Rivet.EXTRA_KEYNAME,"MyKey");

        startActivityForResult(intent, Rivet.INSTRUCT_CREATEKEY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        TextView output = (TextView) findViewById(R.id.output);

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Rivet.INSTRUCT_CREATEKEY && resultCode == RESULT_OK) {
                String keyName = data.getStringExtra(Rivet.EXTRA_KEYNAME);
                output.setText("done: keyname=" + keyName);
                Intent intent = new Intent(Rivet.RIVET_INTENT)
                        .putExtra(Rivet.EXTRA_INSTRUCT, Rivet.INSTRUCT_GETKEY)
                        .putExtra(Rivet.EXTRA_SPID, "98f88054-f98c-440c-81aa-77fa70a31116-fbca7c00-0602-4c1f-a354-820ae9ec46b9")
                        .putExtra(Rivet.EXTRA_KEYNAME, "MyKey");

                startActivityForResult(intent, Rivet.INSTRUCT_GETKEY);
            } else if (requestCode == Rivet.INSTRUCT_GETKEY) {
                String pubKey = data.getStringExtra(Rivet.EXTRA_PUBLICDATA);
                output.append("\npubkey="+pubKey);
            }
        } else {
            output.setText("not done");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}