package org.app.chaincode.invocation;

import java.util.Random;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.app.util.InvokeHelper;
import org.app.util.SupplyChainException;
import org.app.util.Timer;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

import com.gazman.bls.BlsSignatures;
import com.gazman.bls.model.Signature;

public class InvokeInsertChaincodeForSig {

	private static Logger log = Logger.getLogger(InvokeInsertChaincodeForSig.class.getClass());
	private static Timer insertTimer = null;
	
	private final static int txAmount = 40;//number of blocks(Txs) to be generated
	public static int dataSize = 100*1024;// 1KB, 10KB, 100KB, 1MB, 10MB
	public static String jsonKey = "100K";
	public static String fileName = "TimeOf40In200blocks.json";//"TimeOf10KIn200blocks.json"
	public static AccessType accessType = AccessType.LOCAL; //AccessType.LOCAL Or AccessType.REMOTE 模拟轻节点/非轻节点
	
	private final static int chainLength = 200;
	public static int[] txPositionMarkArray = new int[txAmount];
	public static int[] blockPositionBooleanArray = new int[chainLength];
	public static BlsSignatures blsSignatures;
	
	
	public static void main(String[] args) throws InterruptedException {
		
		/*
		 * Some basic config for InvokeInsertChaincodeForSig
		 */
		log.setLevel(Level.INFO);
		//Need to be modified on linux
		String jsonFilePath = null;
		String os = System.getProperty("os.name");
		if(os.toLowerCase().startsWith("mac")){
			jsonFilePath = "/Users/jiaxyan/workspace/blockchain-application-using-fabric-java-sdk-master/results/"+fileName;
		}else {
			jsonFilePath = "/home/csuser/fabric-supply-chain/results/"+fileName;
		}
		insertTimer = new Timer(jsonFilePath);
		InvokeHelper.init(txAmount);
		blsSignatures = InvokeHelper.getBlsSignatures();
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
		
		
		byte[][] privateKey = null;
		try {
			privateKey = InvokeHelper.getPrivateKey();
		} catch (SupplyChainException e1) {
			e1.printStackTrace();
		}
		int prikeyIndex = 0;
		/*
		 * init 200 blocks(including data) in the channel
		 * 生成固定长度的一个通道（对于不同数据大小需要重复生成）
		 */
		for(int count=0; count<chainLength; count++) {
			try {
				if(blockPositionBooleanArray[count] == 1 && prikeyIndex < txAmount) {
					//generate fixed amount of data
					byte[] fixedAmountData = InvokeHelper.getFixedAmountRandomBytes(dataSize);
//					InvokeHelper.putToLedger("txkey_"+count, fixedAmountData);
					
					//generate corresponding signature and
					Signature signature = blsSignatures.sign(fixedAmountData, privateKey[prikeyIndex]);
					int totalLen = fixedAmountData.length + signature.signature.toBytes().length;
					byte[] dataPlusSig = new byte[totalLen];
					System.arraycopy(fixedAmountData, 0, dataPlusSig, 0, fixedAmountData.length);
					System.arraycopy(signature.signature.toBytes(), 0, dataPlusSig, fixedAmountData.length, signature.signature.toBytes().length);
					//put data and sig into channel
					InvokeHelper.putToLedger("txkey_"+count+"_plusSig", dataPlusSig);
					prikeyIndex++;
				}else {
					//generate random data
					byte[] fixedAmountData = InvokeHelper.getFixedAmountRandomBytes(dataSize);
					InvokeHelper.putToLedger("txkey_"+count, fixedAmountData);
					
				}
			} catch (ProposalException | InvalidArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Thread.sleep(2000);
			System.out.print("-");
		}
		
		
		/*
		 * read txAmount data from ledger and get Elapsed time. 
		 * 读取数据并计时
		 */
		readFromLedgerAndTime();
		
	}
	
	
	
	
	
	
	
/*	public static void readFromLedgerAndSendDataTime() {
		ServerSocket serversocket = null;
		Socket client = null;
		DataOutputStream dataOutputStream = null;
		try {
			serversocket = new ServerSocket(9999);
			log.info("Server has been setup(Listening). Please run client side to read from this server...");
            client = serversocket.accept();
            log.info("A client has been accepted.");
            dataOutputStream = new DataOutputStream(client.getOutputStream());
            byte[] tempByteArray = null;
			
			 * read txAmount data from ledger and get Elapsed time. 
			 * 读取数据并计时
			 
			insertTimer.startTiming();
			for(int i=0; i<txAmount; i++) {
				tempByteArray = InvokeHelper.getFromLedger("txkey_"+txPositionMarkArray[i]);
				System.out.println("tempByteArray length (And send):" + tempByteArray.length);
				dataOutputStream.write(tempByteArray);
				dataOutputStream.flush();
			}
			insertTimer.stopTiming();
			insertTimer.recordElapsedTimeToJsonFile(jsonKey);
			insertTimer.saveToFile();
			
		} catch (InvalidArgumentException | ProposalException | IOException | SupplyChainException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				client.close();
				serversocket.close();
				dataOutputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}*/
	
	
	public static void readFromLedgerAndTime() {
		try {
            byte[] tempByteArray = null;
			/*
			 * read txAmount data from ledger and get Elapsed time. 
			 * 读取数据并计时
			 */
			insertTimer.startTiming();
			for(int i=0; i<txAmount; i++) {
				tempByteArray = InvokeHelper.getFromLedger("txkey_"+txPositionMarkArray[i]+"_plusSig");
				System.out.println("tempByteArray length:" + tempByteArray.length);
			}
			insertTimer.stopTiming();
			insertTimer.recordElapsedTimeToJsonFile(jsonKey);
			insertTimer.saveToFile();
			
		} catch (InvalidArgumentException | ProposalException | SupplyChainException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			
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

enum AccessType{
	LOCAL,
	REMOTE
}
