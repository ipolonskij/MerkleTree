package com.merkletree.regular.services;

import com.merkletree.common.AbstractMerkleTreeService;
import com.merkletree.hash.HashService;
import com.merkletree.regular.model.nodes.LeafNode;
import com.merkletree.regular.repositories.MerkleTreeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class MerkleTreeService extends AbstractMerkleTreeService
{
    @Autowired
    public MerkleTreeService(MerkleTreeRepository merkleTreeRepository, LeafNodeService leafNodeService, NodesService nodesService, HashService hashService)
    {
        super(merkleTreeRepository, leafNodeService, nodesService, hashService);
    }

    @Override
    public List<LeafNode> createLeafNodes(com.merkletree.model.LeafNodesDto leafNodesDto)
    {
        verifyMerkleTreeRequest(leafNodesDto);

        var leafNodesDbo = new ArrayList<LeafNode>();

        leafNodesDto.getDataPoints().forEach(
                (index, value) -> leafNodesDbo.add(
                        LeafNode.builder()
                                .index(Integer.parseInt(index))
                                .leafValue(value)
                                .hashValue(hashService.hashData(value))
                                .build()));
        return leafNodesDbo;
    }
}
