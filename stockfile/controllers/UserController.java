package stockfile.controllers;

import stockfile.exceptions.CreateUserException;
import stockfile.exceptions.CreateUserException.CreateUserError;

public class UserController {


	
	public void createUser() throws CreateUserException {
		
		throw new CreateUserException(CreateUserError.PASSWORD);
	}
	
}
