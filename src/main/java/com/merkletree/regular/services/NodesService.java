package com.merkletree.regular.services;

import com.merkletree.regular.model.nodes.Node;
import com.merkletree.regular.repositories.NodesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NodesService
{
    private final NodesRepository nodesRepository;

    public List<Node> getNodes(UUID merkleTreeId)
    {
        return nodesRepository.findAllByMerkleTree_Id(merkleTreeId);
    }

    public Node getRootNode()
    {
        return nodesRepository.findFirstByOrderByIndexDesc();
    }

    public void saveNodes(List<Node> nodes)
    {
        nodesRepository.saveAll(nodes);
    }
}
