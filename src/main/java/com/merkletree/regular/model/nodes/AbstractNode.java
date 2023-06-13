package com.merkletree.regular.model.nodes;

import com.merkletree.regular.model.MerkleTree;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;

import java.util.Objects;
import java.util.UUID;

@MappedSuperclass
@SuperBuilder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractNode
{
    @Id
    @GeneratedValue(generator = "uuid4", strategy = GenerationType.AUTO)
    @GenericGenerator(name = "UUID")
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name ="node_index")
    private int index;

    @Column(name ="node_hash_value")
    private String hashValue;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "merkle_tree_id")
    private MerkleTree merkleTree;

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractNode that = (AbstractNode) o;
        return index == that.index && Objects.equals(id, that.id) && Objects.equals(hashValue, that.hashValue) && Objects.equals(merkleTree, that.merkleTree);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, index, hashValue, merkleTree);
    }
}
