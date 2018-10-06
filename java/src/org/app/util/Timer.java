package org.app.util;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.google.common.base.Stopwatch;

public class Timer {
	private Stopwatch time = null;
	private JSONObject timeRecoder = null;
	private Logger log = Logger.getLogger(Timer.class.getClass());
	
	public Timer() {
		timeRecoder = new JSONObject();
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
}
