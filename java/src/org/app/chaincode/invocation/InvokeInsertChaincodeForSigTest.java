package org.app.chaincode.invocation;

import static org.junit.Assert.*;

import org.junit.Test;

public class InvokeInsertChaincodeForSigTest {

	@Test
	public void testGenerateRandomPosition() {
		InvokeInsertChaincodeForSig.generateRandomPosition();
		int i = 0;
		while(i<40) {
			System.out.print(InvokeInsertChaincodeForSig.txPositionMarkArray[i]+",");
			i++;
		}
		System.out.println("...");
		i = 0;
		while(i<200) {
			System.out.print(InvokeInsertChaincodeForSig.blockPositionBooleanArray[i]+",");
			i++;
		}
	}

}
