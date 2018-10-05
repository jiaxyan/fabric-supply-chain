package org.app.chaincode.invocation;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		byte[] s = new byte[2];
//		s[0] = (byte) 0xf0;
		byte[] s = "Hello".getBytes();
		System.out.println(s[0]+"\t"+s[1]);
		System.out.print((s[0]&128) != 0?1:0);
		System.out.print((s[0]&64) != 0?1:0);
		System.out.print((s[0]&32) != 0?1:0);
		System.out.print((s[0]&16) != 0?1:0);
		System.out.print((s[0]&8) != 0?1:0);
		System.out.print((s[0]&4) != 0?1:0);
		System.out.print((s[0]&2) != 0?1:0);
		System.out.print((s[0]&1) != 0?1:0);


		System.out.print(" "+ ( (s[1]&128) != 0?1:0) );
		System.out.print((s[1]&64) != 0?1:0);
		System.out.print((s[1]&32) != 0?1:0);
		System.out.print((s[1]&16) != 0?1:0);
		System.out.print((s[1]&8) != 0?1:0);
		System.out.print((s[1]&4) != 0?1:0);
		System.out.print((s[1]&2) != 0?1:0);
		System.out.print((s[1]&1) != 0?1:0);
	}

}
