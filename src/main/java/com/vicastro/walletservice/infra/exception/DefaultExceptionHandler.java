package com.vicastro.walletservice.infra.exception;

import com.vicastro.walletservice.shared.exception.InvalidAmountException;
import com.vicastro.walletservice.shared.exception.InvalidWalletCreationException;
import com.vicastro.walletservice.shared.exception.WalletNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DefaultExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(InvalidWalletCreationException.class)
    public ResponseEntity<String> handleInvalidWalletCreationException(InvalidWalletCreationException ex) {
        logger.error("m=handleInvalidWalletCreationException, message={}", ex.getMessage(), ex);
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(InvalidAmountException.class)
    public ResponseEntity<String> handleInvalidAmountException(InvalidAmountException ex) {
        logger.error("m=handleInvalidAmountException, message={}", ex.getMessage(), ex);
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<String> handleWalletNotFoundException(WalletNotFoundException ex) {
        logger.error("m=handleWalletNotFoundException, message={}", ex.getMessage(), ex);
        return ResponseEntity.notFound().build();
    }
}
