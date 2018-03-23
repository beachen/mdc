package se.beachen.mdc;

import ch.qos.logback.classic.LoggerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;


/**
 * @author Anders Strand
 */
public class SLFJLogging {

	private static Logger LOGGER = LoggerFactory.getLogger(SLFJLogging.class);

	private ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(5);


	static
	{
		LoggerContext ctx = (LoggerContext) LoggerFactory.getILoggerFactory();
		ctx.setName("myContext");
	}

	// TODO: How to do it in Spring/HttpServletFilter


	static void executeInParallell() {

		// Main thread

		MDC.put("correlationId", "request1");
		LOGGER.info("Hello from main thread!");

		Map<String, String> context = MDC.getCopyOfContextMap();
		context.forEach((k, v) -> LOGGER.info("key:" + k + ", val=" + v));

		IntStream.range(1,10)
			.parallel()
			.forEach(e -> {
				// For each new thread -> new context
				MDC.setContextMap(context);
				LOGGER.info("Hello from executor thread: calculated val =" + String.valueOf(e));
				MDC.setContextMap(new HashMap<>());

			});

		LOGGER.debug("Debug is black");
		LOGGER.warn("Warnings are red");
		LOGGER.error("Errors are *red*");

		RuntimeException rte = new RuntimeException("Hello, this is a runtime exception");
		LOGGER.error("This is an error with a stack trace", rte);

		MDC.remove("correlationId");
	}

	// Completable features examples:
	// https://www.callicoder.com/java-8-completablefuture-tutorial/

	// Spring: https://moelholm.com/2017/07/24/spring-4-3-using-a-taskdecorator-to-copy-mdc-data-to-async-threads/
	static void executor() throws InterruptedException, ExecutionException, TimeoutException {

		MDC.put("correlationId", "request1");
		LOGGER.info("Before spawning threads");

		// Using Lambda Expression
		CompletableFuture<String> heavyWork = heavyWork();
		CompletableFuture<String> moreHeavyWork = anotherHeavyWork();

		CompletableFuture.allOf(heavyWork, moreHeavyWork).get();



		LOGGER.info("Wait from async res:");
		LOGGER.info("Result from async:" + heavyWork.get(2, TimeUnit.SECONDS));
		LOGGER.info("Wait from async res DONE:");



	}

	private static CompletableFuture<String> anotherHeavyWork() {

		LOGGER.info("Another heavy work started!");
		return CompletableFuture
			.supplyAsync(() -> {
				try {

					LOGGER.info("Async 2");
					TimeUnit.SECONDS.sleep(2);
					LOGGER.info("Async done");
				} catch (InterruptedException e) {
					throw new IllegalStateException(e);
				}
				return "Result of the asynchronous computation"; })
			.thenApplyAsync(val -> {
				LOGGER.info("APPLY");
				return val + ", is applied ";
			});
	}

	private static CompletableFuture<String> heavyWork() {

		LOGGER.info("Heavy work started!");

		return CompletableFuture
			.supplyAsync(() -> {
				try {

					LOGGER.info("Async");
					TimeUnit.SECONDS.sleep(1);
					LOGGER.info("Async done");
				} catch (InterruptedException e) {
					throw new IllegalStateException(e);
				}
				return "Result of the asynchronous computation"; })
			.thenApplyAsync(val -> {
				LOGGER.info("APPLY");
				return val + ", is not applied ";
				});
	}
}
