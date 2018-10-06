package org.app.util;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.google.common.base.Stopwatch;

public class Timer {
	private Stopwatch time = null;
	private JSONObject timeRecoder = null;
	private Logger log = Logger.getLogger(Timer.class.getClass());
	private String filePath = null;
	
	public Timer(String filePath) {
		log.setLevel(Level.INFO);
		this.timeRecoder = new JSONObject();
		this.filePath = filePath;
	}
	
	public void startTiming() {
		time = Stopwatch.createStarted();
	}
	
	public void stopTiming() {
		time.stop();
	}
	
	public long getElapsedMicroSeconds() {
		return time.elapsed(TimeUnit.MICROSECONDS);
	}
	
	public void recordToJsonFile(String jsonKeyString) {
		timeRecoder.put(jsonKeyString, getElapsedMicroSeconds());
		log.info("JosnFile has recorded one record.");
	}
	
	public void saveToFile() {
		
		log.info("JsonFile has been saved to file:" + filePath);
	}
}
