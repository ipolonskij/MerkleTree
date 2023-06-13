package com.merkletree.regular.repositories;

import com.merkletree.regular.model.nodes.LeafNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LeafNodesRepository extends JpaRepository<LeafNode, UUID>
{
    LeafNode findLeafNodeByIndex(Integer index);

    LeafNode findLeafNodeByIndexAndMerkleTree_Id(Integer integer, UUID merkleTreeId);

    List<LeafNode> findAllByMerkleTree_Id(UUID merkleTreeId);
}
