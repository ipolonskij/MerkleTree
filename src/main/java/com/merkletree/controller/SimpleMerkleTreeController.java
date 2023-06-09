package com.merkletree.controller;

import com.merkletree.service.SimpleMerkleTreeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class SimpleMerkleTreeController
{
    private final SimpleMerkleTreeService simpleMerkleTreeService;

    @PostMapping("/simple-merkle-tree")
    public void createSimpleMerkleTree()
    {
        simpleMerkleTreeService.createSimpleMerkleTree();
    }

    @GetMapping("/simple-merkle-tree")
    public String getMerkleProof()
    {
        return simpleMerkleTreeService.getMerkleProof();
    }

    @PutMapping("/simple-merkle-tree")
    public void updateLeafSimpleMerkleTree(@RequestParam Integer key, @RequestParam String value)
    {
        simpleMerkleTreeService.updateLeaf(key, value);
    }

}
