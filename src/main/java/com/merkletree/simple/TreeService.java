package com.merkletree.simple;

import com.merkletree.hash.HashService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TreeService
{
    private final LeafRepository leafRepository;

    private final HashService hashService;

    public void createSimpleMerkleTree()
    {
        var merkleTree = new HashMap<Integer, String>();

        merkleTree.put(0, "zero");
        merkleTree.put(1, "one");
        merkleTree.put(2, "two");
        merkleTree.put(3, "three");
        merkleTree.put(4, "four");
        merkleTree.put(5, "five");
        merkleTree.put(6, "six");
        merkleTree.put(7, "seven");

        for (Map.Entry<Integer, String> entry : merkleTree.entrySet())
        {
            var leaf = new LeafDbo();

            leaf.setKey(entry.getKey());
            leaf.setValue(entry.getValue());

            leafRepository.save(leaf);
        }
    }

    public String getMerkleProof()
    {
        var leafs = leafRepository.findAll();

        var leafMap = new HashMap<Integer, String>();

        leafs.forEach(
                leaf -> leafMap.put(leaf.getKey(), leaf.getValue())
        );

        return hashService.calculateMerkleProof(leafMap);
    }

    public void updateLeaf(Integer key, String value)
    {
        var leaf = leafRepository.findLeafsDboByKey(key);

        leaf.setValue(value);

        leafRepository.save(leaf);
    }
}