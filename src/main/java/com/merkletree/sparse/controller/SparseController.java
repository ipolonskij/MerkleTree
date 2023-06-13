package com.merkletree.sparse.controller;

import com.merkletree.model.LeafNodesDto;
import com.merkletree.sparse.service.SparseMerkleTreeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class SparseController implements com.merkletree.api.SparseMerkleTreeApi
{
    private final SparseMerkleTreeService sparseMerkleTreeService;

    @Override
    public ResponseEntity<UUID> createSparseMerkleTree(LeafNodesDto leafNodesDto)
    {
        var merkelTreeId = sparseMerkleTreeService.createMerkleTree(leafNodesDto);

        return new ResponseEntity<>(merkelTreeId, HttpStatus.CREATED);
    }
}
