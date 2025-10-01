package com.vicastro.walletservice.infra.api.rest;

import com.vicastro.walletservice.adapter.WalletController;
import com.vicastro.walletservice.application.dto.CreateWalletInput;
import com.vicastro.walletservice.infra.api.rest.request.AddFundsRequest;
import com.vicastro.walletservice.infra.api.rest.request.CreateWalletRequest;
import com.vicastro.walletservice.infra.api.rest.response.CreateWalletResponse;
import com.vicastro.walletservice.infra.repository.TransactionRepositoryImpl;
import com.vicastro.walletservice.infra.repository.WalletRepositoryImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wallet")
public class WalletRestController {

    private final WalletRepositoryImpl walletRepository;
    private final TransactionRepositoryImpl transactionRepository;

    public WalletRestController(WalletRepositoryImpl walletRepository, TransactionRepositoryImpl transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    @PostMapping("/funds")
    public ResponseEntity<Void> addFunds(@RequestBody AddFundsRequest request) {
        (new WalletController(walletRepository, transactionRepository)).addFunds(request.walletId(), request.amount());
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<CreateWalletResponse> createWallet(@RequestBody CreateWalletRequest request) {
        var input = CreateWalletInput.builder()
                .userId(request.userId())
                .build();
        var output = (new WalletController(walletRepository, transactionRepository)).createWallet(input);

        return ResponseEntity.status(201).body(new CreateWalletResponse(output.walletId()));
    }
}
