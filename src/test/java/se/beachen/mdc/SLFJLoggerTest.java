package se.beachen.mdc;

import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * @author Anders Strand
 */
public class SLFJLoggerTest {

	@Test
	public void testParallellExecution() {

		// Given

		// When
		SLFJLogging.executeInParallell();

		// Then
	}

	@Test
	public void test() throws InterruptedException, ExecutionException, TimeoutException {

		SLFJLogging.executor();
	}
}