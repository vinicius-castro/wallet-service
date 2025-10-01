package com.vicastro.walletservice.infra.exception;

import com.vicastro.walletservice.shared.exception.InvalidWalletCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DefaultExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(InvalidWalletCreationException.class)
    public ResponseEntity<Object> handleInvalidWalletCreationException(InvalidWalletCreationException ex) {
        logger.error("m=handleInvalidWalletCreationException, message={}", ex.getMessage(), ex);
        return ResponseEntity.badRequest().build();
    }
}
