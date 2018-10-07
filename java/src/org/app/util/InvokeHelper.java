package org.app.util;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.app.chaincode.invocation.InvokeInsertChaincodeForSig;
import org.app.client.CAClient;
import org.app.client.ChannelClient;
import org.app.client.FabricClient;
import org.app.config.Config;
import org.app.user.UserContext;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.EventHub;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;


public class InvokeHelper {
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
	

	private Logger log = Logger.getLogger(InvokeHelper.class.getClass());
	
	
	public static byte[] getFixedAmountRandomBytes(int amount) {
		byte[] result = new byte[amount];
		Random r = new Random();
		r.nextBytes(result);
		return result;
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
	
	
	
	public static Collection<ProposalResponse> putToLedger(String key, byte[] value
			/*ChannelClient channelClient*/) throws ProposalException, InvalidArgumentException {
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
	
	
	public static byte[] getFromLedger(String key 
			/*ChannelClient channelClient*/) throws InvalidArgumentException, ProposalException, SupplyChainException {
		byte[][] args = {key.getBytes()};
		Collection<ProposalResponse>  responsesQuery = channelClient.queryByChainCode(Config.CHAINCODE_1_NAME, "querySig", args);
		if(responsesQuery.size()!=1)
			throw new SupplyChainException("返回的结果长度不为1");
		Iterator<ProposalResponse> it = responsesQuery.iterator();
		return it.next().getChaincodeActionResponsePayload();
	}
	
	/*public byte[] getFixedAmountRandomData(int amount) {
		File tempFile = new File(Config.TEMP_FILE_PATH);
		RandomAccessFile r = null;
		byte[] randomData = null;
		
        try {
            if(tempFile.exists()) tempFile.delete();
        		tempFile.createNewFile();
            r = new RandomAccessFile(tempFile, "rw");  
            r.setLength(amount);
        } catch (IOException e) {
			e.printStackTrace();
		} finally{
            if (r != null) {
                try {
                		randomData = readFileToByteArray(tempFile, amount);
                    r.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            tempFile.delete();
        }  
		return randomData;
	}
	
	public byte[] readFileToByteArray(File file, int length) {
		try {
			InputStream in = new FileInputStream(file.getAbsolutePath());
			ByteArrayOutputStream out = new ByteArrayOutputStream();
		    byte[] buffer = new byte[length];
		    int n = 0;
		    while ((n = in.read(buffer)) != -1) {
		        out.write(buffer, 0, n);
		    }
			return out.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		log.error("[ERROR] Class InvokeHelper->readFileToByteArray failed.");
		return null;
	}*/
}
