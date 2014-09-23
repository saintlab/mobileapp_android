package com.omnom.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.omnom.android.acquiring.mailru.AcquiringMailRu;
import com.omnom.android.acquiring.mailru.MailRuCardInfo;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
	    AcquiringMailRu acquiring = new AcquiringMailRu(this);
	    MailRuCardInfo cardInfo = MailRuCardInfo.create("4111111111111111", "12/18", getString(R.string.acquiring_mailru_test_cvv));
	    acquiring.registerCard(cardInfo.getCardInfo(), "test@omnom.menu", "+7-913-913-1313");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
