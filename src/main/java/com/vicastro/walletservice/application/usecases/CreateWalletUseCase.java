package com.vicastro.walletservice.application.usecases;

import com.vicastro.walletservice.application.dto.CreateWalletInput;
import com.vicastro.walletservice.application.dto.CreateWalletOutput;
import com.vicastro.walletservice.application.repository.WalletRepository;
import com.vicastro.walletservice.shared.exception.InvalidWalletCreationException;

public class CreateWalletUseCase {

    private final WalletRepository walletRepository;

    public CreateWalletUseCase(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public CreateWalletOutput execute(CreateWalletInput input) {
        if (walletRepository.existsByUserId(input.userId())) {
            throw new InvalidWalletCreationException();
        }
        var newWallet = walletRepository.create(input.userId());

        return CreateWalletOutput.builder().walletId(newWallet.code()).build();
    }
}
