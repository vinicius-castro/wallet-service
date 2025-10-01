package com.vicastro.walletservice.adapter;

import com.vicastro.walletservice.application.dto.CreateWalletInput;
import com.vicastro.walletservice.application.dto.CreateWalletOutput;
import com.vicastro.walletservice.application.repository.WalletRepository;
import com.vicastro.walletservice.application.usecases.CreateWalletUseCase;

public class WalletController {

    private final WalletRepository repository;

    public WalletController(WalletRepository repository) {
        this.repository = repository;
    }

    public CreateWalletOutput createWallet(CreateWalletInput input) {
        var useCase = new CreateWalletUseCase(repository);
        return useCase.execute(input);
    }
}
