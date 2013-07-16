package stockfile.exceptions;

@SuppressWarnings("serial")
public class CreateUserException extends Exception {

	public enum CreateUserError {
		PASSWORD,EMAIL,INVALID_FIRSTNAME,INVALID_LASTNAME,USERNAME_TAKEN
	}
	
	public CreateUserException (CreateUserError error) {
		switch (error) {
		
			case PASSWORD:
				System.err.println("Invalid password.");
			case EMAIL:
				System.err.println("Invalid email address.");
			case USERNAME_TAKEN:
				System.err.println("Username is already taken. Please try again.");
		
		}
		
	}
	
}
