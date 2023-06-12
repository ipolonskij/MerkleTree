package com.merkletree.regular.services;

import com.merkletree.regular.model.nodes.Node;
import com.merkletree.regular.repositories.NodesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NodesService
{
    private final NodesRepository nodesRepository;

    public List<Node> getNodes()
    {
        return nodesRepository.findAll();
    }

    public Node getRootNode()
    {
        return nodesRepository.findFirstByOrderByIndexDesc();
    }
}
