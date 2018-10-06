package org.app.chaincode.invocation;

import java.util.Random;

import org.apache.log4j.Logger;
import org.app.util.InvokeHelper;
import org.app.util.Timer;

public class InvokeInsertChaincodeForSig {

	private Logger log = Logger.getLogger(InvokeInsertChaincodeForSig.class.getClass());
	private Timer insertTimer = new Timer();
//	private InvokeHelper invokeHelper = new InvokeHelper();
	private final static int txAmount = 40;//number of blocks(Txs) to be generated
	private final static int chainLength = 200;
	public static int[] txPositionMarkArray = new int[txAmount];
	public static int[] blockPositionBooleanArray = new int[chainLength];
	
	public static void main(String[] args) {
		InvokeHelper.init();
		generateRandomPosition();
		
		
		//init the 200 blocks chain
		for(int count=0; count<chainLength; count++) {
			if(blockPositionBooleanArray[count] == 1) {
				//generate fixed amount of data
				
			}else {
				//generate random data
				
			}
		}
	
	}
	
	public static void generateRandomPosition() {
		Random random = new Random();
		int count = 0;
		for(int i=0; i<txAmount; i++) {
			while(true) {
				int randPos = random.nextInt(200);
				if(randPos<=200 && randPos>=0 && blockPositionBooleanArray[randPos]!=1) {
					txPositionMarkArray[count++] = randPos;
					blockPositionBooleanArray[randPos] = 1;
					break;
				}
			}
		}//for
		
	}

}
