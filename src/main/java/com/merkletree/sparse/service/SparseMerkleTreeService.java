package com.merkletree.sparse.service;

import com.merkletree.common.AbstractMerkleTreeService;
import com.merkletree.hash.HashService;
import com.merkletree.regular.model.MerkleTree;
import com.merkletree.regular.model.nodes.AbstractNode;
import com.merkletree.regular.model.nodes.LeafNode;
import com.merkletree.regular.model.nodes.Node;
import com.merkletree.regular.repositories.MerkleTreeRepository;
import com.merkletree.regular.services.LeafNodeService;
import com.merkletree.regular.services.NodesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.merkletree.model.LeafNodesDto;

import java.util.*;

@Service
@Slf4j
public class SparseMerkleTreeService extends AbstractMerkleTreeService
{
    @Autowired
    public SparseMerkleTreeService(MerkleTreeRepository merkleTreeRepository, LeafNodeService leafNodeService, NodesService nodesService, HashService hashService)
    {
        super( merkleTreeRepository,  leafNodeService,  nodesService,  hashService);
    }



    @Override
    public List<LeafNode> createLeafNodes(LeafNodesDto leafNodesDto)
    {
        verifyMerkleTreeRequest(leafNodesDto);

        var leafNodesDbo = new ArrayList<LeafNode>();

        // Create leaf nodes with values
        leafNodesDto.getDataPoints().forEach(
                (index, value) -> leafNodesDbo.add(
                        LeafNode.builder()
                                .index(Integer.parseInt(index))
                                .leafValue(value)
                                .hashValue(hashService.hashData(value))
                                .build()));

        for (int i = 0; i < leafNodesDto.getSize(); i++)
        {
            if (!leafNodesDto.getDataPoints().containsKey(String.valueOf(i)))
            {
                leafNodesDbo.add(
                        LeafNode.builder()
                                .index(i)
                                .leafValue("DUMMY")
                                .hashValue(hashService.hashData("DUMMY"))
                                .build());
            }
        }

        return leafNodesDbo;
    }
}
