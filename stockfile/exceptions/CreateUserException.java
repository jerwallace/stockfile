package stockfile.exceptions;

@SuppressWarnings("serial")
public class CreateUserException extends Exception {

	public enum CreateUserError {
		PASSWORD,EMAIL, INVALID_USERNAME, INVALID_FIRSTNAME,INVALID_LASTNAME,USERNAME_TAKEN
	}
	
	public CreateUserException (CreateUserError error) {
            
            String msg = "Invalid input. Allowed pattern:\n";
            
		switch (error) {
		
			case INVALID_USERNAME:
                                msg = msg + "- length between 5 to 30 consisting only of the following...\n"
                                        + "- lower case letters\n"
                                        + "- digits\n"
                                        + "- underscore or hiphen\n";
                                break;
                        case PASSWORD:
                                msg = msg + "- length between 8 to 30 consisting of any characters\n";
                                break;
                        case EMAIL:
				msg = msg + "Invalid email address.\n";
                                break;
                        case INVALID_FIRSTNAME:
                        case INVALID_LASTNAME:
                                msg = msg + "- length between 1 to 50 consisting only of the following...\n"
                                        + "- lower case letters\n"
                                        + "- spaces\n";
                                break;
			case USERNAME_TAKEN:
				msg = msg + "Username is already taken.\n";
                                break;
		
		}
                
                msg = msg + "Please try again.\n";
                
                System.err.println(msg);
		
	}
	
}