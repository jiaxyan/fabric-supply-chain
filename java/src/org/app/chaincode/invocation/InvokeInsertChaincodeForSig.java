package org.app.chaincode.invocation;

import java.util.Random;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.app.network.CreateChannel;
import org.app.network.DeployInstantiateChaincode;
import org.app.util.InvokeHelper;
import org.app.util.Timer;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

public class InvokeInsertChaincodeForSig {

	private static Logger log = Logger.getLogger(InvokeInsertChaincodeForSig.class.getClass());
	private static Timer insertTimer = null;
//	private InvokeHelper invokeHelper = new InvokeHelper();
	private final static int txAmount = 40;//number of blocks(Txs) to be generated
	private final static int chainLength = 200;
	public static int[] txPositionMarkArray = new int[txAmount];
	public static int[] blockPositionBooleanArray = new int[chainLength];
	public static int dataSize = 1*1024;// 1KB, 10KB, 100KB, 1MB, 10MB
	
	public static void main(String[] args) throws InterruptedException {
		
		/*
		 * Some basic config for InvokeInsertChaincodeForSig
		 */
		log.setLevel(Level.INFO);
		//Need to be modified on linux
		String jsonFilePath = "/Users/jiaxyan/workspace/blockchain-application-using-fabric-java-sdk-master/results/TimeOf40In200blocks.json";
		insertTimer = new Timer(jsonFilePath);
		InvokeHelper.init();
		generateRandomPosition();
		
		/*
		 * create channel "mychannel"
		 * maybe later try to create mutiply channels
		 */
//		CreateChannel.initialChannel();
//		Thread.sleep(1000);
		
		/**
		 * deploy chaincode
		 */
//		DeployInstantiateChaincode.deploy();
//		Thread.sleep(1000);
		
		//init 200 blocks(including data) in the channel
		for(int count=0; count<chainLength; count++) {
			try {
				if(blockPositionBooleanArray[count] == 1) {
					//generate fixed amount of data
					byte[] fixedAmountData = InvokeHelper.getFixedAmountRandomBytes(txAmount);
					
					InvokeHelper.putToLedger("txkey_"+count, fixedAmountData);
					
				}else {
					//generate random data
					byte[] fixedAmountData = InvokeHelper.getFixedAmountRandomBytes(txAmount);
					
					InvokeHelper.putToLedger("txkey_"+count, fixedAmountData);
				}
			} catch (ProposalException | InvalidArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Thread.sleep(2000);
System.out.print("-");
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
