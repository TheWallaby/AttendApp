package Database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class attendancePeriodCSV {
	File file;
	ArrayList<String> heds = new ArrayList<String>();
	String[] period;
	ArrayList<String[]> data = new ArrayList<String[]>();
	long endTime;

	public attendancePeriodCSV(String fileNameWithPath, int time) {
		endTime = time;

		file = new File(fileNameWithPath);
		heds = new ArrayList<String>();

		Scanner scan;
		try {
			scan = new Scanner(file);
		} catch (FileNotFoundException e) {
			return;
		}
		String[] headers = scan.nextLine().split(",");
		
		for(String str : headers) {
			heds.add(str);
		}
		
		while(scan.hasNextLine()) {
			data.add(scan.nextLine().split(","));
		}
		scan.close();
	}
	
	void beginAttendancePeriod() {

		//86400 There are this many seconds in 1 day.
		endTime = endTime + (new Date().getTime())/1000;
		if(endTime > 86400) endTime = endTime % 86400;

		period = new String[heds.size()];
		for(int i = 0; i < period.length; i++) {
			period[i] = "0";
		}
		
		period[0] = Integer.toString((data.size()));
	}
	
	boolean endAttendancePeriod() throws IOException {
		if(!isFinished()) return false;

		data.add(period);
		FileWriter writer = new FileWriter(file);
		writer.write(toString());
		writer.close();
		return true;
	}
	
	void cancelAttendancePeriod() throws IOException {
		FileWriter writer = new FileWriter(file);
		writer.write(toString());
		writer.close();
	}
	
	boolean submitAttendance(String name) {
		int index = heds.indexOf(name);
		if (index == -1) return false; //error
		
		period[index] = "1";
		return true;
	}
	
	void write() throws IOException {
		FileWriter writer = new FileWriter(file);
		writer.write(toString());
		writer.close();
	}
	
	public String toString() {
		StringBuilder toReturn = new StringBuilder("Date,");
		
		for(String str : heds) {
			toReturn.append(str + ",");
		}
		toReturn.deleteCharAt(toReturn.length()-1);
		toReturn.append("\n");
		
		for(String[] list : data) {
			for(String str : list) {
				toReturn.append(str.replaceAll(" +", " ").trim() +  ",");
			}
			toReturn.deleteCharAt(toReturn.length()-1);
			toReturn.append("\n");
		}
		return toReturn.toString();
		//Date,Name1,Name2,Name3,...Name?\n (\n means new line, there is no ',' after the final entry in that line
		//1,(0 or 1),(0 or 1),(0 or 1)...\n (Again, \n is a new line. There is no new line after the final entry in the file
	}

	boolean isFinished() {
		return (endTime <= (new Date().getTime())/1000);
	}

	public boolean exists() {
		return file.exists();
	}
}