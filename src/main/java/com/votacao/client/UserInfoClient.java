package com.votacao.client;

import com.votacao.dto.UserInfoResponse;

public interface UserInfoClient {
    UserInfoResponse consultar(String cpf);
}
