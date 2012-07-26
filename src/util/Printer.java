package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Printer {
	
	BufferedWriter out;

	public Printer(String fileName) {

		FileWriter fstream;
		try {
			
			fstream = new FileWriter(fileName + ".txt", true);
			out = new BufferedWriter(fstream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Printer(File file) throws IOException {
		
		FileWriter fstream = new FileWriter(file, true);
		out = new BufferedWriter(fstream);
	}

	public void println(String string) {
		
		try {
			out.write(string);
			out.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
