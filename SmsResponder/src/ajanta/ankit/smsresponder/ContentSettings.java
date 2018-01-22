package ajanta.ankit.smsresponder;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ContentSettings extends Activity {
	Button btnSave;
	EditText edtMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content_settings);
		edtMessage = (EditText) findViewById(R.id.edtResponseText);
		btnSave = (Button) findViewById(R.id.btnSaveResponseConfig);
		loadSavedMessage();
		btnSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String FILENAME = "ResponseMessage";
				try {
					FileOutputStream fos = openFileOutput(FILENAME,
							Context.MODE_PRIVATE);

					fos.write(String.valueOf(edtMessage.getText()).getBytes());
					fos.close();
					Toast.makeText(ContentSettings.this,
							"Response Message Saved Successfully",
							Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
	}

	private void loadSavedMessage() {
		String message = "Test Message";
		String FILENAME = "ResponseMessage";
		try {
			InputStream fis = getApplicationContext().openFileInput(FILENAME);
			if (fis != null) {
				InputStreamReader inputStreamReader = new InputStreamReader(fis);
				BufferedReader bufferedReader = new BufferedReader(
						inputStreamReader);
				String receiveString = "";
				StringBuilder stringBuilder = new StringBuilder();

				while ((receiveString = bufferedReader.readLine()) != null) {
					stringBuilder.append(receiveString);
				}

				message = stringBuilder.toString();
			}
			fis.close();
			edtMessage.setText(message);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
