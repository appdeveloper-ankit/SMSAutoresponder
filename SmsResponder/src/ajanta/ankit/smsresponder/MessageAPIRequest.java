package ajanta.ankit.smsresponder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.telephony.SmsManager;

public class MessageAPIRequest extends AsyncTask<String, Void, Boolean> {
	String callerPhoneNumber, message;

	@Override
	protected Boolean doInBackground(String... params) {
		callerPhoneNumber = params[0];
		message = params[1];
		String url = "http://www.saismsindia.com/pushsms.php?username=ankit"
				+ "&api_password=3e881urq43mykksrw" + "&sender=test" + "&to="
				+ callerPhoneNumber.replace("+91", "") + "&message=" + message
				+ "&priority=1";
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			String xml = EntityUtils.toString(httpEntity);
			if (xml.toLowerCase().contains("sorry"))
				return false;
			else if (xml.toLowerCase().contains("wrong"))
				return false;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	protected void onPostExecute(Boolean messageSent) {
		SmsManager manager = SmsManager.getDefault();
		if (!messageSent)
			manager.sendTextMessage(callerPhoneNumber, null, message, null,
					null);
	}
}