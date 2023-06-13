package com.merkletree.regular.controller;

import com.merkletree.model.LeafNodesDto;
import com.merkletree.regular.mapper.NodesMapper;
import com.merkletree.regular.services.MerkleTreeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import com.merkletree.api.MerkleTreeApi;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RegularController implements MerkleTreeApi
{
    private final MerkleTreeService merkleTreeService;

    private final NodesMapper nodesMapper;

    @Override
    public ResponseEntity<UUID> createMerkleTree(LeafNodesDto leafNodesDto)
    {
        var merkleTreeId = merkleTreeService.createMerkleTree(leafNodesDto);

        return new ResponseEntity<>(merkleTreeId, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<String> getMerkleProof(UUID merkleTreeId)
    {
        var merkleProof = merkleTreeService.getMerkleProof();

        return new ResponseEntity<>(merkleProof, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Map<String, String>> getMerkleTree(UUID merkleTreeId)
    {
        var merkleTreeNodes = merkleTreeService.getMerkleTree(merkleTreeId);

        var merkleTreeNodesDto = nodesMapper.nodeDboToDto(merkleTreeNodes);

        return new ResponseEntity<>(merkleTreeNodesDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<String>> getProofOfMembership(UUID merkleTreeId, Integer leafIndex, String leafValue)
    {
        var proofOfMembership = merkleTreeService.getProofOfMembership(merkleTreeId, leafIndex, leafValue);

        return new ResponseEntity<>(proofOfMembership, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> updateLeaf(UUID merkleTreeId, LeafNodesDto leafNodesDto)
    {
        merkleTreeService.updateLeaf(merkleTreeId, leafNodesDto);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
