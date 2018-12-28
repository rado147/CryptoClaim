package cf.cryptoclaim.exception;

public class AuthenticationFailedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AuthenticationFailedException(String message) {
		super(message);
	}
	
	public AuthenticationFailedException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
