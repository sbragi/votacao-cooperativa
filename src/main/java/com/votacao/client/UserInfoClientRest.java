package com.votacao.client;

import com.votacao.dto.UserInfoResponse;
import com.votacao.exception.BusinessException;
import com.votacao.exception.ServiceUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class UserInfoClientRest implements UserInfoClient {
    private static final Logger log = LoggerFactory.getLogger(UserInfoClientRest.class);
    private final RestClient restClient;

    public UserInfoClientRest(RestClient userInfoRestClient) {
        this.restClient = userInfoRestClient;
    }

    @Override
    public UserInfoResponse consultar(String cpf) {
        try {
        	UserInfoResponse statusRequest = restClient.get()
                    .uri("/users/{cpf}", cpf)
                    .retrieve()
                    .onStatus(status -> status.value() == 404,
                            (request, response) -> {
                                log.warn("CPF não encontrado no serviço de associados: cpf={}, status={}",
                                        mascararCpf(cpf), response.getStatusCode().value());
                             
                            })
                    .onStatus(HttpStatusCode::is5xxServerError,
                            (request, response) -> {
                                log.error("Serviço de associados retornou erro: cpf={}, status={}",
                                        mascararCpf(cpf), response.getStatusCode().value());
                                throw new ServiceUnavailableException("Serviço de validação de associado indisponível.");
                            })
                    .body(UserInfoResponse.class);
        	if(statusRequest.status().equals("404")) {
        		return new UserInfoResponse("UNABLE TO VOTE");
        	}else {
        		return new UserInfoResponse("ABLE TO VOTE");
        	}
        	
        } catch (BusinessException | ServiceUnavailableException ex) {
            throw ex;
        } catch (RestClientException ex) {
            log.error("Falha ao consultar serviço de associados: cpf={}", mascararCpf(cpf), ex);
            throw new ServiceUnavailableException("Não foi possível validar o associado no momento.");
        }
    }

    private String mascararCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) return "***";
        return "***.***.***-" + cpf.substring(9);
    }
}
