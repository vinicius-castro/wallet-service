package com.vicastro.walletservice.adapter;

import com.vicastro.walletservice.application.dto.CreateWalletInput;
import com.vicastro.walletservice.application.dto.CreateWalletOutput;
import com.vicastro.walletservice.application.repository.TransactionRepository;
import com.vicastro.walletservice.application.repository.WalletRepository;
import com.vicastro.walletservice.application.usecases.AddFundsUseCase;
import com.vicastro.walletservice.application.usecases.CreateWalletUseCase;
import com.vicastro.walletservice.application.usecases.GetWalletBalanceUseCase;
import com.vicastro.walletservice.application.usecases.TransferUseCase;
import com.vicastro.walletservice.application.usecases.WithdrawFundsUseCase;

import static com.vicastro.walletservice.shared.utils.OffsetDateTimeUtil.parseToOffsetDateTime;

public class WalletController {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public WalletController(WalletRepository walletRepository,
                            TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    public CreateWalletOutput createWallet(CreateWalletInput input) {
        var useCase = new CreateWalletUseCase(walletRepository);
        return useCase.execute(input);
    }

    public void addFunds(String walletId, Long amount) {
        var useCase = new AddFundsUseCase(walletRepository, transactionRepository);
        useCase.execute(walletId, amount);
    }

    public Long getCurrentBalance(String walletId) {
        var useCase = new GetWalletBalanceUseCase(walletRepository, transactionRepository);
        return useCase.execute(walletId);
    }

    public Long getBalanceByDate(String walletId, String date) {
        var useCase = new GetWalletBalanceUseCase(walletRepository, transactionRepository);
        return useCase.execute(walletId, parseToOffsetDateTime(date));
    }

    public void transferFunds(String fromWalletId, String toWalletId, Long amount) {
        var useCase = new TransferUseCase(walletRepository, transactionRepository);
        useCase.execute(fromWalletId, toWalletId, amount);
    }

    public void withdrawFunds(String walletId, Long amount) {
        var useCase = new WithdrawFundsUseCase(walletRepository, transactionRepository);
        useCase.execute(walletId, amount);
    }
}
