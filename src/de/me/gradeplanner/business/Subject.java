package de.me.gradeplanner.business;

import java.util.ArrayList;
import java.util.List;

public class Subject {
	private String name;
	private String teacher;
	private final List<Grade> grades = new ArrayList<Grade>();
	
	
	public Subject(String name, String teacher) {
		this.name = name;
		this.teacher = teacher;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getTeacher() {
		return this.teacher;
	}

	public List<Grade> getGrades() {
		return this.grades;
	}
	
	public void setName(String name) {
		if (this.name != "" && name != null) {
			this.name = name;
		}
	}
	
	public void setTeacher(String teacher) {
		this.teacher = teacher;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	public void addGrade(Grade grade) {
		this.grades.add(grade);
	}
	
	public void removeGrade(int index) {
		this.grades.remove(index);
	}

	public double getAverage() {
		int values = 0;
		int weights = 0;
		
		for (int i = 0; i < this.grades.size(); ++i) {
			Grade grade = grades.get(i);
			
			values += grade.getValue() * grade.getWeight();
			weights += grade.getWeight();
		}
		
		return Math.round((((double) values / weights) * 100)) / 100.0;
	}
	
	public double getBosAverage() {
		int values = 0;			// Sum of all weight-one grades
		int numValues = 0;		// Number of weight-one grades
		int saValues = 0;		// Sum of all weight-two grades
		int numSaValues = 0;	// Number of weight-two grades
		double average = 0;		// Average of all grades
		
		for (int i = 0; i < this.grades.size(); ++i) {
			if (this.grades.get(i).getWeight() == 1) {
				values += this.grades.get(i).getValue();
				++numValues;
			} else if (this.grades.get(i).getWeight() == 2) {
				saValues += this.grades.get(i).getValue();
				++numSaValues;
			}
		}
		
		if (numValues > 0) {
			average += (double) values / numValues;
		}
		
		if (numSaValues > 0) {
			// There is no reason to count double-weighted values twice, if there is no grade with weight one
			if (numValues == 0) {
				average += saValues / numSaValues;
			} else {
				average += ((saValues * 2) / numSaValues);
			}
		}
		
		if (numSaValues > 0 && numValues > 0) {
			average /= 3;
		}
		
		return Math.round(average * 100) / 100.0;
	}
}
