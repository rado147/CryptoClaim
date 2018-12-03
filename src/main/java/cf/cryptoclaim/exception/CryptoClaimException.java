package cf.cryptoclaim.exception;

public class CryptoClaimException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 */
	public CryptoClaimException(String message) {
		super(message);
	}
	
	/**
	 * @param message
	 * @param cause
	 */
	public CryptoClaimException(String message, Throwable cause) {
		super(message, cause);
	}
}
