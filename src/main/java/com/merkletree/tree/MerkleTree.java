package com.merkletree.tree;

import lombok.Builder;
import lombok.Data;
import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Builder
@Data
@Component
public class MerkleTree
{
    private Set<String> dataPoints;

    private final Keccak.Digest256 digest256;

    public int getTotalNumberOfLeafs()
    {
        return 2 ^ (dataPoints.size()) - 1;
    }

    public Optional<String> calculateMerkleProof()
    {
        if (dataPoints.size() == 0)
        {
            return Optional.empty();
        }

        var dataPointHashes = getDataPointHashes();

        var parentHashes = getParentHashes(dataPointHashes);

        while (parentHashes.size() != 1)
        {
            parentHashes = getParentHashes(parentHashes);
        }

        return parentHashes.stream().findFirst();
    }

    /**
     * Returns the hex encoded Keccak 256 hash of data provided in String format.
     */
    private String hashData(String data)
    {
        if (data == null)
        {
            throw new IllegalArgumentException("Provided data is null");
        }

        var hashedBytes = digest256.digest(data.getBytes(StandardCharsets.UTF_8));

        return new String(Hex.encode(hashedBytes));
    }

    public List<String> getDataPointHashes()
    {
        var dataPointHashes = new ArrayList<String>();

        var numberOfFillUps = numberOfNullValuesToFillUp(dataPoints.size());

        dataPoints.forEach(dataPoint -> dataPointHashes.add(this.hashData(dataPoint)));

        if (numberOfFillUps != 0)
        {
            for(int i = 0; i<numberOfFillUps; i++)
            {
                dataPointHashes.add(this.hashData("DUMMY"));
            }
        }

        return dataPointHashes;
    }

    public int numberOfNullValuesToFillUp(int numberOfDataPoints)
    {
        if (numberOfDataPoints == 0)
        {
            return 0;
        }

        if (numberOfDataPoints == 1)
        {
            return 1;
        }

        int n = 0;

        while (Math.pow(2, n) < numberOfDataPoints)
        {
            n++;
        }

        return (int) (Math.pow(2, n) - numberOfDataPoints);
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

    public List<String> getParentHashes(List<String> dataPointHashes)
    {
        if (dataPointHashes == null)
        {
            throw new IllegalArgumentException("Provided hashes of data points are null");
        }

        var parentHashes = new ArrayList<String>();

        for (int i = 0; i < dataPointHashes.size(); i = i + 2)
        {
            var hash = getParentHash(dataPointHashes.get(i), dataPointHashes.get(i + 1));

            parentHashes.add(hash);
        }

        return parentHashes;
    }

    public List<Integer> getPrimeFactors(int number)
    {
        int n = number;

        var factors = new ArrayList<Integer>();

        for (int i = 2; i <= n / i; i++)
        {
            while (n % i == 0)
            {
                factors.add(i);
                n = n / i;
            }
        }
        if (n > 1)
        {
            factors.add(n);
        }
        return factors;
    }
}
