package jssf.log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class TCTable {
	
	private ArrayList<String> fKeys = new ArrayList<String>();
	
	private ArrayList<HashMap<String,Number>> fTable = new ArrayList<HashMap<String,Number>>();
	
	public TCTable() {
	}
	
	public void putData(int index, String key, Number x) {
		resize(index + 1);
		if (!fKeys.contains(key)) {
			fKeys.add(key);
		}
		fTable.get(index).put(key, x);
	}
	
	public Number getData(int index, String key) {
		if (index >= fTable.size()) {
			throw new ArrayIndexOutOfBoundsException();
		}
		return fTable.get(index).get(key);
	}
	
	private void resize(int size) {
		while (fTable.size() < size) {
			fTable.add(new HashMap<String,Number>());
		}
	}
	
	public void writeTo(String filename) throws IOException {
		PrintWriter pw = new PrintWriter(filename);
		pw.print(toString());
		pw.close();
	}
	
	public void readFrom(String filename) throws IOException {
		fKeys.clear();
		fTable.clear();
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String[] keys = br.readLine().split(",");
		for (String key: keys) {
			fKeys.add(key);
		}
		String line = br.readLine();
		while (line != null) {
			HashMap<String,Number> data = new HashMap<String,Number>();
			String[] elements = line.split(",");
			if (elements.length > 0) {
				for (int i = 0; i < elements.length; ++i) {
					if (!elements[i].isEmpty()) {
						data.put(fKeys.get(i), Double.valueOf(elements[i]));
					}
				}
			}
			fTable.add(data);
			line = br.readLine();
		}
		br.close();
	}
	
	
	public String toString() {
		String str = "";
		for (int i = 0; i < fKeys.size(); ++i) {
			str += fKeys.get(i);
			if (i < fKeys.size() - 1) {
				str +=",";
			} else {
				str += "\n";
			}
		}
		for (HashMap<String, Number> line: fTable) {
			for (int i = 0; i < fKeys.size(); ++i) {
				String key = fKeys.get(i);
				Number x = line.get(key);
				if (x != null) {
					str += x;
				}
				if (i < fKeys.size() - 1) {
					str +=",";
				} else {
					str += "\n";
				}				
			}
		}
		return str;
	}
	
	public static void main(String[] args) throws IOException {
		TCTable log = new TCTable();		
		log.putData(3, "generation", 1);
		//log.putData(2, "value1", 10.0);
		log.writeTo("test.csv");
		TCTable log2 = new TCTable();
		log2.readFrom("test.csv");
		System.out.println(log2);
	}

}
