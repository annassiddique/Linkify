package com.nyx.Linkify.feature.authentication.controller;

import com.nyx.Linkify.feature.authentication.dto.AuthenticateResponseBody;
import com.nyx.Linkify.feature.authentication.dto.AuthenticationRequestBody;
import com.nyx.Linkify.feature.authentication.model.AuthenticationUser;
import com.nyx.Linkify.feature.authentication.service.AuthenticationService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/v1/authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @GetMapping("/user")
    public AuthenticationUser getUser(@RequestAttribute("authenticatedUser") AuthenticationUser authenticatedUser) {
        return authenticationService.getUser(authenticatedUser.getEmail());
    }

    @PostMapping("/login")
    public AuthenticateResponseBody loginPage(@Valid @RequestBody AuthenticationRequestBody registerRequestBody) {
        return authenticationService.login(registerRequestBody);
    }

    @PostMapping("/register")
    public AuthenticateResponseBody registerUser(@Valid @RequestBody AuthenticationRequestBody registerRequestBody) throws MessagingException, UnsupportedEncodingException {
        return authenticationService.register(registerRequestBody);
    }



    @DeleteMapping("/delete")
    public String deleteUser(@RequestAttribute("authenticatedUser") AuthenticationUser authenticatedUser) {
        authenticationService.deleteUser(authenticatedUser.getId());
        return "User deleted successfully";
    }




    @PutMapping("/validate-email-verification-token")
    public Map<String, String> verifyEmail(@RequestParam String token, @RequestAttribute("authenticatedUser") AuthenticationUser user) {
        authenticationService.validateEmailVerificationToken(token, user.getEmail());

        // Return response as JSON object
        Map<String, String> response = new HashMap<>();
        response.put("message", "Email verification successful");
        return response;
    }

    @GetMapping("/send-email-verification-token")
    public Map<String, String> sendEmailVerificationToken(@RequestAttribute("authenticatedUser") AuthenticationUser user) throws MessagingException {
        authenticationService.sendEmailVerificationToken(user.getEmail());

        // Return response as JSON object
        Map<String, String> response = new HashMap<>();
        response.put("message", "Email verification token sent");
        return response;
    }

    @PutMapping("/send-password-reset-token")
    public Map<String, String> sendPasswordResetToken(@RequestParam String email) {
        authenticationService.sendPasswordResetToken(email);

        // Return response as JSON object
        Map<String, String> response = new HashMap<>();
        response.put("message", "Password reset token sent");
        return response;
    }

    @PutMapping("/reset-password")
    public Map<String, String> resetPassword(@RequestParam String newPassword, @RequestParam String token, @RequestParam String email) {
        authenticationService.resetPassword(email, newPassword, token);

        // Return response as JSON object
        Map<String, String> response = new HashMap<>();
        response.put("message", "Password reset successful");
        return response;
    }

    @PutMapping("/profile/{id}")
    public AuthenticationUser updateUserProfile(
            @RequestAttribute("authenticatedUser") AuthenticationUser user,
            @PathVariable Long id,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String location) {

        if (!user.getId().equals(id)) {
            throw  new ResponseStatusException(HttpStatus.FORBIDDEN,"User does not have permission to update his profile");
        }

        return authenticationService.updateUserProfile(id,firstName,lastName,company,position,location);

    }






}
