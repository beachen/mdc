package se.beachen.mdc;

import java.util.logging.Logger;

/**
 * @author Anders Strand
 */
public class JULLogging {

	private static final Logger LOG = Logger.getLogger(JULLogging.class.getName());

	public static void log(){

		LOG.info("Info logging");



	}
}
