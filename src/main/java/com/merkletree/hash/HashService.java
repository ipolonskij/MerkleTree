package com.merkletree.hash;

import lombok.RequiredArgsConstructor;
import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class HashService
{
    private final Keccak.Digest256 digest256;

    /**
     * Returns the hex encoded Keccak 256 hash of data provided in String format.
     */
    public String hashData(String data)
    {
        if (data == null)
        {
            throw new IllegalArgumentException("Provided data is null");
        }

        var hashedBytes = digest256.digest(data.getBytes(StandardCharsets.UTF_8));

        return new String(Hex.encode(hashedBytes));
    }

    public String getParentHash(String leftHash, String rightHash)
    {
        if (leftHash == null || rightHash == null)
        {
            throw new IllegalArgumentException("Provided data is null");
        }

        var joinedData = leftHash + rightHash;

        var hashedBytes = digest256.digest(joinedData.getBytes(StandardCharsets.UTF_8));

        return new String(Hex.encode(hashedBytes));
    }

    public List<String> getLeafHashes(Map<Integer, String> leafs)
    {
        if (leafs == null)
        {
            throw new IllegalArgumentException("Provided leafs are null");

        }

        var leafHashes = new ArrayList<String>();

        leafs.values().forEach( leaf -> leafHashes.add(this.hashData(leaf)));

        return leafHashes;
    }

    public List<String> getParentHashes(List<String> leafHashes)
    {
        if (leafHashes == null)
        {
            throw new IllegalArgumentException("Provided hashes of data points are null");
        }

        var parentHashes = new ArrayList<String>();

        for (int i = 0; i < leafHashes.size(); i = i + 2)
        {
            var hash = this.getParentHash(leafHashes.get(i), leafHashes.get(i + 1));

            parentHashes.add(hash);
        }

        return parentHashes;
    }

    public String calculateMerkleProof(Map<Integer, String> leafs)
    {
        var leafHashes = getLeafHashes(leafs);

        var parentHashes = getParentHashes(leafHashes);

        while (parentHashes.size() != 1)
        {
            parentHashes = getParentHashes(parentHashes);
        }

        return parentHashes.stream().findFirst().orElseThrow();
    }
}
