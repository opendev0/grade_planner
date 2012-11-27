package de.me.gradeplanner.ui.fragments;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import de.me.gradeplanner.R;
import de.me.gradeplanner.business.GradePlanner;
import de.me.gradeplanner.business.Subject;
import de.me.gradeplanner.ui.activities.ApplicationPreferenceActivity;
import de.me.gradeplanner.ui.dialogs.ManageSubjectDialog;

public class SubjectOverviewFragment extends ListFragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_subject_overview, container, false);
	}
	
	@TargetApi(11)
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionBar = this.getActivity().getActionBar();
			actionBar.setDisplayShowTitleEnabled(true);
			actionBar.setDisplayHomeAsUpEnabled(false);
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		}
		
		final String averageCalcMethod = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getString("calc_method_setting", "default");
		
		this.setHasOptionsMenu(true);
		this.registerForContextMenu(this.getListView());
		this.setListAdapter(new ArrayAdapter<Subject>(getActivity(), R.layout.list_subjects, GradePlanner.getInstance(getActivity()).getSubjects()) {
        	@Override
        	public View getView(int position, View convertView, ViewGroup parent) {
        		final LinearLayout layout;
        		
        		if (convertView == null) {
        			LayoutInflater inflater = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        			layout = (LinearLayout) inflater.inflate(R.layout.list_subjects, null);
        		} else {
        			layout = (LinearLayout) convertView;
        		}
        		
        		final Subject item = this.getItem(position);
        		final double average = averageCalcMethod.equals("bos") ? item.getBosAverage() : item.getAverage();
        		final String teacher = item.getTeacher();
        		final TextView txtSubjectName = (TextView) layout.findViewById(R.id.item_subject);
        		final TextView txtGradeAverage = (TextView) layout.findViewById(R.id.item_average);
        		final TextView txtTeacher = (TextView) layout.findViewById(R.id.item_teacher);
        		
        		txtSubjectName.setText(item.getName());
        		
        		if (average == 0) {
        			((TextView) layout.findViewById(R.id.average_label)).setVisibility(View.GONE);
        			txtGradeAverage.setText(R.string.empty_grades);
        		} else {
        			txtGradeAverage.setText(average + " NP");
        		}
        		
        		if (teacher == null) {
        			txtTeacher.setVisibility(View.GONE);
        		} else {
        			txtTeacher.setText(teacher);
        		}
        		
        		return layout;
        	}
        });
	}
	
	/**
	 * Open menu if necessary
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

	/**
	 * Things to do for menu-buttons
	 */
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_create_subject:
			ManageSubjectDialog dialog = new ManageSubjectDialog() {
				@Override
				protected void onPositiveClick() {
					((ArrayAdapter<Subject>) getListAdapter()).notifyDataSetChanged();
				}
			};			
			dialog.show(this.getFragmentManager(), "add_subject");
			break;
			
		case R.id.menu_settings:
			this.startActivity(new Intent(this.getActivity(), ApplicationPreferenceActivity.class));
			break;
		}
		
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.setHeaderTitle(R.string.title_context_actions);
		this.getActivity().getMenuInflater().inflate(R.menu.subject_context, menu);
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		/*case R.id.menu_add_grade:
			AddGradeDialog dialog = new AddGradeDialog(((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position) {
				@Override
				public void onDetach() {
					super.onDetach();
					
					// TODO Only update listview if ok button was clicked
					((ArrayAdapter<Subject>) getListAdapter()).notifyDataSetChanged();
				}
			};
			
			dialog.show(this.getFragmentManager(), "add_grade");
			break;*/
			
		case R.id.menu_delete_subject:
			AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
			builder.setTitle("Bist du sicher?");
			builder.setMessage("Willst du das Fach wirklich löschen?");
			builder.setPositiveButton("Ja", new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					GradePlanner.getInstance(getActivity()).deleteSubject(((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position);
					((ArrayAdapter<Subject>) getListAdapter()).notifyDataSetChanged();
				}
			});
			builder.setNegativeButton("Nein", new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.show();
			
			break;
			
		case R.id.menu_edit_subject:
			ManageSubjectDialog dialog = new ManageSubjectDialog(GradePlanner.getInstance(this.getActivity()).getSubjects().get(((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position)) {
				@Override
				protected void onPositiveClick() {
					((ArrayAdapter<Subject>) getListAdapter()).notifyDataSetChanged();
				}
			};
			dialog.show(this.getFragmentManager(), "add_subject");
			break;
		}
		
		return super.onContextItemSelected(item);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		if (this.getActivity().findViewById(R.id.fragment_container) != null) {
			SubjectDetailFragment detail = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) ? new SubjectDetailFragmentHoneycomb() : new SubjectDetailFragment();
			Bundle args = new Bundle();
			args.putInt(SubjectDetailFragment.ARG_INDEX, position);
			detail.setArguments(args);
			
			FragmentTransaction transaction = this.getActivity().getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.fragment_container, detail);
			transaction.addToBackStack(null);
			transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			transaction.commit();
		}
	}
}
