package com.ikindle.repository;

import com.ikindle.entity.UserOAuthSession;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserOAuthSessionRepository extends BaseRepository<UserOAuthSession, Long> {

    Optional<UserOAuthSession> findBySessionKey(String sessionKey);

    Optional<UserOAuthSession> findByProviderAndProviderUserId(String provider, String providerUserId);

    Optional<UserOAuthSession> findByUserId(Long userId);
}
