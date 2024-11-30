package com.nyx.Linkify.feature.authentication.controller;

import com.nyx.Linkify.feature.authentication.dto.AuthenticateResponseBody;
import com.nyx.Linkify.feature.authentication.dto.AuthenticationRequestBody;
import com.nyx.Linkify.feature.authentication.model.AuthenticationUser;
import com.nyx.Linkify.feature.authentication.service.AuthenticationService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("api/v1/authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @GetMapping("/user")
    public AuthenticationUser getUser(@RequestAttribute("authenticatedUser")AuthenticationUser authenticatedUser) {
        return authenticationService.getUser(authenticatedUser.getEmail());
    }

    @PostMapping("/login")
    public AuthenticateResponseBody loginPage(@Valid @RequestBody AuthenticationRequestBody registerRequestBody) {
        return authenticationService.login(registerRequestBody);
    }

    @PostMapping("/register")
    public AuthenticateResponseBody registerUser(@Valid @RequestBody AuthenticationRequestBody registerRequestBody) throws MessagingException, UnsupportedEncodingException {
        return  authenticationService.register(registerRequestBody);
    }

    @PutMapping("/validate-email-verification-token")
    public String verifyEmail(@RequestParam String token, @RequestAttribute("authenticatedUser")AuthenticationUser user) {
        authenticationService.validateEmailVerificationToken(token,user.getEmail());
        return "Email verification";
    }

    @GetMapping("/send-email-verification-token")
    public String sendEmailVerificationToken(@RequestAttribute("authenticatedUser")AuthenticationUser user) throws MessagingException {
        authenticationService.sendEmailVerificationToken(user.getEmail());
        return "Email verification token sent";
    }

    @PutMapping("/send-password-reset-token")
    public String sendPasswordResetToken(@RequestParam String email) {
        authenticationService.sendPasswordResetToken(email);
        return "Password reset token sent";
    }


    @PutMapping("/reset-password")
    public String resetPassword(@RequestParam String newPassword, @RequestParam String token, @RequestParam String email) {
        authenticationService.resetPassword(email,newPassword,token);
        return "Password reset successful";
    }





















}























