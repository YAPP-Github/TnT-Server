package com.tnt.application.member;

import static com.tnt.global.error.model.ErrorMessage.*;

import java.security.KeyFactory;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import org.springframework.stereotype.Component;

import com.auth0.jwt.interfaces.ECDSAKeyProvider;
import com.tnt.global.error.exception.OAuthException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppleEcdsaKeyProvider implements ECDSAKeyProvider {

	private String privateKey;
	private String keyId;

	public AppleEcdsaKeyProvider(String privateKey, String keyId) {
		this.privateKey = privateKey;
		this.keyId = keyId;
	}

	@Override
	public ECPublicKey getPublicKeyById(String keyId) {
		return null;  // 클라이언트 시크릿 생성에는 불필요
	}

	@Override
	public ECPrivateKey getPrivateKey() {
		try {
			byte[] pkcs8EncodedBytes = Base64.getDecoder().decode(privateKey);
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pkcs8EncodedBytes);
			KeyFactory kf = KeyFactory.getInstance("EC");
			return (ECPrivateKey)kf.generatePrivate(keySpec);
		} catch (Exception e) {
			log.error(FAILED_TO_FETCH_PRIVATE_KEY.getMessage(), e);

			throw new OAuthException(FAILED_TO_FETCH_PRIVATE_KEY);
		}
	}

	@Override
	public String getPrivateKeyId() {
		return keyId;
	}
}
