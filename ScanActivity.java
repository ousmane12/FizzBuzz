package com.sunmi.printerhelper.nfc;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import com.sunmi.printerhelper.R;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class ScanActivity extends AppCompatActivity {
    String tag;
    Tag tags;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            Tag tags = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            byte[] id = tags.getId();
            long payload = detectTagData(tags);
            tag = ByteArrayToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID));
            System.out.println("PayLoad "+payload);
            //tags = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            this.getIntent().putExtra("tag", tag);
            //readFromIntent(intent);
            //resolveIntent(intent);
            //setResult(RESULT_OK, this.getIntent());
            //finish();
            String action = intent.getAction();
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String s = action + "nn" + tag.toString();
            Parcelable[] data = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_AID);
            System.out.println("DATA "+data);
            if (data != null) {
                try {
                    for (int i = 0; i < data.length; i++) {
                        NdefRecord [] recs = ((NdefMessage)data[i]).getRecords();
                        for (int j = 0; j < recs.length; j++) {
                            if (recs[j].getTnf() == NdefRecord.TNF_WELL_KNOWN &&
                                    Arrays.equals(recs[j].getType(), NdefRecord.RTD_TEXT)) {
                                byte[] payloads = recs[j].getPayload();
                                String textEncoding = ((payloads[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
                                int langCodeLen = payloads[0] & 0077;

                                s += ("nnNdefMessage[" + i + "], NdefRecord[" + j + "]:n" + new String(payloads, langCodeLen + 1, payloads.length - langCodeLen - 1,
                                        textEncoding) + "");
                                System.out.println("Stering "+s);
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("TagDispatch", e.toString());
                }
            }
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        // creating pending intent:
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        // creating intent receiver for NFC events:
        IntentFilter filter = new IntentFilter();
        filter.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
        filter.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filter.addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, new IntentFilter[]{filter}, this.techList);
    }


    private long toDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = 0; i < bytes.length; ++i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

    private long detectTagData(Tag tag) {
        StringBuilder sb = new StringBuilder();
        byte[] id = tag.getId();
        sb.append("ID (dec): ").append(toDec(id)).append('\n');
        return toDec(id);
    }



    private final String[][] techList = new String[][]{
            new String[]{
                    NfcA.class.getName(),
                    NfcB.class.getName(),
                    NfcF.class.getName(),
                    NfcV.class.getName(),
                    IsoDep.class.getName(),
                    MifareClassic.class.getName(),
                    MifareUltralight.class.getName(), Ndef.class.getName()
            }
    };

    public String ByteArrayToHexString(byte[] inarray) {
        int i, j, in;
        String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        String out = "";

        for (j = 0; j < inarray.length; ++j) {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void readFromIntent(Intent intent) {
        System.out.println("Came huered ");
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            System.out.println("Here too");
            Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] messages ;
            System.out.println("Rwa "+rawMessages);
            if (rawMessages != null) {
                messages = new NdefMessage[rawMessages.length];
                for (int i = 0; i < rawMessages.length; i++) {
                    messages[i] = (NdefMessage) rawMessages[i];
                    NdefRecord [] records = messages[i].getRecords();
                    System.out.println("RECORDS "+records);
                    //if you are sure you have text then you don't need to test TNF
                    for(NdefRecord record: records){
                        processRecord(record);
                    }
                }
            }
        }
    }

    public void processRecord(NdefRecord record) {

        short tnf = record.getTnf();
        switch (tnf) {


            case NdefRecord.TNF_MIME_MEDIA: {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    if (record.toMimeType().equals("MIME/Type")) {
                        // handle this as you want
                        System.out.println("HEREEEE");
                    } else {
                        //Record is not our MIME
                    }
                }
            }
            // you can write more cases
            default: {
                //unsupported NDEF Record
            }
        }
    }

    private String processRtdTextRecord(byte[] payload) {
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
        int languageCodeLength = payload[0] & 0063;

        String text = "";
        try {
            text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e("UnsupportedEncoding", e.toString());
        }
        return text;
    }


    void resolveIntent(Intent intent) {
        // 1) Parse the intent and get the action that triggered this intent
        String action = intent.getAction();
        // 2) Check if it was triggered by a tag discovered interruption.
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            //  3) Get an instance of the TAG from the NfcAdapter
            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            // 4) Get an instance of the Mifare classic card from this TAG intent
            MifareClassic mfc = MifareClassic.get(tagFromIntent);
            byte[] data;

            try {       //  5.1) Connect to card
                mfc.connect();
                boolean auth = false;
                String cardData = null;
                // 5.2) and get the number of sectors this card has..and loop thru these sectors
                int secCount = mfc.getSectorCount();
                int bCount = 0;
                int bIndex = 0;
                for (int j = 0; j < secCount; j++) {
                    // 6.1) authenticate the sector
                    auth = mfc.authenticateSectorWithKeyA(j, MifareClassic.KEY_DEFAULT);
                    if (auth) {
                        // 6.2) In each sector - get the block count
                        bCount = mfc.getBlockCountInSector(j);
                        bIndex = 0;
                        for (int i = 0; i < bCount; i++) {
                            bIndex = mfc.sectorToBlock(j);
                            // 6.3) Read the block
                            data = mfc.readBlock(bIndex);
                            // 7) Convert the data into a string from Hex format.
                            System.out.println("TAG data "+ data);
                            bIndex++;
                        }
                    } else { // Authentication failed - Handle it

                    }
                }
            } catch (IOException e) {
                Log.e("TAG", e.getLocalizedMessage());
                //showAlert(3);
            }
        }// End of method

    }



    }
