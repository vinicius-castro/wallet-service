package com.vicastro.walletservice.infra.api.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vicastro.walletservice.domain.Wallet;
import com.vicastro.walletservice.infra.api.rest.request.CreateWalletRequest;
import com.vicastro.walletservice.infra.repository.WalletRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WalletRestController.class)
class WalletRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WalletRepositoryImpl walletRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateWalletAndReturn201() throws Exception {
        var userId = "user-1";
        var walletId = "wallet-123";
        var request = new CreateWalletRequest(userId);

        when(walletRepository.create(any())).thenReturn(new Wallet(walletId, userId));

        mockMvc.perform(post("/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.wallet_id").value(walletId));
    }
}
