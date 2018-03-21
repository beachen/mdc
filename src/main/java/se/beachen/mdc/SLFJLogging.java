package se.beachen.mdc;

import ch.qos.logback.classic.LoggerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
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

		MDC.put("correlationId", "UniqueId");
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

	void executor(){

		Callable<Integer> task = () -> {
			try {
				TimeUnit.SECONDS.sleep(1);
				return 123;
			}
			catch (InterruptedException e) {
				throw new IllegalStateException("task interrupted", e);
			}
		};

		Future<Integer> res = threadPoolExecutor.submit(task);

	}
}
