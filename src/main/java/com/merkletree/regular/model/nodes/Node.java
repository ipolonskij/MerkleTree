package com.merkletree.regular.model.nodes;

import com.merkletree.regular.model.MerkleTree;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Table(name = "node")
@Entity
@SuperBuilder
@NoArgsConstructor
@Getter
@Setter
public class Node extends AbstractNode
{
    @ElementCollection
    @Column(name = "child_nodes")
    private List<Integer> childNodes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merkle_tree_id")
    private MerkleTree merkleTree;
}
