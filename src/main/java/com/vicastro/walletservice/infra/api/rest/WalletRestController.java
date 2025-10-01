package com.vicastro.walletservice.infra.api.rest;

import com.vicastro.walletservice.adapter.WalletController;
import com.vicastro.walletservice.application.dto.CreateWalletInput;
import com.vicastro.walletservice.infra.api.rest.request.CreateWalletRequest;
import com.vicastro.walletservice.infra.api.rest.response.CreateWalletResponse;
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

    public WalletRestController(WalletRepositoryImpl walletRepository) {
        this.walletRepository = walletRepository;
    }

    @PostMapping
    public ResponseEntity<CreateWalletResponse> createWallet(@RequestBody CreateWalletRequest request) {
        var output = (new WalletController(walletRepository)).createWallet(CreateWalletInput.builder()
                .userId(request.userId())
                .build());

        return ResponseEntity.status(201).body(new CreateWalletResponse(output.walletId()));
    }
}
