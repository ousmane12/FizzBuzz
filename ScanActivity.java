package com.sunmi.printerhelper.nfc;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.TagLostException;
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
import android.widget.Toast;

import com.sunmi.printerhelper.R;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

@SuppressLint("NewApi")
public class ScanActivity extends AppCompatActivity implements NfcAdapter.ReaderCallback {
    String tag;
    Tag tags;
    private NfcAdapter mNfcAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

    }

//    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
//    @Override
//    public void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        if ( intent.getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED) ) {
//            Tag tags = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
//            System.out.println("Intent "+intent.getAction());
//            long payload = detectTagData(tags);
//            tag = ByteArrayToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID));
//            System.out.println("PayLoad "+payload);
//            //tags = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
//            this.getIntent().putExtra("tag", tag);
//            readFromIntent(intent);
//            //resolveIntent(intent);
//            //setResult(RESULT_OK, this.getIntent());
//            //finish();
//
//
//
//
//        }
//
//    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            parseNdefMessage(intent);
        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();
        // creating pending intent:
        if(mNfcAdapter!= null) {
            Bundle options = new Bundle();
            // Work around for some broken Nfc firmware implementations that poll the card too fast
            options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 250);

            // Enable ReaderMode for all types of card and disable platform sounds
            mNfcAdapter.enableReaderMode(this,
                    this,
                    NfcAdapter.FLAG_READER_NFC_A |
                            NfcAdapter.FLAG_READER_NFC_B |
                            NfcAdapter.FLAG_READER_NFC_F |
                            NfcAdapter.FLAG_READER_NFC_V |
                            NfcAdapter.FLAG_READER_NFC_BARCODE |
                            NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS,
                    options);
        }

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

        String action = intent.getAction();
        System.out.println("Action "+action.toString());
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            System.out.println("enterewd "+NfcAdapter.ACTION_TAG_DISCOVERED);
            Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            System.out.println("NUOO "+rawMessages);
            NdefMessage[] messages ;
            if (rawMessages != null) {
                messages = new NdefMessage[rawMessages.length];
                for (int i = 0; i < rawMessages.length; i++) {
                    messages[i] = (NdefMessage) rawMessages[i];
                    NdefRecord [] records = messages[i].getRecords();
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
        byte[] type = record.getType();

        System.out.println("TNF "+tnf);
        System.out.println("TYPE "+type);
        if (tnf == NdefRecord.TNF_WELL_KNOWN &&
                Arrays.equals(type, NdefRecord.RTD_TEXT)) {
            // Correct TNF and Type for Text record
            // Now process the Text Record encoding
            System.out.println("fghjkg "+processRtdTextRecord(type));
            System.out.println("TEXT RECORD>>> "+NdefRecord.RTD_TEXT);
        }
//        switch (tnf) {
//
//            case NdefRecord.TNF_MIME_MEDIA: {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//        if (record.toMimeType().equals("text/plain")){
//                    if (tnf == NdefRecord.TNF_WELL_KNOWN &&
//                            Arrays.equals(type, NdefRecord.RTD_TEXT)){
//                        // Correct TNF and Type for Text record
//                        // Now process the Text Record encoding
//
//                    }
//                }
//            }
//            // you can write more cases
//            default: {
//                //unsupported NDEF Record
//            }
//        }
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

    void parseNdefMessage(Intent intent) {
        Parcelable[] ndefMessageArray = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // Test if there is actually a NDef message passed via the Intent
        if (ndefMessageArray != null) {
            NdefMessage ndefMessage = (NdefMessage) ndefMessageArray[0];
            //Get Bytes of payload
            byte[] payload = ndefMessage.getRecords()[0].getPayload();
            // Read First Byte and then trim off the right length
            byte[] textArray = Arrays.copyOfRange(payload, (int) payload[0] + 1, payload.length);
            // Convert to Text
            String text = new String(textArray);
            System.out.println("Text "+text);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onPause() {
        super.onPause();
        if(mNfcAdapter!= null)
            mNfcAdapter.disableReaderMode(this);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onTagDiscovered(Tag tag) {
        // Read and or write to Tag here to the appropriate Tag Technology type class
        // in this example the card should be an Ndef Technology Type
        Ndef mNdef = Ndef.get(tag);

        // Check that it is an Ndef capable card
        if (mNdef!= null) {

            // If we want to read
            // As we did not turn on the NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK
            // We can get the cached Ndef message the system read for us.

            NdefMessage mNdefMessage = mNdef.getCachedNdefMessage();


            // Or if we want to write a Ndef message

            // Create a Ndef Record
            NdefRecord mRecord = NdefRecord.createTextRecord("en","English String");

            // Add to a NdefMessage
            NdefMessage mMsg = new NdefMessage(mRecord);

            // Catch errors
            try {
                mNdef.connect();
                mNdef.writeNdefMessage(mMsg);

                // Success if got to here
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(),
                            "Write to NFC Success",
                            Toast.LENGTH_SHORT).show();
                });

                // Make a Sound
                try {
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(),
                            notification);
                    r.play();
                } catch (Exception e) {
                    // Some error playing sound
                }

            } catch (FormatException e) {
                // if the NDEF Message to write is malformed
            } catch (TagLostException e) {
                // Tag went out of range before operations were complete
            } catch (IOException e){
                // if there is an I/O failure, or the operation is cancelled
            } finally {
                // Be nice and try and close the tag to
                // Disable I/O operations to the tag from this TagTechnology object, and release resources.
                try {
                    mNdef.close();
                } catch (IOException e) {
                    // if there is an I/O failure, or the operation is cancelled
                }
            }

        }

    }
}
