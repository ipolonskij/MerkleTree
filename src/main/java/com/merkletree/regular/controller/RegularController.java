package com.merkletree.regular.controller;

import com.merkletree.model.LeafNodesDto;
import com.merkletree.regular.mapper.NodesMapper;
import com.merkletree.regular.services.MerkleTreeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import com.merkletree.api.MerkleTreeApi;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class RegularController implements MerkleTreeApi
{
    private final MerkleTreeService merkleTreeService;

    private final NodesMapper nodesMapper;

    @Override
    public ResponseEntity<Void> createMerkleTree(LeafNodesDto leafNodesDto)
    {
        merkleTreeService.createMerkleTree(leafNodesDto);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Map<String, String>> getMerkleTree()
    {
        var merkleTreeNodes = merkleTreeService.getMerkleTree();

        var merkleTreeNodesDto = nodesMapper.nodeDboToDto(merkleTreeNodes);

        return new ResponseEntity<>( merkleTreeNodesDto,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<String>> getProofOfMembership(Integer leafIndex, String leafValue)
    {
        var proofOfMembership = merkleTreeService.getProofOfMembership(leafIndex, leafValue);

        return new ResponseEntity<>(proofOfMembership, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> updateLeaf(Integer leafIndex, String leafValue)
    {
        merkleTreeService.updateLeaf(leafIndex, leafValue);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> getMerkleProof()
    {
        var merkleProof = merkleTreeService.getMerkleProof();

        return new ResponseEntity<>(merkleProof, HttpStatus.OK);
    }
}
