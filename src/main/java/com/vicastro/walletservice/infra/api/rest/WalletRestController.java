package com.vicastro.walletservice.infra.api.rest;

import com.vicastro.walletservice.adapter.WalletController;
import com.vicastro.walletservice.application.dto.CreateWalletInput;
import com.vicastro.walletservice.infra.api.rest.request.FundsRequest;
import com.vicastro.walletservice.infra.api.rest.request.CreateWalletRequest;
import com.vicastro.walletservice.infra.api.rest.request.TransferFundsRequest;
import com.vicastro.walletservice.infra.api.rest.response.CreateWalletResponse;
import com.vicastro.walletservice.infra.api.rest.response.WalletBalanceResponse;
import com.vicastro.walletservice.infra.repository.TransactionRepositoryImpl;
import com.vicastro.walletservice.infra.repository.WalletRepositoryImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/wallet")
public class WalletRestController {

    private final WalletRepositoryImpl walletRepository;
    private final TransactionRepositoryImpl transactionRepository;

    public WalletRestController(WalletRepositoryImpl walletRepository, TransactionRepositoryImpl transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    @PostMapping("/deposit")
    public ResponseEntity<Void> addFunds(@RequestBody FundsRequest request) {
        getWalletController().addFunds(request.walletId(), request.amount());
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<CreateWalletResponse> createWallet(@RequestBody CreateWalletRequest request) {
        var input = CreateWalletInput.builder()
                .userId(request.userId())
                .build();
        var output = getWalletController().createWallet(input);

        return ResponseEntity.status(201).body(new CreateWalletResponse(output.walletId()));
    }

    @GetMapping("{walletId}/balance")
    public ResponseEntity<WalletBalanceResponse> getCurrentBalance(@PathVariable("walletId") String walletId) {
        var balance = getWalletController().getCurrentBalance(walletId);
        return ResponseEntity.ok(new WalletBalanceResponse(walletId, BigDecimal.valueOf(balance)));
    }

    @GetMapping("{walletId}/balance/{date}")
    public ResponseEntity<WalletBalanceResponse> getBalanceByDate(@PathVariable("walletId") String walletId,
                                                                  @PathVariable("date") String date) {
        var balance = getWalletController().getBalanceByDate(walletId, date);
        return ResponseEntity.ok(new WalletBalanceResponse(walletId, BigDecimal.valueOf(balance)));
    }

    @PostMapping("/transfer")
    public ResponseEntity<Void> transferFunds(@RequestBody TransferFundsRequest request) {
        getWalletController().transferFunds(request.fromWalletId(), request.toWalletId(), request.amount());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Void> withdrawFunds(@RequestBody FundsRequest request) {
        getWalletController().withdrawFunds(request.walletId(), request.amount());
        return ResponseEntity.noContent().build();
    }

    private WalletController getWalletController() {
        return new WalletController(walletRepository, transactionRepository);
    }
}
