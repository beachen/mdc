package se.beachen.mdc;

import ch.qos.logback.classic.LoggerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;


/**
 * @author Anders Strand
 */
public class LoggerApp {

	private static Logger LOGGER = LoggerFactory.getLogger(LoggerApp.class);
	static
	{
		LoggerContext ctx = (LoggerContext) LoggerFactory.getILoggerFactory();
		ctx.setName("myContext");
	}

	public static void main(String[] args) {

		// Main thread

		MDC.put("correlationId", UUID.randomUUID().toString());
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

		MDC.remove("correlationId");


		// TODO: How to do it in Spring/HttpServletFilter
		// TODO:
	}
}
