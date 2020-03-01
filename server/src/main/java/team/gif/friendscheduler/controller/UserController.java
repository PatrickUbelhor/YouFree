package team.gif.friendscheduler.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.gif.friendscheduler.exception.InvalidFieldException;
import team.gif.friendscheduler.model.User;
import team.gif.friendscheduler.model.request.NewUser;
import team.gif.friendscheduler.service.AuthService;
import team.gif.friendscheduler.service.FieldValidator;
import team.gif.friendscheduler.service.FriendshipService;
import team.gif.friendscheduler.service.IntervalService;
import team.gif.friendscheduler.service.UserService;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
	
	private final FieldValidator fieldValidator;
	private final AuthService authService;
	private final FriendshipService friendshipService;
	private final IntervalService intervalService;
	private final UserService userService;
	private static final Logger logger = LogManager.getLogger(UserController.class);
	
	@Autowired
	public UserController(FieldValidator fieldValidator,
	                      AuthService authService,
	                      FriendshipService friendshipService,
	                      IntervalService intervalService,
	                      UserService userService) {
		
		this.fieldValidator = fieldValidator;
		this.authService = authService;
		this.friendshipService = friendshipService;
		this.intervalService = intervalService;
		this.userService = userService;
	}
	
	
	@PostMapping(value = "/user", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> createUser(
			@Valid @RequestBody NewUser newUser) {
		
		logger.info("Received createUser request");
		fieldValidator.validateUser(newUser);
		User user = userService.createUser(newUser);
		logger.info("Created user " + user.getEmail());
		
		String token = authService.generateSessionToken(user.getId());
		
		return ResponseEntity.status(HttpStatus.CREATED)
				.header("token", "" + token)
				.body(user);
	}
	
	
	@GetMapping(value = "/user")
	public ResponseEntity<User> getUser(
			@RequestParam(value = "id", required = false) Long id,
			@RequestParam(value = "username", required = false) String username,
			@RequestParam(value = "email", required = false) String email,
			@RequestHeader("token") String token) {
		
		logger.info("Received getUser request");
		User result = userService.queryUsers(id, username, email);
		
		if (result == null) {
			result = userService.getUser(authService.getUserIdFromToken(token));
		}
		
		return ResponseEntity.ok(result);
	}
	
	
	@PutMapping(value = "/user", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> updateUser(
			@RequestHeader("token") String token,
			@Valid @RequestBody NewUser user) {
		
		logger.info("Received updateUser request");
		
		if (!fieldValidator.validateOneNonNull(user)) {
			throw new InvalidFieldException("One or more fields must be non-empty");
		}
		
		Long id = authService.getUserIdFromToken(token);
		User result = userService.updateUser(id, user);
		
		return ResponseEntity.ok(result);
	}
	
	
	@DeleteMapping(value = "/user")
	public ResponseEntity<Void> deleteUser(
			@RequestHeader("token") String token) {
		
		logger.info("Received deleteUser request");
		Long id = authService.getUserIdFromToken(token);
		friendshipService.removeUser(id);
		intervalService.removeAllIntervals(id);
		userService.deleteUser(id);
		
		return ResponseEntity.noContent().build();
	}
	
}
