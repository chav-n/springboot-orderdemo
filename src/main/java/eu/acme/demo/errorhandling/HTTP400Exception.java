package eu.acme.demo.errorhandling;

/**
 * Base class for all exceptions that need HTTP-400 error code.
 * 
 * @author Νικόλαος Χαβαράνης
 *
 */
public class HTTP400Exception extends Exception {
	private static final long serialVersionUID = -3721206664672945322L;

	public HTTP400Exception(String message) {
		super(message);
	}

}
