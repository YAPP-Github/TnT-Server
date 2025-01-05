package com.tnt.domain.auth;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SessionInfo {

	private String status;  // login/logout
	private LocalDateTime lastAccessTime;
	private String userAgent;
	private String clientIp;
}