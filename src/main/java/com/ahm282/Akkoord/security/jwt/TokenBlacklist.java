package com.ahm282.Akkoord.security.jwt;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class TokenBlacklist {
    private final ConcurrentHashMap<String, Long> blacklistedTokens = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    public TokenBlacklist() {
        // Clean expired tokens every hour
        scheduler.scheduleAtFixedRate(this::removeExpiredTokens, 1, 1, TimeUnit.HOURS);
    }
    
    public void addToBlacklist(String token, Long expirationTime) {
        blacklistedTokens.put(token, expirationTime);
    }
    
    public boolean isBlacklisted(String token) {
        return blacklistedTokens.containsKey(token);
    }
    
    private void removeExpiredTokens() {
        long now = System.currentTimeMillis();
        Set<String> tokensToRemove = new HashSet<>();
        
        blacklistedTokens.forEach((token, expTime) -> {
            if (expTime < now) {
                tokensToRemove.add(token);
            }
        });
        
        tokensToRemove.forEach(blacklistedTokens::remove);
    }
}