package com.merkletree.simple;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LeafRepository extends JpaRepository<LeafDbo, UUID>
{

    LeafDbo findLeafsDboByKey(Integer key);

}
