/*
* Copyright 2015 Rivetz Corp
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
* THE SOFTWARE.
*/
package com.rivetz.samples.simplerivet;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {
    // this is the SPID for the Rivetz Developer Tools service provider account
    private static final String MYSPID =
            "029d785242baad9f3d7bedcfca29d5391b3c247a3d4eaf5c3a0a5edd9489d1fcad";
    private static final String keyName = "MyKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pairDevice();
    }

    public void pairDevice() {
        // Pairing establishes the service provider on this device. It creates
        // the key store that will hold the keys created with this SPID
        // If the SPID is already paired, the result will be RESULT_OK. If
        // not the user will be prompted to accept the pairing.
        Intent intent = new Intent("com.rivetz.adapter.PAIR")
                .putExtra(Rivet.EXTRA_SPID, MYSPID)
                .putExtra(Rivet.EXTRA_SILENT, true);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, Rivet.INSTRUCT_PAIRDEVICE);
        }
    }

    public void buttonOnClick(View v) {
        TextView output = (TextView) findViewById(R.id.output);

        // CREATEKEY will return an error if the key name is already in use
        // If no key name is provided, one will be generated and can be
        // queried from the result with getStringExtra(Rivet.EXTRA_KEYNAME)
        Intent intent = new Intent(Rivet.RIVET_INTENT)
                .putExtra(Rivet.EXTRA_INSTRUCT, Rivet.INSTRUCT_CREATEKEY)
                .putExtra(Rivet.EXTRA_SPID, MYSPID)
                .putExtra(Rivet.EXTRA_KEYTYPE, Rivet.KEYTYPE_ECDSA_DEFAULT)
                .putExtra(Rivet.EXTRA_KEYNAME,keyName);

        startActivityForResult(intent, Rivet.INSTRUCT_CREATEKEY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        TextView output = (TextView) findViewById(R.id.output);

        super.onActivityResult(requestCode, resultCode, data);

        // On create key success or key already exists
        // sign the string given in the display (targetText)
        // and get the public key for keyName
        if (requestCode == Rivet.INSTRUCT_CREATEKEY &&
                (resultCode == RESULT_OK || resultCode == Rivet.ERROR_KEYNAME_EXISTS)) {
            EditText targetText = (EditText)findViewById(R.id.targetText);
            String targetTextStr = targetText.getText().toString();
            output.setText("keyname=" + keyName + "\n");
            output.append("target=" + targetTextStr +"\n");
            Intent intent = new Intent(Rivet.RIVET_INTENT)
                    .putExtra(Rivet.EXTRA_INSTRUCT, Rivet.INSTRUCT_SIGN)
                    .putExtra(Rivet.EXTRA_SPID, MYSPID)
                    .putExtra(Rivet.EXTRA_KEYNAME, keyName)
                    .putExtra(Rivet.EXTRA_MESSAGE, targetTextStr);
            startActivityForResult(intent, Rivet.INSTRUCT_SIGN);
            intent = new Intent(Rivet.RIVET_INTENT)
                    .putExtra(Rivet.EXTRA_INSTRUCT, Rivet.INSTRUCT_GETKEY)
                    .putExtra(Rivet.EXTRA_SPID, MYSPID)
                    .putExtra(Rivet.EXTRA_KEYNAME, keyName);
            startActivityForResult(intent, Rivet.INSTRUCT_GETKEY);

        // on a response from GETKEY print the public key
        } else if (requestCode == Rivet.INSTRUCT_GETKEY && resultCode == RESULT_OK) {
            // change the intent fired to GETKEY to display the public key
            String pubKey = data.getStringExtra(Rivet.EXTRA_PUBLICDATA);
            output.append("pubkey=" + pubKey + "\n");

        // on a response from SIGN print the returned signature
        } else if (requestCode == Rivet.INSTRUCT_SIGN && resultCode == RESULT_OK) {
            String signature = data.getStringExtra(Rivet.EXTRA_SIGNATURE);
            output.append("signature=" + signature + "\n");

        // on a successful response to pair device
        } else if (requestCode == Rivet.INSTRUCT_PAIRDEVICE && resultCode == RESULT_OK) {
            output.append("Device is paired");

        } else if (resultCode == RESULT_CANCELED) {
            output.append("cancelled request code = " + String.valueOf(requestCode) + "\n");
        } else {
            output.append("error:\n  Request Code = "+String.valueOf(requestCode)+"\n"+
                    "  Result Code = "+Rivet.FormatError(resultCode)+"\n");
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
