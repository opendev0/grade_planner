package de.me.gradeplanner.ui.fragments;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TwoLineListItem;
import de.me.gradeplanner.R;
import de.me.gradeplanner.business.Grade;
import de.me.gradeplanner.business.GradePlanner;
import de.me.gradeplanner.ui.dialogs.ManageGradeDialog;

public class SubjectDetailFragment extends ListFragment {
	public final static String ARG_INDEX = "arg_index";
	
	protected int index;
	
	
	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_subject_detail, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		this.index = this.getArguments().getInt(ARG_INDEX, 0);
		
		this.setHasOptionsMenu(true);
		this.registerForContextMenu(this.getListView());
		this.setListAdapter();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_add_grade:
			new ManageGradeDialog(this.index) {
				protected void onPositiveClick() {
					if (index == this.subjectIndex) {
						((ArrayAdapter<Grade>) getListAdapter()).notifyDataSetChanged();
					} else {
						index = this.subjectIndex;
						setListAdapter();						
					}
				}
			}.show(this.getFragmentManager(), "add_grade");
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.subject_detail_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.setHeaderTitle(R.string.title_context_actions);
		this.getActivity().getMenuInflater().inflate(R.menu.subject_detail_context, menu);
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final int gradeIndex = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
		
		switch (item.getItemId()){
		case R.id.edit_grade:
			final Grade grade = GradePlanner.getInstance(this.getActivity()).getSubjects().get(this.index).getGrades().get(gradeIndex);
			
			new ManageGradeDialog(this.index, grade) {
				@Override
				protected void onPositiveClick() {
					if (index == this.subjectIndex) {
						((ArrayAdapter<Grade>) getListAdapter()).notifyDataSetChanged();
					} else {
						index = this.subjectIndex;
						setListAdapter();						
					}
				}
			}.show(this.getFragmentManager(), "add_grade");
			break;
			
		case R.id.delete_grade:
			AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
			builder.setTitle("Bist du sicher?");
			builder.setMessage("Willst du die Note wirklich löschen?");
			builder.setPositiveButton("Ja", new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					GradePlanner.getInstance(getActivity()).deleteGrade(index, gradeIndex);
					((ArrayAdapter<Grade>) getListAdapter()).notifyDataSetChanged();
				}
			});
			builder.setNegativeButton("Nein", new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.show();
			break;
		}
		
		return super.onContextItemSelected(item);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		l.showContextMenuForChild(v);
	}
	
	protected void setListAdapter() {
		this.setListAdapter(new ArrayAdapter<Grade>(this.getActivity(), android.R.layout.simple_list_item_2, GradePlanner.getInstance(this.getActivity()).getSubjects().get(this.index).getGrades()) {
			public View getView(int position, View convertView, ViewGroup parent) {
				TwoLineListItem row;
				
				if (convertView == null) {
        			LayoutInflater inflater = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        			row = (TwoLineListItem) inflater.inflate(android.R.layout.simple_list_item_2, null);
        		} else {
        			row = (TwoLineListItem) convertView;
        		}
				
				Grade item = this.getItem(position);
				
				TextView text1 = row.getText1();
				text1.setText("Punkte: " + item.getValue());
        		text1.setTextColor(Color.BLACK);
        		
        		String type;
        		switch (item.getWeight()) {
        		case 1:
        			type = "Ex/Kurzarbeit/mündlich";
        			break;
        			
        		case 2:
        			type = "Schulaufgabe";
        			break;
        			
    			default:
    				type = "";
    				break;
        		}
        		
        		TextView text2 = row.getText2();
        		text2.setText(type);
        		text2.setTextColor(0xff888888);
        		
        		return row;
			}
		});
	}
}
