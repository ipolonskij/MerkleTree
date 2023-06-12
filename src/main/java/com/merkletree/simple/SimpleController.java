package com.merkletree.simple;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class SimpleController
{
    private final TreeService treeService;

    @PostMapping("/simple-merkle-tree")
    public void createSimpleMerkleTree()
    {
        treeService.createSimpleMerkleTree();
    }

    @GetMapping("/simple-merkle-tree")
    public String getMerkleProof()
    {
        return treeService.getMerkleProof();
    }

    @PutMapping("/simple-merkle-tree")
    public void updateLeafSimpleMerkleTree(@RequestParam Integer key, @RequestParam String value)
    {
        treeService.updateLeaf(key, value);
    }

}
