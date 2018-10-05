/****************************************************** 
 *  Copyright 2018 IBM Corporation 
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 *  you may not use this file except in compliance with the License. 
 *  You may obtain a copy of the License at 
 *  http://www.apache.org/licenses/LICENSE-2.0 
 *  Unless required by applicable law or agreed to in writing, software 
 *  distributed under the License is distributed on an "AS IS" BASIS, 
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 *  See the License for the specific language governing permissions and 
 *  limitations under the License.
 */ 
package org.app.chaincode.invocation;

import static com.google.common.base.Preconditions.checkState;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.app.client.CAClient;
import org.app.client.ChannelClient;
import org.app.client.FabricClient;
import org.app.config.Config;
import org.app.user.UserContext;
import org.app.util.Util;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.EventHub;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.json.JSONObject;

import com.gazman.bls.BlsSignatures;
import com.gazman.bls.model.Signature;
import com.google.common.base.Stopwatch;

import edu.emory.mathcs.backport.java.util.Arrays;
import it.unisa.dia.gas.jpbc.Element;

/**
 * 
 * @author Balaji Kadambi
 *
 */

public class InvokeQueryChaincodeForSig {

	private static final byte[] EXPECTED_EVENT_DATA = "!".getBytes(UTF_8);
	private static final String EXPECTED_EVENT_NAME = "event";
	
	private static String caUrl = Config.CA_ORG1_URL;
	private static CAClient caClient;
	private static UserContext adminUserContext;
	private static FabricClient fabClient ;
	private static ChannelClient channelClient;
	private static Channel channel;
	private static Peer peer;
	private static EventHub eventHub;
	private static Orderer orderer;
	private static String testString = null;
	private static int Num = 100;
//	private static TransactionProposalRequest request;
//	private static Collection<ProposalResponse> response;

	
	public static void main(String args[]) throws Exception {
		JSONObject querytimeJson = new JSONObject();
		String message = null;
		Stopwatch querySigtime = null;
		byte[] signaturebyte;
		
		
		BlsSignatures blsSignatures = new BlsSignatures();
		byte[][] privateKey = new byte[Num][];
		
		for(int i=0; i<Num; i++) 
			privateKey[i] = blsSignatures.pairing.getZr().newRandomElement().toBytes();
			
		init();//初始化
		
		for(int i=0; i<Num; i++) {
			message = "Location:Place"+(i+1)+",Date:"+(i+1)+",ProduName:Drugs,ProduAmount:1KG";
			Signature signatureT = blsSignatures.sign(message.getBytes(), privateKey[i]);
			putInMessageAndSignature(message, signatureT.signature.toBytes());//写入Signature
		}
		System.out.println("\n\n\ngenerate "+Num+" sigs and upload them complete.\n\n\n");
		Thread.sleep(5000);
		
		for(int count=1; count<=5; count++) {
			System.out.println("count:"+count);
			querySigtime = Stopwatch.createStarted();
			for(int countTime=1; countTime<=20*count; countTime++) {
				message = "Location:Place"+countTime+",Date:"+countTime+",ProduName:Drugs,ProduAmount:1KG";
				signaturebyte = getSignatureAccordingMessage(message);
		        checkState(blsSignatures.validateSingleSignature( blsSignatures.pairing.getG2().newElementFromBytes(signaturebyte)) == true, "---- Signature has not passed the verification");
			}
			querySigtime.stop();
			querytimeJson.put("TimeOfQueryAndVerify_"+(count*20)+"_Sigs", querySigtime.elapsed(TimeUnit.MICROSECONDS));
		
		}


		System.out.println("分别生成20 40 60 80 100 ");
		//分别生成20 40 60 80 100 
		BlsSignatures[] blsSignatureslocal = new BlsSignatures[5];
		for(int count=1; count<=5; count++) {
			ArrayList<Signature> signatureslist = new ArrayList<>();
			blsSignatureslocal[count-1] = new BlsSignatures();
			for(int countTime=1; countTime<=20*count; countTime++) {
				message = "Location:City_"+countTime+",Date:"+countTime+",ProduName:Drugs,ProduAmount:1KG";
				byte[] privateKeylocal = blsSignatureslocal[count-1].pairing.getZr().newRandomElement().toBytes();
	            Signature signature = blsSignatureslocal[count-1].sign( message.getBytes(), privateKeylocal );
//	            System.out.println(signature.signature.toBytes().length);
	            signatureslist.add(  new Signature(signature.message, signature.publicKey, 
	            		blsSignatureslocal[count-1].pairing.getG1().newElementFromBytes(  signature.signature.toBytes())  ) );
//		        checkState(blsSignatures.validateSingleSignature( blsSignatures.pairing.getG2().newElementFromBytes(signaturebyte)) == true, "---- Signature has not passed the verification");
			}
			Element res = blsSignatureslocal[count-1].AggregateSignature(signatureslist);
	        byte[] resbyte = res.toBytes();
	        String messageAgg = "Location:City_"+count*20+",Date:"+count*20+",ProduName:Drugs,ProduAmount:1KG";
			putInMessageAndSignature(messageAgg, resbyte);//写入Signature
			
			Thread.sleep(5000);//等待五秒钟
			System.out.println("\n\n\nAggreSignature put in successfully\n\n\n");
			
			querySigtime = Stopwatch.createStarted();
//			for(int countTime=1; countTime<=1; countTime=20*count) {
//				System.out.println("count="+count+"    countTime="+countTime);
//				message = "Location:City_"+countTime+",Date:"+countTime+",ProduName:Drugs,ProduAmount:1KG";
				signaturebyte = getSignatureAccordingMessage(messageAgg);
		        checkState(blsSignatureslocal[count-1].validateAggreSignature( blsSignatureslocal[count-1].pairing.getG2().newElementFromBytes(signaturebyte)) == true, "---- Aggregate Signature has not passed the verification");
//			}
			querySigtime.stop();
			querytimeJson.put("AggreTimeOfQueryAndVerify_"+(count*20)+"_Sigs", querySigtime.elapsed(TimeUnit.MICROSECONDS));
		
		}
		
		
		
/*		for(int countTime=1; countTime<=Num; countTime++) {
			String message = "Location:Place"+countTime+",Date:"+countTime+",ProduName:Drugs,ProduAmount:1KG";
			byte[] signaturebyte = getSignatureAccordingMessage(message);
	        checkState( blsSignatures.validateSingleSignature( blsSignatures.pairing.getG2().newElementFromBytes(signaturebyte)) == true, "---- Signature has not passed the verification");
//			System.out.println(blsSignatures.validateSingleSignature( blsSignatures.pairing.getG2().newElementFromBytes(signaturebyte)));
//			System.out.println("now we see:"+new String(signaturebyte));
		}*/
		
		
		String storePath = "/Users/jiaxyan/Downloads/blockchain-application-using-fabric-java-sdk-master/results/TimeOfQueryAndVerify.json";
		System.out.println("---- storePath is"+storePath);
		FileWriter fw = new FileWriter(storePath);
		PrintWriter out = new PrintWriter(fw);
		out.write(querytimeJson.toString());
		out.println();
		fw.close();
		out.close();
	}

	
	public static void putInMessageAndSignature(String message_, byte[] ElementByteValue/*Element element_*/) throws ProposalException, InvalidArgumentException {
		
		if(testString==null)
			putToLedger(message_, ElementByteValue, channelClient);//This func has return types
		else
			putToLedger(message_, testString.getBytes(), channelClient);
		
//		System.out.println("putIn Len:"+element_.toBytes().length);
	}
	
	public static byte[] getSignatureAccordingMessage(String message_) throws InvalidArgumentException, ProposalException {
		byte[] res = getFromLedger(message_);
		System.out.println("getSig");
		int siglen = testString==null?152:testString.getBytes().length;
		byte[] sigbytes = new byte[siglen];
//		byte[] keybytes = new byte[res.length-siglen];
		System.arraycopy(res, 0, sigbytes, 0, siglen);
//		System.arraycopy(res, siglen, keybytes, 0, (res.length-siglen));
		return sigbytes;
	}
	
 	public static void init() {
		try {
	        Util.cleanUp();
			caClient = new CAClient(caUrl, null);
			// Enroll Admin to Org1MSP
			adminUserContext = new UserContext();
			adminUserContext.setName(Config.ADMIN);
			adminUserContext.setAffiliation(Config.ORG1);
			adminUserContext.setMspId(Config.ORG1_MSP);
			caClient.setAdminUserContext(adminUserContext);
			adminUserContext = caClient.enrollAdminUser(Config.ADMIN, Config.ADMIN_PASSWORD);
			
			fabClient = new FabricClient(adminUserContext);
			
			channelClient = fabClient.createChannelClient(Config.CHANNEL_NAME);
			channel = channelClient.getChannel();
			peer = fabClient.getInstance().newPeer(Config.ORG1_PEER_0, Config.ORG1_PEER_0_URL);
			eventHub = fabClient.getInstance().newEventHub("eventhub01", "grpc://localhost:7053");
			orderer = fabClient.getInstance().newOrderer(Config.ORDERER_NAME, Config.ORDERER_URL);
			channel.addPeer(peer);
			channel.addEventHub(eventHub);
			channel.addOrderer(orderer);
			channel.initialize();
	
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	
	public static Collection<ProposalResponse> putToLedger(String key, byte[] value, 
			ChannelClient channelClient) throws ProposalException, InvalidArgumentException {
		TransactionProposalRequest request = fabClient.getInstance().newTransactionProposalRequest();
		ChaincodeID ccid = ChaincodeID.newBuilder().setName(Config.CHAINCODE_1_NAME).build();
		request.setChaincodeID(ccid);
		request.setFcn("createSig");
		byte[][] arguments = {key.getBytes(), value};
		request.setArgBytes(arguments);
		request.setProposalWaitTime(1000);
		
		
		Map<String, byte[]> tm2 = new HashMap<>();
		tm2.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8)); // Just some extra junk
																							// in transient map
		tm2.put("method", "TransactionProposalRequest".getBytes(UTF_8)); // ditto
		tm2.put("result", ":)".getBytes(UTF_8)); // This should be returned see chaincode why.
		tm2.put(EXPECTED_EVENT_NAME, EXPECTED_EVENT_DATA); // This should trigger an event see chaincode why.
		request.setTransientMap(tm2);
//		Collection<ProposalResponse> responses = channelClient.sendTransactionProposal(request);
//		return responses;
		return channelClient.sendTransactionProposal(request);
	}
	
	/**
	 * 把一批 key-value 对写入ledger
	 * @param keyValue
	 * @param channelClient
	 * @return
	 * @throws ProposalException
	 * @throws InvalidArgumentException
	 */
	public static Collection<ProposalResponse> putToLedgerBatch(Map<String, String> keyValue, 
			ChannelClient channelClient) throws ProposalException, InvalidArgumentException {
		TransactionProposalRequest request = fabClient.getInstance().newTransactionProposalRequest();
		ChaincodeID ccid = ChaincodeID.newBuilder().setName(Config.CHAINCODE_1_NAME).build();
		request.setChaincodeID(ccid);
		request.setFcn("createBatchSigs");
		int argumentsLen = keyValue.size()*2;
		byte[][] arguments = new byte[argumentsLen][];
		int counter = 0;
		for(String keystring : keyValue.keySet()) {
			arguments[counter] = keystring.getBytes();
			arguments[counter+1] = keyValue.get(keystring).getBytes();
			counter+=2;
		}
		for(int i=0; i<argumentsLen; i++)
			System.out.println(new String(arguments[i]));
		System.out.println(argumentsLen);
		request.setArgBytes(arguments);
		request.setProposalWaitTime(1000);
		
		
		Map<String, byte[]> tm2 = new HashMap<>();
		tm2.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8)); // Just some extra junk
																							// in transient map
		tm2.put("method", "TransactionProposalRequest".getBytes(UTF_8)); // ditto
		tm2.put("result", ":)".getBytes(UTF_8)); // This should be returned see chaincode why.
		tm2.put(EXPECTED_EVENT_NAME, EXPECTED_EVENT_DATA); // This should trigger an event see chaincode why.
		request.setTransientMap(tm2);
//		Collection<ProposalResponse> responses = channelClient.sendTransactionProposal(request);
//		return responses;
		return channelClient.sendTransactionProposal(request);
	}
	
	
	
	public static byte[] getFromLedger(String key 
			/*ChannelClient channelClient*/) throws InvalidArgumentException, ProposalException {
		byte[][] args = {key.getBytes()};
		Collection<ProposalResponse>  responsesQuery = channelClient.queryByChainCode(Config.CHAINCODE_1_NAME, "querySig", args);
		if(responsesQuery.size()!=1)
			{System.out.println("====[ERROR] 返回的结果长度不为1====");return null;}
		Iterator<ProposalResponse> it = responsesQuery.iterator();
		return it.next().getChaincodeActionResponsePayload();
	}
}
