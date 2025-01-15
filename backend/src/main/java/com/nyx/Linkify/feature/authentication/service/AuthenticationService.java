package com.nyx.Linkify.feature.authentication.service;

import com.nyx.Linkify.feature.authentication.dto.AuthenticateResponseBody;
import com.nyx.Linkify.feature.authentication.dto.AuthenticationRequestBody;
import com.nyx.Linkify.feature.authentication.model.AuthenticationUser;
import com.nyx.Linkify.feature.authentication.repo.AuthenticateUserRepo;
import com.nyx.Linkify.feature.authentication.utils.EmailService;
import com.nyx.Linkify.feature.authentication.utils.Encoder;
import com.nyx.Linkify.feature.authentication.utils.JsonWebToken;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class AuthenticationService {
    private static final Logger logger = Logger.getLogger(String.valueOf(AuthenticationService.class));
    private final JsonWebToken jsonWebToken;
    private final Encoder encoder;
    private final AuthenticateUserRepo authenticateUserRepo;
    private final EmailService emailService;
    private final int durationInMintes = 1;


    @PersistenceContext
    private EntityManager entityManager;




    public AuthenticationService(JsonWebToken jsonWebToken, Encoder encoder, AuthenticateUserRepo authenticateUserRepo, EmailService emailService) {
        this.jsonWebToken = jsonWebToken;
        this.encoder = encoder;
        this.authenticateUserRepo = authenticateUserRepo;
        this.emailService = emailService;
    }


    public static String generateEmailVerificationToken(){
        SecureRandom random = new SecureRandom();
        StringBuilder token = new StringBuilder(5);
        for (int i = 0; i < 5; i++) {
            token.append(random.nextInt(10));
        }
        return token.toString();
    }


    public void sendEmailVerificationToken(String email) throws MessagingException {
        Optional<AuthenticationUser> authenticationUser = authenticateUserRepo.findByEmail(email);
        if (authenticationUser.isPresent() && !authenticationUser.get().getEmailVerified()) {
            String emailVerificationToken = generateEmailVerificationToken();
            String hashedToken = encoder.encode(emailVerificationToken);
            authenticationUser.get().setEmailVerificationToken(hashedToken);
            authenticationUser.get().setEmailVerificationTokenExpiryDate(LocalDateTime.now().plusMinutes(durationInMintes));
            authenticateUserRepo.save(authenticationUser.get());
            String subject = "Email Verification";
            String body = String.format("One step away from accessing Linkify \n\n"
            + "Enter this code to verify your email : " + "%s\n\n" + "The code will expire in " +"%s"+"minutes",
                    emailVerificationToken, durationInMintes
            );
            try {
                emailService.sendEmail(email, subject, body);
            } catch (Exception e) {
                logger.info("Error while sending email: {}");

            }
        }else{
            throw new IllegalArgumentException("Invalid email verification token or email is already verified");
        }
    }

    public AuthenticationUser getUser(String email) {
        return authenticateUserRepo.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found."));
    }


    public void validateEmailVerificationToken(String token, String email) {
        Optional<AuthenticationUser> authenticationUser = authenticateUserRepo.findByEmail(email);
        if (authenticationUser.isPresent() && encoder.matches(token, authenticationUser.get().getEmailVerificationToken()) && !authenticationUser.get().getEmailVerificationTokenExpiryDate().isBefore(LocalDateTime.now())) {
            authenticationUser.get().setEmailVerified(true);
            authenticationUser.get().setEmailVerificationTokenExpiryDate(null);
            authenticationUser.get().setEmailVerificationToken(null);
            authenticateUserRepo.save(authenticationUser.get());
        } else if
        (authenticationUser.isPresent() && encoder.matches(token, authenticationUser.get().getEmailVerificationToken()) && authenticationUser.get().getEmailVerificationTokenExpiryDate().isBefore(LocalDateTime.now()))
        {
            throw new IllegalArgumentException("Email verification token expired");
        } else{
            throw new IllegalArgumentException("Email verification token failed");
        }


    }



    public AuthenticateResponseBody register(AuthenticationRequestBody registerRequestBody) throws MessagingException, UnsupportedEncodingException {
        AuthenticationUser user = authenticateUserRepo.save(new AuthenticationUser(registerRequestBody.getEmail(),encoder.encode(registerRequestBody.getPassword())));
        String emailVerificationToken = generateEmailVerificationToken();
        String hashedToken = encoder.encode(emailVerificationToken);
        user.setEmailVerificationToken(hashedToken);
        user.setEmailVerificationTokenExpiryDate(LocalDateTime.now().plusMinutes(durationInMintes));
        authenticateUserRepo.save(user);

        String subject = "Email Verification";
        String body = String.format("""
                Only one step away from accessing Linkify \n
                
                Enter this code to verify your email : " + "%s\n\n" + "The code will expire in %s"+"minutes"
                """,emailVerificationToken, durationInMintes);
        try{
            emailService.sendEmail(registerRequestBody.getEmail(), subject, body);
        }catch (Exception e){
            logger.info("Error while sending email: {}");
        }
        String authToken = jsonWebToken.generateToken(registerRequestBody.getEmail());
        return new AuthenticateResponseBody(authToken,"successful");
    }

    public AuthenticateResponseBody login(@Valid AuthenticationRequestBody loginRequestBody) {
        AuthenticationUser user = authenticateUserRepo.findByEmail(loginRequestBody.getEmail()).orElseThrow(()->new IllegalArgumentException("User not found"));
        if(!encoder.matches(loginRequestBody.getPassword(),user.getPassword())){
            throw new IllegalArgumentException("Wrong password");
        }
        String token = jsonWebToken.generateToken(loginRequestBody.getEmail());
        return new AuthenticateResponseBody(token,"successful");
    }



    public void sendPasswordResetToken(String email) {
        Optional<AuthenticationUser> user = authenticateUserRepo.findByEmail(email);
        if (user.isPresent()) {
            String passwordResetToken = generateEmailVerificationToken();
            String hashedToken = encoder.encode(passwordResetToken);
            user.get().setPasswordResetToken(hashedToken);
            user.get().setPasswordResetTokenExpiryDate(LocalDateTime.now().plusMinutes(durationInMintes));
            authenticateUserRepo.save(user.get());
            String subject = "Password Reset";
            String body = String.format("""
                            You requested a password reset.
                            
                            Enter this code to reset your password: %s. The code will expire in %s minutes.""",
                    passwordResetToken, durationInMintes);
            try {
                emailService.sendEmail(email, subject, body);
            } catch (Exception e) {
                logger.info("Error while sending email: {}");
            }
        } else {
            throw new IllegalArgumentException("User not found.");
        }
    }



    public void resetPassword(String email, String newPassword, String token) {
        // Retrieve the user by email
        Optional<AuthenticationUser> user = authenticateUserRepo.findByEmail(email);


        if (!user.isPresent()) {
            throw new IllegalArgumentException("User not found.");
        }

        // Get the user object
        AuthenticationUser authenticationUser = user.get();

        // Validate the token
        boolean isTokenValid = encoder.matches(token, authenticationUser.getPasswordResetToken());

        // Check if the token is valid and not expired
        if (isTokenValid && !authenticationUser.getPasswordResetTokenExpiryDate().isBefore(LocalDateTime.now())) {
            // Token is valid and not expired, reset the password
            authenticationUser.setPasswordResetToken(null);
            authenticationUser.setPasswordResetTokenExpiryDate(null);
            authenticationUser.setPassword(encoder.encode(newPassword)); // Hash the new password
            authenticateUserRepo.save(authenticationUser);
        } else if (isTokenValid && authenticationUser.getPasswordResetTokenExpiryDate().isBefore(LocalDateTime.now())) {
            // Token is expired
            throw new IllegalArgumentException("Password reset token expired.");
        } else {
            // Invalid token
            throw new IllegalArgumentException("Password reset token failed.");
        }
    }


    public AuthenticationUser updateUserProfile(Long id, String firstName, String lastName, String company, String position, String location) {

        AuthenticationUser user = authenticateUserRepo.findById(id).orElseThrow(()->new RuntimeException("User not found"));

        if(firstName !=null) user.setFirstName(firstName);
        if(lastName !=null) user.setLastName(lastName);
        if(company !=null) user.setCompany(company);
        if(position !=null) user.setPosition(position);
        if(location !=null) user.setLocation(location);

        return authenticateUserRepo.save(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        AuthenticationUser user = entityManager.find(AuthenticationUser.class, userId);

        if(user != null){
            entityManager.createNativeQuery("DELETE FROM post_likes WHERE user_id= :userId")
                            .setParameter("userId", userId)
                                    .executeUpdate();

            authenticateUserRepo.deleteById(userId);
        }


    }
}

