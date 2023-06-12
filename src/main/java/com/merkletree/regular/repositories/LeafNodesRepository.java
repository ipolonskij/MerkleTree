package com.merkletree.regular.repositories;

import com.merkletree.regular.model.nodes.LeafNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LeafNodesRepository extends JpaRepository<LeafNode, UUID>
{
    LeafNode findLeafNodeByIndex(Integer index);
}
