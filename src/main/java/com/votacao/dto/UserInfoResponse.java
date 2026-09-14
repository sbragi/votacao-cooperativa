package com.votacao.dto;


public record UserInfoResponse(String status) {
	
    public boolean canVote() {
        return "ABLE_TO_VOTE".equalsIgnoreCase(status);
    }
}
