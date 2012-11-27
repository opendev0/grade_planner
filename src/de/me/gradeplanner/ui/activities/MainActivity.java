package de.me.gradeplanner.ui.activities;

import android.os.Bundle;
import de.me.gradeplanner.R;
import de.me.gradeplanner.ui.fragments.SubjectDetailFragment;
import de.me.gradeplanner.ui.fragments.SubjectOverviewFragment;

public class MainActivity extends android.support.v4.app.FragmentActivity {
	/**
	 * Create the list adapter and register for context menu
	 */
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle(R.string.title_activity_main);
        this.setContentView(R.layout.activity_main);
        
        if (this.findViewById(R.id.fragment_container) != null) {
        	if (savedInstanceState != null) {
        		return;
        	}
        	
        	SubjectOverviewFragment detail = new SubjectOverviewFragment();
        	detail.setArguments(this.getIntent().getExtras());
        	this.getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, detail).commit();
        } else {
        	if (savedInstanceState != null) {
        		return;
        	}
        	
        	SubjectOverviewFragment overview = new SubjectOverviewFragment();
        	SubjectDetailFragment detail = new SubjectDetailFragment();
        	detail.setArguments(this.getIntent().getExtras());
        	this.getSupportFragmentManager().beginTransaction().add(R.id.subject_list, overview).commit();
        	this.getSupportFragmentManager().beginTransaction().add(R.id.subject_detail, detail).commit();
        }
    }
}
