package com.vicastro.walletservice.infra.api.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vicastro.walletservice.infra.api.rest.request.CreateWalletRequest;
import com.vicastro.walletservice.infra.api.rest.request.FundsRequest;
import com.vicastro.walletservice.infra.api.rest.request.TransferFundsRequest;
import com.vicastro.walletservice.infra.repository.TransactionRepositoryImpl;
import com.vicastro.walletservice.infra.repository.WalletRepositoryImpl;
import com.vicastro.walletservice.shared.utils.OffsetDateTimeUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WalletRestController.class)
class WalletRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WalletRepositoryImpl walletRepository;

    @MockitoBean
    private TransactionRepositoryImpl transactionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateWalletAndReturn201() throws Exception {
        var userId = "user-1";
        var walletId = "wallet-123";
        var request = new CreateWalletRequest(userId);

        when(walletRepository.create(any())).thenReturn(new com.vicastro.walletservice.domain.Wallet(walletId, userId));

        mockMvc.perform(post("/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.wallet_id").value(walletId));
    }

    @Test
    void shouldAddFundsAndReturn204() throws Exception {
        var walletId = "wallet-1";
        var amount = 100L;
        var request = new FundsRequest(walletId, amount);

        when(walletRepository.existsById(walletId)).thenReturn(true);
        doNothing().when(transactionRepository).addTransaction(any());

        mockMvc.perform(post("/wallet/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldWithdrawFundsAndReturn204() throws Exception {
        var walletId = "wallet-1";
        var amount = 50L;
        var request = new FundsRequest(walletId, amount);

        when(walletRepository.existsById(walletId)).thenReturn(true);
        when(transactionRepository.getBalance(walletId)).thenReturn(100L);
        doNothing().when(transactionRepository).addTransaction(any());

        mockMvc.perform(post("/wallet/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldTransferFundsAndReturn204() throws Exception {
        var fromWalletId = "wallet-1";
        var toWalletId = "wallet-2";
        var amount = 30L;
        var request = new TransferFundsRequest(fromWalletId, toWalletId, amount);

        when(walletRepository.existsById(fromWalletId)).thenReturn(true);
        when(transactionRepository.getBalance(fromWalletId)).thenReturn(100L);
        when(walletRepository.existsById(toWalletId)).thenReturn(true);
        doNothing().when(transactionRepository).addTransaction(any());

        mockMvc.perform(post("/wallet/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldGetCurrentBalanceAndReturn200() throws Exception {
        var walletId = "wallet-1";
        var balance = 150L;

        when(walletRepository.existsById(walletId)).thenReturn(true);
        when(transactionRepository.getBalance(walletId)).thenReturn(balance);

        var expectedValueInCents = balance / 100;
        mockMvc.perform(get("/wallet/{walletId}/balance", walletId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.wallet_id").value(walletId))
                .andExpect(jsonPath("$.balance").value(expectedValueInCents));
    }

    @Test
    void shouldGetBalanceByDateAndReturn200() throws Exception {
        var walletId = "wallet-1";
        var date = OffsetDateTimeUtil.parseToOffsetDateTime("2024-06-01T00:00:00-03:00");
        var balance = 200L;

        when(walletRepository.existsById(walletId)).thenReturn(true);
        when(transactionRepository.getBalanceByDate(walletId, date)).thenReturn(balance);

        var expectedValueInCents = balance / 100;
        mockMvc.perform(get("/wallet/{walletId}/balance/{date}", walletId, date))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.wallet_id").value(walletId))
                .andExpect(jsonPath("$.balance").value(expectedValueInCents));
    }
}