package ajanta.ankit.smsresponder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.lang.String;

public class MainActivity extends Activity {
	Button btnContentSettings,btnExportData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		btnContentSettings = (Button) findViewById(R.id.btnContentSettings);
        btnExportData = (Button) findViewById(R.id.btnExportData);
		btnContentSettings.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			Intent intent = new Intent(MainActivity.this,ContentSettings.class);
			startActivity(intent);
			}
		});
        btnExportData.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                exportContactList();
            }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
    private void exportContactList() {
        if (!Environment.getExternalStorageDirectory().canWrite()) {
            Toast.makeText(MainActivity.this, "Cannot Export File", Toast.LENGTH_LONG);
            return;
        }
        File fileDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/sanjanaCinema/");
        fileDir.mkdirs();
        String outFileName = fileDir + "/contactList.csv";
        File outFile = new File(outFileName);
        FileWriter fileWriter = new FileWriter(outFile);
        BufferedWriter out = new BufferedWriter(fileWriter);
        DatabaseHandler db = new DatabaseHandler(MainActivity.this);
        List<Contact> contacts = db.getAllContactsExport();
        for (Contact cn : contacts) {
            out.write(cn.getPhoneNumber() + "," + cn.getDate());
        }
        out.close();
    }
}
