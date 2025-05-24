package com.example.hackerthon.Exception;


import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@NoArgsConstructor
public enum ErrorCode {
    INVALID_KEY(1001,"Invalid key", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1001,"User not found",HttpStatus.NOT_FOUND),
    USER_EXIST(1002,"User is existed",HttpStatus.BAD_REQUEST),
    ROLE_IS_EXITED(1006,"Role is existed",HttpStatus.BAD_REQUEST),
    ROLE_NOT_FOUND(1006,"Role not found",HttpStatus.NOT_FOUND),
    USER_MEMBERSHIP_EXIST(1006,"User member is Valid",HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1004,"Khong du quyen truy cap",HttpStatus.UNAUTHORIZED),
    EMAIL_IS_EXITED(1005,"Email is existed",HttpStatus.BAD_REQUEST),
    UNCATEGORIZED(9999,"Uncategorized", HttpStatus.INTERNAL_SERVER_ERROR);
    ErrorCode(int Code,String Message, HttpStatusCode sponse){
        this.code = Code;
        this.message = Message;
        this.status = sponse;
    }
    private int code;
    private String message;
    private HttpStatusCode status;
}