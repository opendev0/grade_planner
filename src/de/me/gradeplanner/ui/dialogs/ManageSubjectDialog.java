package de.me.gradeplanner.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import de.me.gradeplanner.R;
import de.me.gradeplanner.business.GradePlanner;
import de.me.gradeplanner.business.Subject;

public class ManageSubjectDialog extends DialogFragment {
	protected Subject subject;
	
	
	public ManageSubjectDialog() {}
	
	public ManageSubjectDialog(Subject subject) {
		this.subject = subject;
	}	
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
		final LayoutInflater inflater = this.getActivity().getLayoutInflater();
		final View view = inflater.inflate(R.layout.dialog_add_subject, null);
		final EditText txtSubjectName = (EditText) view.findViewById(R.id.input_subject);
		
		builder.setView(view);
		
		if (this.subject == null) {
			builder.setTitle(R.string.title_dialog_add_subject);
			builder.setPositiveButton(R.string.button_add, new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					final EditText txtTeacher = (EditText) view.findViewById(R.id.input_teacher);
					GradePlanner.getInstance(getActivity()).addSubject(txtSubjectName.getText().toString(), txtTeacher.getText().toString());
				}
			});
		} else {
			builder.setTitle(R.string.title_dialog_update_subject);
			builder.setPositiveButton(R.string.button_save, new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					final EditText txtTeacher = (EditText) view.findViewById(R.id.input_teacher);
					
					subject.setName(txtSubjectName.getText().toString());
					subject.setTeacher(txtTeacher.getText().toString());
					
					GradePlanner.getInstance(getActivity()).updateSubject(subject);
				}
			});
			
			final EditText txtTeacher = (EditText) view.findViewById(R.id.input_teacher);
			
			txtSubjectName.setText(subject.getName());
			txtTeacher.setText(subject.getTeacher());
		}
		builder.setNegativeButton(R.string.button_abort, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		
		final AlertDialog dialog = builder.create();
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

		txtSubjectName.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				if (s.length() == 0) {
					dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
				} else {
					dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(true);
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
		});
		
		if (this.subject == null) {
			dialog.show();
			dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
		}
		
		return dialog;
	}
	
	protected void onPositiveClick() {
		
	}
}
