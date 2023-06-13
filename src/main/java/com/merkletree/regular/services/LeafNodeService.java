package com.merkletree.regular.services;

import com.merkletree.hash.HashService;
import com.merkletree.regular.model.nodes.LeafNode;
import com.merkletree.regular.repositories.LeafNodesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeafNodeService
{
    private final LeafNodesRepository leafNodesRepository;

    private final HashService hashService;

    public List<LeafNode> getLeafNodes(UUID merkleTreeId)
    {
        return leafNodesRepository.findAllByMerkleTree_Id(merkleTreeId);
    }

    public void updateLeafValue(UUID merkleTreeId,Integer leafIndex, String leafValue)
    {
        var leafNode = leafNodesRepository.findLeafNodeByIndexAndMerkleTree_Id(leafIndex, merkleTreeId);

        leafNode.setLeafValue(leafValue);

        leafNode.setHashValue(hashService.hashData(leafValue));

        leafNodesRepository.save(leafNode);
    }
}
