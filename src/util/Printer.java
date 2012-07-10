package util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Printer {
	
	BufferedWriter out;

	public Printer(String fileName) {

		FileWriter fstream;
		try {
			fstream = new FileWriter(fileName + ".txt");
			out = new BufferedWriter(fstream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public void println(String string) {
		
		try {
			out.write(string);
			out.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
