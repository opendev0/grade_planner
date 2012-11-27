package de.me.gradeplanner.business;

import java.util.ArrayList;
import android.content.Context;
import de.me.gradeplanner.data.SubjectSQLiteDataHandler;

public class GradePlanner {
	private static GradePlanner instance;
	private SubjectSQLiteDataHandler dataHandler;
	private ArrayList<Subject> subjects;
	
	
	private GradePlanner(Context context) {
		this.dataHandler = new SubjectSQLiteDataHandler(context);
		this.subjects = dataHandler.getAll();
	}
	
	public static GradePlanner getInstance(Context context) {
		if (instance == null) {
			instance = new GradePlanner(context);
		}
		
		return instance;
	}
	
	public SubjectSQLiteDataHandler getDataHandler() {
		return this.dataHandler;
	}
	
	public ArrayList<Subject> getSubjects() {
		return this.subjects;
	}
	
	public void addSubject(String name, String teacher) {
		Subject newSubject = new Subject(name, teacher.equals("") ? null : teacher);
		
		this.subjects.add(newSubject);
		this.dataHandler.insertSubject(newSubject);
	}
	
	public void updateSubject(Subject subject) {
		this.dataHandler.updateSubject(subject);
	}
	
	public void deleteSubject(int index) {
		this.dataHandler.deleteSubject(this.subjects.get(index));
		this.subjects.remove(index);
	}
	
	public void addGrade(int index, int value, int weight) {
		final Subject subject = this.subjects.get(index);
		final Grade grade = new Grade(value, weight);
		
		subject.addGrade(grade);
		this.dataHandler.insertGrade(subject, grade);
	}
	
	public void updateGrade(Grade grade) {
		this.dataHandler.updateGrade(grade);
	}
	
	public void deleteGrade(int subjectIndex, int gradeIndex) {
		this.dataHandler.deleteGrade(this.subjects.get(subjectIndex).getGrades().get(gradeIndex));
		this.subjects.get(subjectIndex).removeGrade(gradeIndex);
	}
}
