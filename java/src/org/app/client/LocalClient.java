package org.app.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class LocalClient {
	private static int datasize = 1 * 1024+8;
	private static int txAmount = 40;
	
	
	public static void main(String[] args) {
		Socket socket = null;
		InetSocketAddress address = null;
		try {
            socket = new Socket();
            address = new InetSocketAddress("127.0.0.1", 9999);
            socket.connect(address, 1000);
            
            DataInputStream input = new DataInputStream(socket.getInputStream());
            byte[] receivedByteArray = new byte[datasize];
            for(int i=0; i<txAmount; i++) {
            		input.readFully(receivedByteArray);
            		System.out.println("received bytes length:"+receivedByteArray.length);
            }
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
        		try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		
        }
	}
}
