package com.example.hackerthon.Config;

import com.example.hackerthon.Dto.Request.IntrospectRequest;
import com.example.hackerthon.Service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.Objects;

@Component
@Slf4j
public class CustumJwtDecoder implements JwtDecoder {
    @Value("${jwt.signedJWT}")
    private String signerKey;

    @Autowired
    private AuthenticationService authenticationService;

    private NimbusJwtDecoder nimbusJwtDecoder=null;
    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            var response=authenticationService.instrospect(IntrospectRequest.builder()
                    .token(token)
                    .build());
            if(!response.valid()){
                throw new JwtException("Token is invalid");
            }
        }catch (JOSEException | ParseException e){
            throw  new JwtException(e.getMessage());
        }
        if(Objects.isNull(nimbusJwtDecoder))
        {
            SecretKeySpec secretKeySpec=new SecretKeySpec(signerKey.getBytes(),"HS512");
            nimbusJwtDecoder = NimbusJwtDecoder
                    .withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }
        log.info(nimbusJwtDecoder.toString());
        return nimbusJwtDecoder.decode(token);
    }
}
