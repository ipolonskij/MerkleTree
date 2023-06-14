package com.merkletree.common;

import com.merkletree.model.LeafNodesDto;
import com.merkletree.hash.HashService;
import com.merkletree.regular.model.MerkleTree;
import com.merkletree.regular.model.nodes.AbstractNode;
import com.merkletree.regular.model.nodes.LeafNode;
import com.merkletree.regular.model.nodes.Node;
import com.merkletree.regular.repositories.MerkleTreeRepository;
import com.merkletree.regular.services.LeafNodeService;
import com.merkletree.regular.services.NodesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public abstract class AbstractMerkleTreeService
{
    protected final MerkleTreeRepository merkleTreeRepository;

    protected final LeafNodeService leafNodeService;

    protected final NodesService nodesService;

    protected final HashService hashService;

    public void verifyMerkleTreeRequest(LeafNodesDto leafNodesDto)
    {
        var numberOfLeafNodes = leafNodesDto.getDataPoints().size();

        var primeFactorsOfLeafNodes = getPrimeFactors(numberOfLeafNodes);

        var containsValueNotTwo = primeFactorsOfLeafNodes.stream().anyMatch(number -> number != 2);

        if (containsValueNotTwo)
        {
            throw new IllegalArgumentException("Number of leaf nodes is not binary");
        }
    }

    public static List<Integer> getPrimeFactors(int number)
    {
        var primeFactors = new ArrayList<Integer>();

        // Divide the number by 2 until it is divisible
        while (number % 2 == 0)
        {
            primeFactors.add(2);
            number /= 2;
        }

        // Check for prime factors starting from 3
        for (int i = 3; i <= Math.sqrt(number); i += 2)
        {
            while (number % i == 0)
            {
                primeFactors.add(i);
                number /= i;
            }
        }

        // If the remaining number is greater than 2, it is also a prime factor
        if (number > 2)
        {
            primeFactors.add(number);
        }

        return primeFactors;

    }

    public ArrayList<Node> getNodes(List<? extends AbstractNode> nodes)
    {
        if (nodes == null)
        {
            throw new IllegalArgumentException("Provided leaf node hashes are null");
        }

        var parentNodes = new ArrayList<Node>();

        for (int i = 0; i < nodes.size(); i += 2)
        {
            var parentNode = new Node();

            // Add child nodes to parent node
            parentNode.setChildNodes(Arrays.asList(nodes.get(i).getIndex(), nodes.get(i + 1).getIndex()));

            // Set the hash value of the node, calculated from the hash value of the respective child nodes
            parentNode.setHashValue(hashService.getParentHash(nodes.get(i).getHashValue(), nodes.get(i + 1).getHashValue()));

            // set the node's index
            int index;
            if (parentNodes.isEmpty())
            {
                index = nodes.stream().max(Comparator.comparingInt(AbstractNode::getIndex)).orElseThrow().getIndex() + 1;

            } else
            {
                index = parentNodes.stream().max(Comparator.comparingInt(AbstractNode::getIndex)).orElseThrow().getIndex() + 1;

            }
            parentNode.setIndex(index);

            // add node to parent nodes list
            parentNodes.add(parentNode);
        }

        return parentNodes;
    }

    public Map<Integer, String> getMerkleTree(UUID merkleTreeId)
    {
        var leafNodes = leafNodeService.getLeafNodes(merkleTreeId);

        var nodes = nodesService.getNodes(merkleTreeId);

        var merkleTreeNodes = new HashMap<Integer, String>();

        leafNodes.forEach(
                leafNode -> merkleTreeNodes.put(leafNode.getIndex(), leafNode.getHashValue()));

        nodes.forEach(
                node -> merkleTreeNodes.put(node.getIndex(), node.getHashValue()));

        return merkleTreeNodes;
    }

    public String getMerkleProof()
    {
        var node = nodesService.getRootNode();

        return node.getHashValue();
    }

    public List<String> getProofOfMembership(UUID merkleTreeId, Integer proofLeafIndex, String proofLeafValue)
    {
        var leafNodes = leafNodeService.getLeafNodes(merkleTreeId);

        verifyProofOfMembershipLeaf(leafNodes, proofLeafIndex, proofLeafValue);

        var merkleTree = getMerkleTree(merkleTreeId);

        var proofOfMembership = new ArrayList<String>();

        int levelOffset = 0;
        int leafIndex = proofLeafIndex;

        for (int levelSize = leafNodes.size(); levelSize > 1; levelSize = (levelSize + 1) / 2)
        {
            int sibilingNodeIndex = (leafIndex % 2 == 0) ? leafIndex + 1 : leafIndex - 1;

            if (sibilingNodeIndex < levelSize)
            {
                int nodeIndex = levelOffset + sibilingNodeIndex;
                proofOfMembership.add(merkleTree.get(nodeIndex));
            }

            leafIndex /= 2;

            levelOffset += levelSize;
        }

        return proofOfMembership;
    }

    public abstract void verifyProofOfMembershipLeaf(List<LeafNode> leafNodes, Integer proofLeafIndex, String proofLeafValue);

    public UUID createMerkleTree(List<LeafNode> leafNodes, UUID merkleTreeId)
    {
        var parentNodes = getNodes(leafNodes);

        var completeNodeList = new ArrayList<>(parentNodes);

        while (parentNodes.size() != 1)
        {
            parentNodes = getNodes(parentNodes);

            completeNodeList.addAll(parentNodes);
        }

        MerkleTree merkleTree;

        if (merkleTreeId == null)
        {
            merkleTree = MerkleTree.builder()
                    .leafNodes(leafNodes)
                    .nodes(completeNodeList)
                    .build();

            leafNodes.forEach(leafNode -> leafNode.setMerkleTree(merkleTree));

            completeNodeList.forEach(node -> node.setMerkleTree(merkleTree));
        } else
        {
            merkleTree = merkleTreeRepository.findById(merkleTreeId).orElseThrow();

            completeNodeList.forEach(node -> node.setMerkleTree(merkleTree));

            merkleTree.getLeafNodes().addAll(leafNodes);

            merkleTree.getNodes().forEach(node ->
                    completeNodeList.stream()
                            .filter(completeNode -> completeNode.getIndex() == node.getIndex())
                            .findFirst()
                            .ifPresent(completeNode -> node.setHashValue(completeNode.getHashValue())));
        }

        var savedMerkleTree = merkleTreeRepository.save(merkleTree);

        return savedMerkleTree.getId();

    }

    public UUID updateLeaf(UUID merkleTreeId, com.merkletree.model.LeafNodesDto leafNodesDto)
    {
        leafNodesDto.getDataPoints().forEach((index, leafValue) ->
        {
            leafNodeService.updateLeafValue(merkleTreeId, Integer.parseInt(index), leafValue);
        });

        var leafNodes = leafNodeService.getLeafNodes(merkleTreeId);

        return createMerkleTree(leafNodes, merkleTreeId);
    }

    public UUID createMerkleTree(com.merkletree.model.LeafNodesDto leafNodesDto)
    {
        var leafNodes = createLeafNodes(leafNodesDto);

        return createMerkleTree(leafNodes, null);
    }

    public abstract List<LeafNode> createLeafNodes(com.merkletree.model.LeafNodesDto leafNodesDto);


}
