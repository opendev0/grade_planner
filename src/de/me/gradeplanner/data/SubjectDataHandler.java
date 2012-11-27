package de.me.gradeplanner.data;

import java.util.ArrayList;
import de.me.gradeplanner.business.Grade;
import de.me.gradeplanner.business.Subject;

public interface SubjectDataHandler {
	ArrayList<Subject> getAll();
	
	void insertSubject(Subject subject);
	void updateSubject(Subject subject);
	void deleteSubject(Subject subject);
	
	void insertGrade(Subject subject, Grade grade);
	void updateGrade(Grade grade);
	void deleteGrade(Grade grade);
}
