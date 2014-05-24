package com.abk.newBus;

import org.json.JSONArray;
import org.json.JSONObject;

import com.abk.newBus.MainActivity;
import com.abk.newBus.PostHTTP;
import com.abk.newBus.InterpretApi;
import com.example.nextBus.R;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
 
public class MainActivity extends Activity {
 
     TextView tv_Result, tv_View;
     EditText et_Text;
     
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_Result = (TextView)  findViewById(R.id.tv_Result);
        tv_View = (TextView)findViewById(R.id.tv_View);
	    et_Text = (EditText)findViewById(R.id.et_Text);;
        
        addBussStopHandler();
    }
    
    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected())
                return true;
            else
                return false;  
    }
    
    
    public void addBussStopHandler() {
	    final String url = "https://api.octranspo1.com/v1.2/GetNextTripsForStopAllRoutes";
	    final MainActivity m = this;
	    
	    et_Text.setOnEditorActionListener(new OnEditorActionListener() {
	        @Override
	        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
	            boolean handled = false;
	            if (actionId == EditorInfo.IME_ACTION_SEND) {
	            	
	            	if (isConnected()) {
                		Editable s = et_Text.getText();
                		if (s.length() == 4) {
                			PostHTTP task = new PostHTTP(m);
                			task.execute(url, s.toString());
                			handled = true;
                		}
            	    }
	            }
	            return handled;
	        }
	    });
	    
	  //add new KeyListener Callback...this is to handle the case where we have a keyboard (debug)
	    et_Text.setOnKeyListener(new OnKeyListener()
	    {
	        public boolean onKey(View v, int keyCode, KeyEvent event)
	        {
	            if (event.getAction() == KeyEvent.ACTION_DOWN)
	            {
	                if (keyCode == KeyEvent.KEYCODE_ENTER)
	                {
	                	if (isConnected()) {
	                		Editable s = et_Text.getText();
	                		if (s.length() == 4) {
	                			PostHTTP task = new PostHTTP(m);
	                			task.execute(url, s.toString());
	                		}
	            	    }
	                    return true;
	                }
	            }
	            return false;
	        }
	    });
	    
	    
	    // check if you are connected or not
        if(isConnected()){
        	tv_View.setBackgroundColor(0xFF00CC00);
        	tv_View.setText("Ready");
        }
        else{
        	tv_View.setBackgroundColor(0xFFDC381F);
        	tv_View.setText("No data connection");
        }
    }
    
    public void handleResult(String responseString){
    	String display = "No Results, please try another stop";
    	
    	try {
	    	JSONObject json = new JSONObject(responseString);
	    	
	    	if (json.isNull("GetRouteSummaryForStopResult")) {
	    		display = getString(R.string.NoSummary);
	    	} else {
		    	JSONObject summary = json.getJSONObject("GetRouteSummaryForStopResult");
		    	display = InterpretApi.interpretRouteSummaryForStopResult(summary);
    		}
	    	
	    } catch (Exception e) {
			Log.e("Exception", e.getMessage());
			display = "No Results, please try another stop (ex001)";
		} finally {
		}
    	
    	tv_Result.setText(Html.fromHtml(display));
    }
}