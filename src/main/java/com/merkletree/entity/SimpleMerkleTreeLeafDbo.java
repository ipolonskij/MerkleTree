package com.merkletree.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Table(name = "simple_merkle_tree_leafs")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SimpleMerkleTreeLeafDbo
{
    @Id
    @GeneratedValue(generator = "uuid4", strategy = GenerationType.AUTO)
    @GenericGenerator(name = "UUID")
    @Column(name = "leaf_id", nullable = false, updatable = false)
    private UUID uuid;

    @Column(name = "leaf_index")
    private Integer key;

    @Column(name = "leaf_value")
    private String value;
}
