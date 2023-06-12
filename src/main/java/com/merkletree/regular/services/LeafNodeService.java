package com.merkletree.regular.services;

import com.merkletree.hash.HashService;
import com.merkletree.regular.model.nodes.LeafNode;
import com.merkletree.regular.repositories.LeafNodesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeafNodeService
{
    private final LeafNodesRepository leafNodesRepository;

    private final HashService hashService;

    public List<LeafNode> getLeafNodes()
    {
        return leafNodesRepository.findAll();
    }

    public void updateLeafValue(Integer leafIndex, String leafValue)
    {
        var leafNode = leafNodesRepository.findLeafNodeByIndex(leafIndex);

        leafNode.setLeafValue(leafValue);

        leafNode.setHashValue(hashService.hashData(leafValue));

        leafNodesRepository.save(leafNode);
    }

}
