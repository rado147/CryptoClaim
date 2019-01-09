package cf.cryptoclaim.exception;

public class TooBigRequestException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 */
	public TooBigRequestException(String message) {
		super(message);
	}
	
	/**
	 * @param message
	 * @param cause
	 */
	public TooBigRequestException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
