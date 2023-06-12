package com.merkletree.regular.model.nodes;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Table(name = "leaf_nodes")
@Entity
@SuperBuilder
@NoArgsConstructor
@Getter
@Setter
public class LeafNode extends AbstractNode
{
    @Column(name = "leaf_value")
    private String leafValue;
}
