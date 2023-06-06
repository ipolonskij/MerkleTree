package com.merkletree.hash;

import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HashConfiguration
{
    @Bean
    public Keccak.Digest256 keccakDigest256()
    {
        return new Keccak.Digest256();
    }
}
