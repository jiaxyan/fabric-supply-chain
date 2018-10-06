package org.app.config;

import java.io.File;

public class Config {
	public static final String TEMP_FILE_PATH = "../results/temp.txt";
	
	public static final String ORG1_MSP = "Org1MSP";

	public static final String ORG1 = "org1";

	public static final String ORG2_MSP = "Org2MSP";

	public static final String ORG2 = "org2";

	public static final String ADMIN = "admin";

	public static final String ADMIN_PASSWORD = "adminpw";

	public static final String CHANNEL_CONFIG_PATH = "config/channel.tx";
	public static final String CHANNEL1_CONFIG_PATH = "../network_resources/config/channel1.tx";
	
	//组织1的基目录 crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp
	public static final String ORG1_USR_BASE_PATH = "crypto-config" + File.separator + "peerOrganizations" + File.separator
			+ "org1.example.com" + File.separator + "users" + File.separator + "Admin@org1.example.com"
			+ File.separator + "msp";

	//组织1的基目录 crypto-config/peerOrganizations/org2.example.com/users/Admin@org2.example.com/msp
	public static final String ORG2_USR_BASE_PATH = "crypto-config" + File.separator + "peerOrganizations" + File.separator
			+ "org2.example.com" + File.separator + "users" + File.separator + "Admin@org2.example.com"
			+ File.separator + "msp";
	
	public static final String ORG1_USR_ADMIN_PK = ORG1_USR_BASE_PATH + File.separator + "keystore";
	public static final String ORG1_USR_ADMIN_CERT = ORG1_USR_BASE_PATH + File.separator + "admincerts";

	public static final String ORG2_USR_ADMIN_PK = ORG2_USR_BASE_PATH + File.separator + "keystore";
	public static final String ORG2_USR_ADMIN_CERT = ORG2_USR_BASE_PATH + File.separator + "admincerts";
	
	public static final String CA_ORG1_URL = "http://localhost:7054";
	
	public static final String CA_ORG2_URL = "http://localhost:8054";
	
	public static final String ORDERER_URL = "grpc://localhost:7050";
	
	public static final String ORDERER_NAME = "orderer.example.com";

	public static final String CHANNEL_NAME = "mychannel";
	public static final String CHANNEL1_NAME = "mychannel1";
	
	public static final String[] CHANNELS_NAME = {"mychannel1", "mychannel2", "mychannel3", "mychannel4", "mychannel5"};

	public static final String[] CHANNELS_CONFIG_PATH = {"/Users/jiaxyan/workspace/blockchain-application-using-fabric-java-sdk-master/network_resources/config/channel1.tx", "../config/channel2.tx", "../config/channel3.tx", "config/channel4.tx", "config/channel5.tx"};
	
	public static final String ORG1_PEER_0 = "peer0.org1.example.com";
	
	public static final String ORG1_PEER_0_URL = "grpc://localhost:7051";
	
	public static final String ORG1_PEER_1 = "peer1.org1.example.com";
	
	public static final String ORG1_PEER_1_URL = "grpc://localhost:7056";
	
    public static final String ORG2_PEER_0 = "peer0.org2.example.com";
	
	public static final String ORG2_PEER_0_URL = "grpc://localhost:8051";
	
	public static final String ORG2_PEER_1 = "peer1.org2.example.com";
	
	public static final String ORG2_PEER_1_URL = "grpc://localhost:8056";
	
	public static final String CHAINCODE_ROOT_DIR = "chaincode";
	
	public static final String CHAINCODE_1_NAME = "fabcar";//fabcar
	
	public static final String CHAINCODE_1_PATH = "github.com/fabcar";//"github.com/fabcar"
	
	public static final String CHAINCODE_1_VERSION = "1";


}
