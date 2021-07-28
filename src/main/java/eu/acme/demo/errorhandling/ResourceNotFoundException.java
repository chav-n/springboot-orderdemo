package eu.acme.demo.errorhandling;

/**
 * Exception thrown when the REST API cannot find a resource requested
 * with a GET method using a unique id.
 * 
 * @author nchavaranis
 *
 */
public class ResourceNotFoundException extends HTTP400Exception {
	private static final long serialVersionUID = -3742261023292732653L;
	public ResourceNotFoundException(String message) {
		super(message);
	}

}
