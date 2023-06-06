package com.merkletree.tree;

import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.bouncycastle.util.encoders.Hex;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for the correct setup of the Merkle tree")
class MerkleTreeTest
{
    @InjectMocks
    MerkleTree merkleTree;

    @Mock
    Keccak.Digest256 keccak;

    Set<String> dataPoints;

    @BeforeEach
    void setUp()
    {
        dataPoints = Instancio.ofSet(String.class).create();
        merkleTree.setDataPoints(dataPoints);
    }

    @Nested
    @DisplayName("Test to correctly calculate the number of leafs of a merkle tree")
    class NumberOfLeafs
    {
        @Test
        void getNumberOfLeafs_works()
        {
            //given
            var expectedNumberOfLeafs = 2 ^ merkleTree.getDataPoints().size() - 1;

            // when
            var totalNumberOfLeafs = merkleTree.getTotalNumberOfLeafs();

            // then
            assertEquals(expectedNumberOfLeafs, totalNumberOfLeafs);
        }
    }

    @Nested
    @DisplayName("Tests for the hashes of the data points")
    class GetHashesForDataPoints
    {
        @ParameterizedTest
        @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15})
        @DisplayName("Calculation of hashes of data points works correctly")
        void getDataPointHashes_works(int numberOfDataPoints) throws NoSuchAlgorithmException
        {
            dataPoints = Instancio.ofSet(String.class).size(numberOfDataPoints).create();
            merkleTree.setDataPoints(dataPoints);

            lenient().when(keccak.digest(any(byte[].class))).thenAnswer(invocation ->
            {
                byte[] inputBytes = invocation.getArgument(0);
                return computeHash(inputBytes);
            });

            var numberOfFillUps = merkleTree.numberOfNullValuesToFillUp(dataPoints.size());

            // when
            var dataPointHashes = merkleTree.getDataPointHashes();

            // then
            assertEquals(numberOfDataPoints + numberOfFillUps, dataPointHashes.size());

            var dummyHash = new String(Hex.encode(computeHash("DUMMY".getBytes(StandardCharsets.UTF_8))));

            var numberOfDummyHashes = Collections.frequency(dataPointHashes, dummyHash);

            assertEquals(numberOfFillUps, numberOfDummyHashes);
        }
    }

    @Nested
    @DisplayName("Calculation of the parent hash from two child leafs works correctly")
    class GetParentHash
    {
        @Test
        @DisplayName("Retrieval of parent hash works")
        void getParentHash_works() throws NoSuchAlgorithmException
        {
            // given
            var stringLeft = "StringLeft";
            var stringRight = "StringRight";

            var expectedString = "StringLeftStringRight";

            var sha256Digest = MessageDigest.getInstance("SHA-256");

            var expectedHashedBytes = sha256Digest.digest(expectedString.getBytes(StandardCharsets.UTF_8));

            when(keccak.digest(any(byte[].class))).thenAnswer(invocation ->
            {
                byte[] inputBytes = invocation.getArgument(0);
                return computeHash(inputBytes);
            });

            // when
            var actualHash = merkleTree.getParentHash(stringLeft, stringRight);

            assertEquals(new String(Hex.encode(expectedHashedBytes)), actualHash);
        }

        @ParameterizedTest
        @MethodSource("provideNullValues")
        @DisplayName("Exception is thrown if input is null")
        void getParentHash_nullInput_throws(String leftHash, String rightHash)
        {
            assertThrows(IllegalArgumentException.class, () -> merkleTree.getParentHash(leftHash, rightHash));
        }

        static Stream<Arguments> provideNullValues()
        {
            return Stream.of(
                    Arguments.of("a", null),
                    Arguments.of(null, "2"),
                    Arguments.of(null, null)
            );
        }
    }

    @Nested
    @DisplayName("Tests to correctly receive parent hashes")
    class GetParentHashes
    {

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15})
        @DisplayName("Retrieval of correct amount of parent hashes works correctly")
        void getParentHashes_correctAmount_works(int numberOfDataPoints)
        {
            dataPoints = Instancio.ofSet(String.class).size(numberOfDataPoints).create();
            merkleTree.setDataPoints(dataPoints);

            when(keccak.digest(any(byte[].class))).thenAnswer(invocation ->
            {
                byte[] inputBytes = invocation.getArgument(0);
                return computeHash(inputBytes);
            });

            var dataPointHashes = merkleTree.getDataPointHashes();

            // when
            var parentHashes = merkleTree.getParentHashes(dataPointHashes);

            // then
            assertEquals(dataPointHashes.size() / 2, parentHashes.size());
        }

        @Test
        @DisplayName("Correct parent hash is returned for respective data point hashes")
        void getParentHashes_correctHash_works()
        {
            // given
            dataPoints = Instancio.ofSet(String.class).size(4).create();
            merkleTree.setDataPoints(dataPoints);

            when(keccak.digest(any(byte[].class))).thenAnswer(invocation ->
            {
                byte[] inputBytes = invocation.getArgument(0);
                return computeHash(inputBytes);
            });

            var dataPointHashes = merkleTree.getDataPointHashes();

            var firstExpectedParentHash = merkleTree.getParentHash(dataPointHashes.get(0), dataPointHashes.get(1));

            var secondExpectedParentHash = merkleTree.getParentHash(dataPointHashes.get(2), dataPointHashes.get(3));

            var expectedParentHashes = new ArrayList<String>();

            expectedParentHashes.add(firstExpectedParentHash);
            expectedParentHashes.add(secondExpectedParentHash);

            // when
            var parentHashes = merkleTree.getParentHashes(dataPointHashes);

            // then
            assertEquals(expectedParentHashes, parentHashes);
        }

        @Test
        @DisplayName("Error is thrown if provided data point hashes are null")
        void getParentHashes_nullInput_throws()
        {
            assertThrows(IllegalArgumentException.class, () -> merkleTree.getParentHashes(null));
        }
    }

    @Nested
    @DisplayName("Tests for the correct calculation of the merkle proof for a given set of data points")
    class CalculateMerkleProof
    {
        @ParameterizedTest
        @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15})
        @DisplayName("Calculation of a merkle proof works")
        void calculateMerkleProof_works(int numberOfDataPoints)
        {
            // given
            dataPoints = Instancio.ofSet(String.class).size(numberOfDataPoints).create();
            merkleTree.setDataPoints(dataPoints);

            lenient().when(keccak.digest(any(byte[].class))).thenAnswer(invocation ->
            {
                byte[] inputBytes = invocation.getArgument(0);
                return computeHash(inputBytes);
            });

            var merkleProof = merkleTree.calculateMerkleProof();

            if (numberOfDataPoints == 0)
            {
                assertTrue(merkleProof.isEmpty());
            }
            else
            {
                assertTrue(merkleProof.isPresent());
            }
        }
    }

    @Nested
    @DisplayName("Tests to obtain the prime factors for a given number")
    class GetPrimeFactors
    {
        @ParameterizedTest
        @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15})
        @DisplayName("Check if provided numbers are prime")
        void getPrimeFactors_works(int number)
        {
            var primeFactors = merkleTree.getPrimeFactors(number);

            primeFactors.forEach(primeFactor -> assertTrue(isPrime(primeFactor)));
        }

        public boolean isPrime(int num)
        {
            if(num<=1)
            {
                return false;
            }
            for(int i=2;i<=num/2;i++)
            {
                if((num%i)==0)
                    return  false;
            }
            return true;
        }
    }

    @Nested
    @DisplayName("Calculate the number of fill up data points for the merkle tree")
    class NumberOfNullValuesToFillUp
    {
        @ParameterizedTest
        @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15})
        @DisplayName("")
        void numberOfNullValuesToFillUp_works(int numberOfDataPoints)
        {
            var numberOfFillUps = merkleTree.numberOfNullValuesToFillUp(numberOfDataPoints);

            System.out.println("Number of data points: " + numberOfDataPoints + " number of fill ups: " +  numberOfFillUps);
            assertNotNull(numberOfFillUps);
        }
    }

    static byte[] computeHash(byte[] inputBytes) throws NoSuchAlgorithmException
    {
        MessageDigest sha256Digest = MessageDigest.getInstance("SHA-256");
        return sha256Digest.digest(inputBytes);
    }

}