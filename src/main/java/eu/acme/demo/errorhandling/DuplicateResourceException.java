package eu.acme.demo.errorhandling;

/**
 * Exception thrown when a new resource cannot be created because of some
 * duplication constraint.
 * 
 * @author Νικόλαος Χαβαράνης
 *
 */

public class DuplicateResourceException extends HTTP400Exception {
	private static final long serialVersionUID = -8313063900167445003L;
	public DuplicateResourceException(String message) {
		super(message);
	}

}
