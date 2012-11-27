package de.me.gradeplanner.ui.fragments;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Service;
import android.app.ActionBar.OnNavigationListener;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;
import de.me.gradeplanner.R;
import de.me.gradeplanner.business.Grade;
import de.me.gradeplanner.business.GradePlanner;
import de.me.gradeplanner.business.Subject;
import de.me.gradeplanner.ui.dialogs.ManageGradeDialog;

@TargetApi(11)
public class SubjectDetailFragmentHoneycomb extends SubjectDetailFragment implements OnNavigationListener {	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Make activity title a Spinner
		ActionBar actionBar = this.getActivity().getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setListNavigationCallbacks(new ArrayAdapter<Subject>(this.getActivity(), android.R.layout.simple_spinner_item, GradePlanner.getInstance(this.getActivity()).getSubjects()) {
			// TODO Set color of dropdown items with theme
			public View getView(int position, View convertView, ViewGroup parent) {
				TextView textView;
        		
        		if (convertView == null) {
        			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        			textView = (TextView) inflater.inflate(android.R.layout.simple_spinner_item, null);
        		} else {
        			textView = (TextView) convertView;
        		}
        		
        		textView.setText(this.getItem(position).getName());
        		textView.setTextColor(Color.WHITE);
        		
				return textView;
			}

			// TODO Set size and color of items with theme
			@Override
			public View getDropDownView(int position, View convertView, ViewGroup parent) {
				CheckedTextView textView;
        		
        		if (convertView == null) {
        			LayoutInflater inflater = (LayoutInflater) getActivity().getLayoutInflater();
        			textView = (CheckedTextView) inflater.inflate(android.R.layout.simple_spinner_dropdown_item, null);
        		} else {
        			textView = (CheckedTextView) convertView;
        		}
        		
        		textView.setText(this.getItem(position).getName());
        		textView.setTextColor(Color.WHITE);
        		textView.setHeight(72);
        		
				return textView;
			}
		}, this);
		actionBar.setSelectedNavigationItem(this.getArguments().getInt(ARG_INDEX));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.getFragmentManager().popBackStack();
			break;
			
		case R.id.menu_add_grade:
			ManageGradeDialog dialog = new ManageGradeDialog(this.index) {
				protected void onPositiveClick() {
					if (index == this.subjectIndex) {
						((ArrayAdapter<Grade>) getListAdapter()).notifyDataSetChanged();
					} else {
						getActivity().getActionBar().setSelectedNavigationItem(this.subjectIndex);
					}
				}
			};
			dialog.show(this.getFragmentManager(), "add_grade");
			break;
		}
		
		return true;
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.edit_grade) {
			final int gradeIndex = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
			final Grade grade = GradePlanner.getInstance(this.getActivity()).getSubjects().get(this.index).getGrades().get(gradeIndex);
			
			new ManageGradeDialog(this.index, grade) {
				@Override
				protected void onPositiveClick() {
					if (index == this.subjectIndex) {
						((ArrayAdapter<Grade>) getListAdapter()).notifyDataSetChanged();
					} else {
						getActivity().getActionBar().setSelectedNavigationItem(this.subjectIndex);
					}
				}
			}.show(this.getFragmentManager(), "add_grade");
			
			return true;
		}
			
		return super.onContextItemSelected(item);
	}
	
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		this.index = itemPosition;
		this.setListAdapter();
		
		return false;
	}
}
