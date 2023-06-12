package com.merkletree.regular.model;

import com.merkletree.regular.model.nodes.LeafNode;
import com.merkletree.regular.model.nodes.Node;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Table(name = "merkle_tree")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class MerkleTree
{
    @Id
    @GeneratedValue(generator = "uuid4", strategy = GenerationType.AUTO)
    @GenericGenerator(name = "UUID")
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "merkleTree", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LeafNode> leafNodes = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "merkleTree", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Node> nodes = new ArrayList<>();

    public int getNumberOfNodes()
    {
        return (2*(leafNodes.size()) -1);
    }

}
