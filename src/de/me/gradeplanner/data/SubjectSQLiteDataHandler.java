package de.me.gradeplanner.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import de.me.gradeplanner.business.Grade;
import de.me.gradeplanner.business.Subject;

public class SubjectSQLiteDataHandler extends SQLiteOpenHelper implements SubjectDataHandler {
	private static final String DB_NAME = "grade_planner";
	private static final int DB_VERSION = 2;
	private final Map<Subject, Long> subjects = new HashMap<Subject, Long>();
	private final Map<Grade, Long> grades = new HashMap<Grade, Long>();
	
	
	public SubjectSQLiteDataHandler(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE subject (id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(1023) NOT NULL, teacher VARCHAR(1023))");
		db.execSQL("CREATE TABLE grade (id INTEGER PRIMARY KEY AUTOINCREMENT, id_subject INTEGER NOT NULL, value INTEGER NOT NULL, weight REAL NOT NULL)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {}
	
	public ArrayList<Subject> getAll() {
		final ArrayList<Subject> subjects = new ArrayList<Subject>();
		final Cursor cur = this.getReadableDatabase().rawQuery("SELECT s.id, s.name, s.teacher, g.id, g.value, g.weight FROM subject s LEFT JOIN grade g ON s.id=g.id_subject ORDER BY s.id", null);
		
		long lastId = 0;
		Subject curSubject = null;
		while (cur.moveToNext()) {
			final long subjectId = cur.getLong(0);
			
			if (subjectId != lastId) {
				// Update last id and current subject
				lastId = subjectId;
				curSubject = new Subject(cur.getString(1), cur.getString(2));
				
				// Add current subject to subject id array and array which will be returned
				this.subjects.put(curSubject, subjectId);
				subjects.add(curSubject);
			}
			
			final long gradeId = cur.getLong(3);
			
			if (gradeId > 0) {
				final Grade grade = new Grade(cur.getInt(4), cur.getInt(5));
				this.grades.put(grade, gradeId);
				curSubject.addGrade(grade);
			}
		}
		
		cur.close();
		
		return subjects;
	}

	public void insertSubject(Subject subject) {
		final String teacher = subject.getTeacher();
		
		ContentValues cv = new ContentValues();
		cv.put("name", subject.getName());
		cv.put("teacher", teacher);
		
		long subjectId = this.getWritableDatabase().insert("subject", null, cv);
		this.subjects.put(subject, subjectId);
	}
	
	public void updateSubject(Subject subject) {
		ContentValues values = new ContentValues();
		values.put("name", subject.getName());
		values.put("teacher", subject.getTeacher());
		
		this.getWritableDatabase().update("subject", values, "id=" + this.subjects.get(subject).toString(), null);
	}
	
	public void deleteSubject(Subject subject) {
		this.getWritableDatabase().execSQL("DELETE FROM grade WHERE id_subject=" + this.subjects.get(subject));
		this.getWritableDatabase().execSQL("DELETE FROM subject WHERE id=" + this.subjects.get(subject));
		this.subjects.remove(subject);
	}
	
	public void insertGrade(Subject subject, Grade grade) {
		// Prepare values to insert
		ContentValues cv = new ContentValues();
		cv.put("id_subject", this.subjects.get(subject));
		cv.put("value", grade.getValue());
		cv.put("weight", grade.getWeight());
		
		// Insert values into database and associate grade with database id 
		this.grades.put(grade, this.getWritableDatabase().insert("grade", null, cv));
	}
	
	public void updateGrade(Grade grade) {
		ContentValues cv = new ContentValues();
		cv.put("value", grade.getValue());
		cv.put("weight", grade.getWeight());
		
		this.getWritableDatabase().update("grade", cv, "id=" + this.grades.get(grade), null);
	}
	
	public void deleteGrade(Grade grade) {
		this.getWritableDatabase().execSQL("DELETE FROM grade WHERE id=" + this.grades.get(grade));
		this.grades.remove(grade);
	}
}
