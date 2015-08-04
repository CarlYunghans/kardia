package org.lightsys.donorapp;

import org.lightsys.donorapp.data.Account;
import org.lightsys.donorapp.data.DataConnection;
import org.lightsys.donorapp.data.GenericTextWatcher;
import org.lightsys.donorapp.data.LocalDBHandler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.donorapp.R;

import java.util.ArrayList;

/**
 * This activity recieves the selected account's user name, user password, 
 * and server name (address) and the id for that account from the SQLiteDatabase.
 * Populating editTexts with those things (except for the id), allowing the user
 * to change any of them and click the update button to save the changes to the
 * database.
 * 
 * After clicking update or the back button takes them back to account page.  
 * 
 * @author Andrew Cameron
 *
 */
public class EditAccountActivity extends Activity{

	String oldName, oldPass, oldServer, oldDonorId, name, pass, server, donorId;
	EditText editName, editPass, editServer, editDonorId;
	Button submit, cancel;
	private static Boolean isValidAccount = null;
	private static AccountsActivity.ErrorType errorType = null;
	
	/**
	 * Loads current data for the selected account.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
    	setContentView(R.layout.account_edit_layout);
    	
    	editName = (EditText)findViewById(R.id.userName);
    	editPass = (EditText)findViewById(R.id.userPass);
    	editServer = (EditText)findViewById(R.id.serverName);
    	editDonorId = (EditText)findViewById(R.id.donorId);
    	submit = (Button)findViewById(R.id.submit);
		cancel = (Button)findViewById(R.id.cancel);

		// Adds EditTexts to text listener for resetting errors
		editName.addTextChangedListener(new GenericTextWatcher(editName));
		editPass.addTextChangedListener(new GenericTextWatcher(editPass));
		editServer.addTextChangedListener(new GenericTextWatcher(editServer));
		editDonorId.addTextChangedListener(new GenericTextWatcher(editDonorId));
    	
    	Intent intent = getIntent();
    	
    	final int id = intent.getIntExtra("theid", -1);

		oldName = intent.getStringExtra("oldname");
		oldPass = intent.getStringExtra("oldpass");
		oldServer = intent.getStringExtra("oldserver");
		oldDonorId = intent.getIntExtra("olddonorid", -1) + "";

    	editName.setText(oldName);
    	editPass.setText(oldPass);
    	editServer.setText(oldServer);
    	editDonorId.setText(oldDonorId);

		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});

    	submit.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				LocalDBHandler db = new LocalDBHandler(EditAccountActivity.this, null, null, 9);
				name = editName.getText().toString();
				pass = editPass.getText().toString();
				server = editServer.getText().toString();
				donorId = editDonorId.getText().toString();
				ArrayList<Account> accounts = db.getAccounts();

				int newId = Integer.parseInt(donorId);

				// If account already stored, display message and return
				for(Account a : accounts){
					if(a.getAccountName().equals(name) && a.getServerName().equals(server) &&
							a.getAccountPassword().equals(pass) && a.getDonorid() == newId){
						Toast.makeText(EditAccountActivity.this, "Account already stored", Toast.LENGTH_LONG).show();
						db.close();
						return;
					}
				}
				// If any of the fields are blank or invalid, set error and return
				if(name == null || name.equals("")){
					editName.setError("Invalid Username");
					db.close();
					return;
				}	
				if(pass == null || pass.equals("")){
					editPass.setError("Invalid Password");
					db.close();
					return;
				}	
				if(server == null || server.equals("")){
					editServer.setError("Invalid Server Address");
					db.close();
					return;
				}	
				if(donorId == null || donorId.equals("")){
					editDonorId.setError("Invalid Donor ID.");
					db.close();
					return;
				}

				Account a = new Account(id, name, pass, server, newId);
				// Execute data connection
				new DataConnection(EditAccountActivity.this, a).execute("");

				// Wait for async task to signal whether account is valid or not
				while (isValidAccount == null) {
					continue;
				}
				if (isValidAccount) {
					db.updateAccount(id, editName.getText().toString(), editPass.getText().toString(), editServer.getText().toString(), newId);
					Toast.makeText(EditAccountActivity.this, "Account updated.", Toast.LENGTH_SHORT).show();
					// Set asynchronous flags back to null for next attempted account
					isValidAccount = null;
					errorType = null;
					db.close();
				} else {
					// Set error statement based on error provided by async task
					String errorStatement;
					if (errorType == AccountsActivity.ErrorType.NotFound) {
						errorStatement = "No account with this Donor ID";
					} else if (errorType == AccountsActivity.ErrorType.Server) {
						errorStatement = "Could not connect to specified server";
					} else if (errorType == AccountsActivity.ErrorType.Unauthorized) {
						errorStatement = "Username or Password is incorrect";
					} else {
						errorStatement = "Unknown issue. \n 1) Check Internet connection" +
								"\n 2) Server may be down";
					}
					Toast.makeText(EditAccountActivity.this, "Connecting account failed. \n -" + errorStatement,
							Toast.LENGTH_LONG).show();
					// set async flags back to null for next account connection
					isValidAccount = null;
					errorType = null;
					db.close();
					return;
				}
				finish();
			}
		});
	}

	/**
	 * sets whether or not the account attempting to be updated is a valid account
	 * @param isValid, boolean value if account if valid
	 */
	public static void setValidation(boolean isValid) {
		isValidAccount = isValid;
	}

	/**
	 * sets the error type of an invalid account
	 * @param error, the type of error causing the account connection to fail
	 */
	public static void setErrorType(AccountsActivity.ErrorType error) {
		errorType = error;
	}
}
