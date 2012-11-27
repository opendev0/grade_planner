package de.me.gradeplanner.ui.activities;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.support.v4.app.NavUtils;
import android.text.InputType;
import android.view.MenuItem;
import de.me.gradeplanner.R;

public class ApplicationPreferenceActivity extends PreferenceActivity {
	private EditTextPreference txtMinGrade;
	private EditTextPreference txtMaxGrade;
	
	
	@TargetApi(11)
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle(R.string.title_activity_settings);
        this.addPreferencesFromResource(R.xml.preferences);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        	this.getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        this.txtMinGrade = (EditTextPreference) this.findPreference("min_grade_setting");
        this.txtMaxGrade = (EditTextPreference) this.findPreference("max_grade_setting");
        
        this.txtMinGrade.getEditText().setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        this.txtMaxGrade.getEditText().setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
    }

	@TargetApi(11)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	        switch (item.getItemId()) {
	            case android.R.id.home:
	                NavUtils.navigateUpFromSameTask(this);
	                return true;
	        }
		}
        
        return super.onOptionsItemSelected(item);
    }
}
