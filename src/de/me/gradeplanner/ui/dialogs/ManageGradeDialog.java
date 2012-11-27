package de.me.gradeplanner.ui.dialogs;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.NumberPicker;
import android.widget.NumberPicker.Formatter;
import android.widget.Spinner;
import android.widget.TextView;
import de.me.gradeplanner.R;
import de.me.gradeplanner.business.Grade;
import de.me.gradeplanner.business.GradePlanner;
import de.me.gradeplanner.business.Subject;

@TargetApi(11)
public class ManageGradeDialog extends DialogFragment {
	protected int subjectIndex;
	protected Grade grade;
	protected Spinner spinSubject;
	
	
	public ManageGradeDialog(int subjectIndex) {
		this.subjectIndex = subjectIndex;
	}
	
	public ManageGradeDialog(int subjectIndex, Grade grade) {
		this.subjectIndex = subjectIndex;
		this.grade = grade;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {		
		// Create dialog and layout
		final AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
		final LayoutInflater inflater = this.getActivity().getLayoutInflater();
		final View view = inflater.inflate(R.layout.dialog_add_grade, null);
		
		// Get best and worse grade from preferences
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
		final int minGrade = Integer.valueOf(preferences.getString("min_grade_setting", "6"));
		final int maxGrade = Integer.valueOf(preferences.getString("max_grade_setting", "1"));
		
		this.addCheckboxListener(view);
		
		// Initialize dropdown for subjects and select right subject
		this.spinSubject = (Spinner) view.findViewById(R.id.spinSubject);
		spinSubject.setAdapter(new ArrayAdapter<Subject>(this.getActivity(), android.R.layout.simple_spinner_item, GradePlanner.getInstance(this.getActivity()).getSubjects()) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				final TextView row;
				
				if (convertView == null) {
					LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Service.LAYOUT_INFLATER_SERVICE);
					row = (TextView) inflater.inflate(android.R.layout.simple_spinner_item, null);
				} else {
					row = (TextView) convertView;
				}
				
				row.setText(this.getItem(position).getName());
				
				return row;
			}

			@Override
			public View getDropDownView(int position, View convertView, ViewGroup parent) {
				final CheckedTextView row;
				
				if (convertView == null) {
					LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Service.LAYOUT_INFLATER_SERVICE);
					row = (CheckedTextView) inflater.inflate(android.R.layout.simple_spinner_dropdown_item, null);
				} else {
					row = (CheckedTextView) convertView;
				}
				
				row.setText(this.getItem(position).getName());
				row.setHeight(72);
				
				return row;
			}
		});
		spinSubject.setSelection(this.subjectIndex);
	
		// Numberpicker widget for Honeycomb and later, Dropdown for older versions
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Initialize grade picker
			final NumberPicker npGrade = (NumberPicker) view.findViewById(R.id.npGrade);
			npGrade.setMinValue(Math.min(minGrade, maxGrade));
			npGrade.setMaxValue(Math.max(minGrade, maxGrade));
			npGrade.setWrapSelectorWheel(false);
			npGrade.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
			
			// Initialize weight picker
			final NumberPicker npWeight = (NumberPicker) view.findViewById(R.id.npWeight);
			npWeight.setMinValue(1);
			npWeight.setMaxValue(3);
			npWeight.setWrapSelectorWheel(false);
			npWeight.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
			
			// If we are in creation mode and just need to add the grade after clicking the positive button
			if (this.grade == null) {
				npGrade.setValue((minGrade + maxGrade) / 2);
				
				builder.setPositiveButton(R.string.button_add, new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						createOrUpdateGrade(null, npGrade.getValue(), npWeight.getValue());
						
						onPositiveClick();
					}
				});
			} 
			// Else we are in edit mode and need to set default values and update the grade after clicking
			// the positive button
			else {
				npGrade.setValue(this.grade.getValue());
				npWeight.setValue(this.grade.getWeight());
				this.spinSubject.setEnabled(false);
				
				builder.setPositiveButton(R.string.button_save, new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						createOrUpdateGrade(grade, npGrade.getValue(), npWeight.getValue());
						
						onPositiveClick();
					}
				});
			}
		} 
		// Versions older than Honeycomb
		else {
			// Create array of grade values for grade dropdown and determine minimum and maximum value
			int minGradeValue = Math.min(minGrade, maxGrade);
			final int maxGradeValue = Math.max(minGrade, maxGrade);
			final Integer[] gradeValues = new Integer[maxGradeValue - minGradeValue + 1];
			for (int i = 0; minGradeValue <= maxGradeValue; ++i, ++minGradeValue) {
				gradeValues[i] = minGradeValue;
			}
			
			// Initialize grade dropdown
			final Spinner spinGrade = (Spinner) view.findViewById(R.id.spinGrade);
			spinGrade.setAdapter(new ArrayAdapter<Integer>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, gradeValues) {
				public View getView(int position, View convertView, ViewGroup parent) {
					final TextView row;
					
					if (convertView == null) {
						LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Service.LAYOUT_INFLATER_SERVICE);
						row = (TextView) inflater.inflate(android.R.layout.simple_spinner_item, null);
					} else {
						row = (TextView) convertView;
					}
					
					row.setText(String.valueOf(this.getItem(position)));
					
					return row;
				}
			});
			
			// Inizialize weight dropdown
			final Spinner spinWeight = (Spinner) view.findViewById(R.id.spinWeight);
			spinWeight.setAdapter(new ArrayAdapter<Integer>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, new Integer[] {1, 2, 3}) {
				public View getView(int position, View convertView, ViewGroup parent) {
					final TextView row;
					
					if (convertView == null) {
						LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Service.LAYOUT_INFLATER_SERVICE);
						row = (TextView) inflater.inflate(android.R.layout.simple_spinner_item, null);
					} else {
						row = (TextView) convertView;
					}
					
					row.setText(String.valueOf(this.getItem(position)));
					
					return row;
				}
			});
			
			if (this.grade == null) {
				spinGrade.setSelection((minGrade + maxGrade - 1) / 2);
				
				builder.setPositiveButton(R.string.button_add, new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						createOrUpdateGrade(null, (Integer) spinGrade.getSelectedItem(), (Integer) spinWeight.getSelectedItem());
											
						onPositiveClick();
					}
				});
			} else {
				spinGrade.setSelection(this.grade.getValue() - 1);
				spinWeight.setSelection(this.grade.getWeight() - 1);
				
				builder.setPositiveButton(R.string.button_save, new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						createOrUpdateGrade(grade, (Integer) spinGrade.getSelectedItem(), (Integer) spinWeight.getSelectedItem());
											
						onPositiveClick();
					}
				});
			}
		}
		
		builder.setView(view);
		builder.setTitle(R.string.title_dialog_add_grade);
		builder.setNegativeButton(R.string.button_abort, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		
		return builder.create();
	}
	
	/**
	 * Adds a grade or updates it.
	 * @param grade Grade, if it already exists. null if a new grade has to be created.
	 * @param value Value of the grade.
	 * @param weight Weight of the grade.
	 */
	private void createOrUpdateGrade(Grade grade, int value, int weight) {
		final CheckBox partGrade = (CheckBox) this.getActivity().getLayoutInflater().inflate(R.layout.dialog_add_grade, null).findViewById(R.id.chkPartGrade);
		
		if (grade == null) {
			if (partGrade.isChecked()) {
				GradePlanner.getInstance(this.getActivity()).addGrade(this.subjectIndex, value, 1 / weight);
			} else {
				GradePlanner.getInstance(this.getActivity()).addGrade(this.subjectIndex, value, weight);				
			}
		} else {
			grade.setValue(value);
			
			if (partGrade.isChecked()) {
				grade.setWeight(1 / weight);
			} else {
				grade.setWeight(weight);
			}
			
			GradePlanner.getInstance(this.getActivity()).updateGrade(grade);
		}
		
		this.subjectIndex = this.spinSubject.getSelectedItemPosition();
	}
	
	private void addCheckboxListener(View layout) {
		final NumberPicker npWeight = (NumberPicker) layout.findViewById(R.id.npWeight);
		final CheckBox partGrade = (CheckBox) layout.findViewById(R.id.chkPartGrade);
		
		partGrade.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					npWeight.setMinValue(2);
					npWeight.setMaxValue(5);
					npWeight.setValue(2);
					npWeight.setFormatter(new Formatter() {
						public String format(int value) {
							return "1/" + String.valueOf(value);
						}
					});
				} else {					
					npWeight.setMinValue(1);
					npWeight.setMaxValue(3);
					npWeight.setFormatter(null);
				}
			}
		});
	}
	
	protected void onPositiveClick() {}
}
