package org.app.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.google.common.base.Stopwatch;

public class Timer {
	private Stopwatch time = null;
	private JSONObject timeRecoder = null;
	private Logger log = Logger.getLogger(Timer.class.getClass());
	
	private File jsonFile = null;
	
	public Timer(String filePath) {
		log.setLevel(Level.INFO);
		jsonFile = new File(filePath);
		if(jsonFile.exists()) {
			String jsonString = readJsonFileToString();
			this.timeRecoder = new JSONObject(jsonString);
		}else {
			try {
				jsonFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.timeRecoder = new JSONObject();
		}
	}
	
	public void startTiming() {
		log.info("Timer begin...");
		time = Stopwatch.createStarted();
	}
	
	public void stopTiming() {
		time.stop();
		log.info("Timer finished...");
	}
	
	public long getElapsedMicroSeconds() {
		return time.elapsed(TimeUnit.MICROSECONDS);
	}
	
	/*
	 * record elapsedtime to json with 'jsonKeyString'
	 * 记录间隔时间到json中
	 */
	public void recordElapsedTimeToJsonFile(String jsonKeyString) {
		timeRecoder.put(jsonKeyString, getElapsedMicroSeconds());
		log.info("JosnFile has recorded one record.");
	}
	
	/*
	 * save jsonObject to local file
	 * 把json存到文件中
	 */
	public void saveToFile() {
		FileWriter fw;
		try {
			fw = new FileWriter(jsonFile.getAbsolutePath());
			PrintWriter out = new PrintWriter(fw);
			out.write(timeRecoder.toString());
			out.println();
			fw.close();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		log.info("JsonFile has been saved to file:\n\t" + jsonFile.getAbsolutePath());
	}
	
	private String readJsonFileToString() {
        FileReader reader;
        String str = null;
		try {
			reader = new FileReader(jsonFile.getAbsolutePath());
			BufferedReader bReader = new BufferedReader(reader);
	        StringBuilder sb = new StringBuilder();
	        String s = "";
	        while ((s = bReader.readLine()) != null) {
	            sb.append(s + "\n");
	        }
	        bReader.close();
	        str = sb.toString();
		} catch ( IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("Successfully read json from file:\n\t"+jsonFile.getAbsolutePath());
        return str;
    }
	
/*	public static void main(String[] args) {
		String s = readJsonFileToString("../results/TimeOf40In200blocks.json");
		System.out.println(s);
		JSONObject jo = new JSONObject(s);
		jo.put("key", "999");
		FileWriter fw;
		try {
			fw = new FileWriter("../results/TimeOf40In200blocks.json");
			PrintWriter out = new PrintWriter(fw);
			out.write(jo.toString());
			out.println();
			fw.close();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}*/

}
