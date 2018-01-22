package ajanta.ankit.smsresponder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.List;

import ajanta.ankit.smsresponder.database.Contact;
import ajanta.ankit.smsresponder.database.DatabaseHandler;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.TelephonyManager;

public class IncomingCallListener extends BroadcastReceiver {
	boolean ring = false;
	String callerPhoneNumber = "";
	Context ctx;

	@Override
	public void onReceive(Context context, Intent intent) {
		ctx = context;
		String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
		if (state == null)
			return;
		if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {

			ring = true;
			// Get the Caller's Phone Number
			Bundle bundle = intent.getExtras();
			callerPhoneNumber = bundle.getString("incoming_number");
			disconnectCall();
			if (!isDuplicate())
				sendResponse();
		}
	}

	private boolean isDuplicate() {
		DatabaseHandler db = new DatabaseHandler(ctx);
		List<Contact> contacts = db.getAllContacts();
		for (Contact cn : contacts) {
			if (cn.getPhoneNumber().trim().equals(callerPhoneNumber.trim())) {
				return true;
			}
		}
		db.addContact(new Contact(callerPhoneNumber));
		return false;
	}

	private void sendResponse() {
		String message = "Response Message Not Configured";
		String FILENAME = "ResponseMessage";
		try {
			InputStream fis = ctx.getApplicationContext().openFileInput(
					FILENAME);
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		new MessageAPIRequest().execute(callerPhoneNumber, message);
	}

	public void disconnectCall() {
		try {

			String serviceManagerName = "android.os.ServiceManager";
			String serviceManagerNativeName = "android.os.ServiceManagerNative";
			String telephonyName = "com.android.internal.telephony.ITelephony";
			Class<?> telephonyClass;
			Class<?> telephonyStubClass;
			Class<?> serviceManagerClass;
			Class<?> serviceManagerNativeClass;
			Method telephonyEndCall;
			Object telephonyObject;
			Object serviceManagerObject;
			telephonyClass = Class.forName(telephonyName);
			telephonyStubClass = telephonyClass.getClasses()[0];
			serviceManagerClass = Class.forName(serviceManagerName);
			serviceManagerNativeClass = Class.forName(serviceManagerNativeName);
			Method getService = // getDefaults[29];
			serviceManagerClass.getMethod("getService", String.class);
			Method tempInterfaceMethod = serviceManagerNativeClass.getMethod(
					"asInterface", IBinder.class);
			Binder tmpBinder = new Binder();
			tmpBinder.attachInterface(null, "fake");
			serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder);
			IBinder retbinder = (IBinder) getService.invoke(
					serviceManagerObject, "phone");
			Method serviceMethod = telephonyStubClass.getMethod("asInterface",
					IBinder.class);
			telephonyObject = serviceMethod.invoke(null, retbinder);
			telephonyEndCall = telephonyClass.getMethod("endCall");
			telephonyEndCall.invoke(telephonyObject);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
