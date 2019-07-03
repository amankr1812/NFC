package com.example.amank.checknfc;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

public class Page1 extends AppCompatActivity {
    NfcAdapter nfcAdapter;
    Button b1;
    EditText et1;






    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page1);

        nfcAdapter=NfcAdapter.getDefaultAdapter(this);




        if(nfcAdapter!=null && nfcAdapter.isEnabled())
        {
        }
        else
        {
            finish();
        }


    }





    protected void onResume()
    {
        enableForegroundDispatchSystem();
        super.onResume();
    }

    protected void onPause()
    {
        disableForegroundDispatchSystem();
        super.onPause();
    }

    @Override
    public void onNewIntent(final Intent intent)  {
        Button b1=(Button)findViewById(R.id.b1);
        assert b1 != null;
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
                    Toast.makeText(Page1.this, "DATA SAVED", Toast.LENGTH_SHORT).show();

                    Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

                    String ki = "";

                    EditText et1 = (EditText) findViewById(R.id.et1);

                    String txt = et1.getText().toString();
                    NdefMessage ndefMessage = createNdefMessage(txt);

                    writeNdefMessage(tag, ndefMessage);
                    makeReadOnly(tag);


                }

            }
        });
        super.onNewIntent(intent);
    }

    private void enableForegroundDispatchSystem()
    {

        Intent in=new Intent(this,Page1.class);
        in.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pn=PendingIntent.getActivity(this,0,in,0);
        IntentFilter[] filters = new IntentFilter[]{};
        nfcAdapter.enableForegroundDispatch(this, pn, filters, null);
    }

    private void disableForegroundDispatchSystem()
    {
        nfcAdapter.disableForegroundDispatch(this);
    }


    public void formatTag(Tag tag,NdefMessage ndefMessage)
    {
        try
        {
            NdefFormatable ndefFormatable = NdefFormatable.get(tag);

            if (ndefFormatable == null) {


                Toast.makeText(Page1.this, "Tag is not ndef formatable", Toast.LENGTH_SHORT).show();
                return;
            }


            ndefFormatable.connect();
            ndefFormatable.format(ndefMessage);
            ndefFormatable.close();

            Toast.makeText(Page1.this, "Tag Written", Toast.LENGTH_LONG).show();

        }
        catch (Exception e)
        {
            Log.e("formatTag",e.getMessage());

        }
    }

    private void writeNdefMessage(Tag tag, NdefMessage ndefMessage)
    {

        try
        {
            if(tag==null)
            {
                Toast.makeText(Page1.this, "tag object cannot be null", Toast.LENGTH_SHORT).show();
                return;
            }

            Ndef ndef=Ndef.get(tag);

            if(ndef==null)
            {
                //format tag goes here with the ndef format and write the message
                formatTag(tag,ndefMessage);
            }
            else
            {
                ndef.connect();

                if(!ndef.isWritable())
                {
                    Toast.makeText(Page1.this, "tag is not writable", Toast.LENGTH_SHORT).show();
                    ndef.close();
                    return;
                }

                ndef.writeNdefMessage(ndefMessage);
                ndef.close();



            }



        }
        catch (Exception e)
        {
            Log.e("writeNdefMessage",e.getMessage());
        }
    }

    private NdefRecord createTextRecord(String content)
    {
        try
        {
            byte[] language;
            language = Locale.getDefault().getLanguage().getBytes("utf-8");

            final byte[] text=content.getBytes("utf-8");
            final int languageSize=language.length;
            final int textLength=text.length;
            final ByteArrayOutputStream payload=new ByteArrayOutputStream(1+languageSize+textLength);

            payload.write((byte) (languageSize & 0x1F));
            payload.write(language,0,languageSize);
            payload.write(text,0,textLength);


            return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0],payload.toByteArray());
        }

        catch (UnsupportedEncodingException e)
        {
            Log.e("createTextRecord",e.getMessage());
        }
        return null;
    }

    private NdefMessage createNdefMessage(String content)
    {
        NdefRecord ndefRecord = createTextRecord(content);

        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{ndefRecord});

        return ndefMessage;
    }



    public void makeReadOnly(Tag tag) {
        if (tag == null) {
            Toast.makeText(Page1.this, "Tag is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        try {

            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {

                ndef.connect();

                if(ndef.canMakeReadOnly()){
                    //ndef.canMakeReadOnly();
                    Toast.makeText(Page1.this, "CARD MADE READ-ONLY", Toast.LENGTH_SHORT).show();
                }


                //  ndef.canMakeReadOnly();
                ndef.close();
                //  Log.e("22222.......", "2222.......");

            }




        } catch (IOException e) {
            // TODO Auto-generated catch block

            e.printStackTrace();
        }

    }




}
