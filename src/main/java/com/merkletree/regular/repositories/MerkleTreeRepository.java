package com.merkletree.regular.repositories;

import com.merkletree.regular.model.MerkleTree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MerkleTreeRepository extends JpaRepository<MerkleTree, UUID>
{
}
