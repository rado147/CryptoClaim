package cf.cryptoclaim.exception;

public class KeyDerivationException extends CryptoClaimException {

	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 */
	public KeyDerivationException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public KeyDerivationException(String message, Throwable cause) {
		super(message, cause);
	}

}
