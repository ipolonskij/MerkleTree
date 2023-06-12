package com.merkletree.regular.services;

import com.merkletree.hash.HashService;
import com.merkletree.model.LeafNodesDto;
import com.merkletree.regular.model.MerkleTree;
import com.merkletree.regular.model.nodes.AbstractNode;
import com.merkletree.regular.model.nodes.LeafNode;
import com.merkletree.regular.model.nodes.Node;
import com.merkletree.regular.repositories.MerkleTreeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MerkleTreeService
{
    private final MerkleTreeRepository merkleTreeRepository;

    private final LeafNodeService leafNodeService;

    private final NodesService nodesService;

    private final HashService hashService;

    public void createMerkleTree(LeafNodesDto leafNodesDto)
    {
        var leafNodes = createLeafNodes(leafNodesDto);

        createMerkleTree(leafNodes);
    }

    private void createMerkleTree(List<LeafNode> leafNodes)
    {
        var parentNodes = getNodes(leafNodes);

        var completeNodeList = new ArrayList<>(parentNodes);

        while (parentNodes.size() != 1)
        {
            parentNodes = getNodes(parentNodes);

            completeNodeList.addAll(parentNodes);
        }

        var merkleTree = MerkleTree.builder()
                .leafNodes(leafNodes)
                .nodes(completeNodeList)
                .build();

        leafNodes.forEach(leafNode -> leafNode.setMerkleTree(merkleTree));

        completeNodeList.forEach(node -> node.setMerkleTree(merkleTree));

        merkleTreeRepository.save(merkleTree);
    }

    public Map<Integer, String> getMerkleTree()
    {
        var leafNodes = leafNodeService.getLeafNodes();

        var nodes = nodesService.getNodes();

        var merkleTreeNodes = new HashMap<Integer, String>();

        leafNodes.forEach(
                leafNode -> merkleTreeNodes.put(leafNode.getIndex(), leafNode.getHashValue()));

        nodes.forEach(
                node -> merkleTreeNodes.put(node.getIndex(), node.getHashValue()));

        return merkleTreeNodes;
    }

    public void updateLeaf(Integer leafIndex, String leafValue)
    {
        leafNodeService.updateLeafValue(leafIndex, leafValue);

        var leafNodes = leafNodeService.getLeafNodes();

        createMerkleTree(leafNodes);
    }

    public String getMerkleProof()
    {
        var node = nodesService.getRootNode();

        return node.getHashValue();
    }

    public List<String> getProofOfMembership(Integer proofLeafIndex, String proofLeafValue)
    {
        var leafNodes = leafNodeService.getLeafNodes();

        var containsLeafNode =  leafNodes.stream().anyMatch(
                leafNode -> leafNode.getIndex() == proofLeafIndex && leafNode.getLeafValue().equals(proofLeafValue));

        if (!containsLeafNode)
        {
            throw new IllegalArgumentException("No leaf with provided index or leaf value present");
        }

        var merkleTree = getMerkleTree();

        var proofOfMembership = new ArrayList<String>();

        int levelOffset = 0;
        int leafIndex = proofLeafIndex;

        for (int levelSize = leafNodes.size(); levelSize > 1; levelSize = (levelSize+1)/2)
        {
            int sibilingNodeIndex  = (leafIndex % 2 == 0) ? leafIndex + 1 : leafIndex -1;

            if (sibilingNodeIndex < levelSize)
            {
                int nodeIndex = levelOffset + sibilingNodeIndex;
                proofOfMembership.add(merkleTree.get(nodeIndex));
            }

            leafIndex /=2;

            levelOffset += levelSize;
        }

        return proofOfMembership;
    }

    private List<LeafNode> createLeafNodes(LeafNodesDto leafNodesDto)
    {
        var numberOfLeafNodes = leafNodesDto.getDataPoints().size();

        var primeFactorsOfLeafNodes = getPrimeFactors(numberOfLeafNodes);

        var containsValueNotTwo = primeFactorsOfLeafNodes.stream().anyMatch(number -> number != 2);

        if (containsValueNotTwo)
        {
            throw new IllegalArgumentException("Number of leaf nodes is not binary");
        }

        var leafNodesDbo = new ArrayList<LeafNode>();

        leafNodesDto.getDataPoints().forEach(
                (index, value) -> leafNodesDbo.add(
                        LeafNode.builder()
                                .index(Integer.parseInt(index))
                                .leafValue(value)
                                .hashValue(hashService.hashData(value))
                                .build()));
        return leafNodesDbo;
    }

    private static List<Integer> getPrimeFactors(int number)
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

    private ArrayList<Node> getNodes(List<? extends AbstractNode> nodes)
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
}
