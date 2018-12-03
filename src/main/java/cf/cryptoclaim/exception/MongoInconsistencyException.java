package cf.cryptoclaim.exception;

public class MongoInconsistencyException extends CryptoClaimRuntimeException {

	private static final long serialVersionUID = 2733936267023903407L;

	/**
	 * @param message
	 */
	public MongoInconsistencyException(String message) {
		super(message);
	}
	
	/**
	 * @param message
	 * @param cause
	 */
	public MongoInconsistencyException(String message, Throwable cause) {
		super(message, cause);
	}
}
