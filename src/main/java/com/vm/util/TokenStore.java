package com.vm.util;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class TokenStore {
    private Set<String> usedTokens = new HashSet<>();

    public boolean isTokenUsed(String token) {
        return usedTokens.contains(token);
    }

    public void markTokenAsUsed(String token) {
        usedTokens.add(token);
    }
}
