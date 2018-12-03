package cf.cryptoclaim.exception;

public class CryptoClaimRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 */
	public CryptoClaimRuntimeException(String message) {
		super(message);
	}
	
	/**
	 * @param message
	 * @param cause
	 */
	public CryptoClaimRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}
}
