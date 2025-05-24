package com.example.hackerthon.Service;

import com.example.hackerthon.Dto.Request.AuthenticationRequest;
import com.example.hackerthon.Dto.Request.IntrospectRequest;
import com.example.hackerthon.Dto.Response.AuthenticationResponse;
import com.example.hackerthon.Dto.Response.IntrospectResponse;
import com.example.hackerthon.Exception.AppException;
import com.example.hackerthon.Exception.ErrorCode;
import com.example.hackerthon.Model.InvalidateToken;
import com.example.hackerthon.Model.User;
import com.example.hackerthon.Repo.InvalidateRepository;
import com.example.hackerthon.Repo.UserRepo;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepo userRepository;
    InvalidateRepository invalidateRepository;
    @NonFinal
    @Value("${jwt.signedJWT}")
    protected String SIGN_KEY;
    @NonFinal
    @Value("${jwt.valid-duration}")
    protected Long VALID_DURATION;
    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected Long REFRESH_DURATION;
    public AuthenticationResponse isAuthenticated(AuthenticationRequest request) throws JOSEException {
        var user = userRepository.findByEmail(request.email()).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_FOUND));
        log.info("1");
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        log.info("2");
        log.info(user.getEmail());
        log.info(passwordEncoder.encode(user.getPassword()));
        boolean result = passwordEncoder.matches(request.password(),
                user.getPassword());
        if (!result) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        var token = generateToken(user);
        return AuthenticationResponse.builder()
                .accessToken(token)
                .authenticated(result)
                .build();
    }

    public IntrospectResponse instrospect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.token();
        boolean valid=true;
        try {
            verifyToken(token,false);
        }catch (AppException e){
            valid=false;
        }
        return IntrospectResponse.builder()
                .valid(valid)
                .build();
    }

    private String generateToken(User user) throws JOSEException {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        log.info("3");
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("dailun.com")
                .issueTime(new Date())
                .jwtID(UUID.randomUUID().toString())
                .expirationTime(new Date(Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
                .claim("scope",customScope(user))
                .build();
        Payload payload = new Payload(claimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        jwsObject.sign(new MACSigner(SIGN_KEY.getBytes()));
        return jwsObject.serialize();
    }
    private SignedJWT verifyToken(String token, boolean IsRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGN_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expiryTime =
                (IsRefresh)?
                        new Date(signedJWT.getJWTClaimsSet().getIssueTime()
                                .toInstant().plus(REFRESH_DURATION, ChronoUnit.SECONDS).toEpochMilli())
                        :(signedJWT.getJWTClaimsSet().getExpirationTime());
        var verified=signedJWT.verify(verifier);
        if (!(verified && expiryTime.after(new Date()))){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        if(invalidateRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
        {
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        }
        var verifiers = signedJWT.verify(verifier);
        return signedJWT;
    }
   /* public void logout(LogOutRequest request) throws ParseException, JOSEException {

        try {
            var signToken=verifyToken(request.getToken(),true);
            String jit=signToken.getJWTClaimsSet().getJWTID();
            Date exp=signToken.getJWTClaimsSet().getExpirationTime();
            InvalidateToken invalidateToken= InvalidateToken.builder()
                    .id(jit)
                    .expiryTime(exp)
                    .build();
            invalidateRepository.save(invalidateToken);
        }catch (AppException appException){
            log.info("Token already expired");
        }


    }*/
   /* public AuthenticationResponse refresh(RefreshRequest request) throws ParseException, JOSEException {
        var signToken=verifyToken(request.(),true);
        String jit=signToken.getJWTClaimsSet().getJWTID();
        Date exp=signToken.getJWTClaimsSet().getExpirationTime();
        InvalidateToken invalidateToken= InvalidateToken.builder()
                .id(jit)
                .expiryTime(exp)
                .build();
        invalidateRepository.save(invalidateToken);
        var username=signToken.getJWTClaimsSet().getSubject();
        var user=userRepository.findByUserName(username)
                .orElseThrow(()->new AppException(ErrorCode.USER_NOT_FOUND));
        var token = generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }*/
    private String customScope(User user){
        StringJoiner springJoiner=new StringJoiner(" ");
        if(!CollectionUtils.isEmpty(user.getRoles())){
            user.getRoles().forEach(role->{
                springJoiner.add("ROLE_"+role.getName());
            });
        }
        return springJoiner.toString();
    }
}
