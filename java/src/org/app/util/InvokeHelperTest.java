package org.app.util;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.app.config.Config;
import org.junit.Test;

public class InvokeHelperTest {

	@Test
	public void testGetFixedAmountRandomData() {
		//fail("Not yet implemented");
		InvokeHelper ih = new InvokeHelper();
		byte[] array = ih.getFixedAmountRandomBytes(100);
		System.out.println(array.length);
	}

}
