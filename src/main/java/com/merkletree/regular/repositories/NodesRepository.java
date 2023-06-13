package com.merkletree.regular.repositories;

import com.merkletree.regular.model.nodes.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NodesRepository extends JpaRepository<Node, UUID>
{
    Node findFirstByOrderByIndexDesc();

    List<Node> findAllByMerkleTree_Id(UUID merkleTreeId);

}
