package com.merkletree.hash;

import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.bouncycastle.util.encoders.Hex;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@DisplayName("Tests for the Hash Service")
@ExtendWith(MockitoExtension.class)
class HashServiceTest
{
    @InjectMocks
    HashService hashService;

    @Mock
    Keccak.Digest256 digest256;

    @Nested
    @DisplayName("Tests for hashing of arbitrary data")
    class HashData
    {
        @Test
        @DisplayName("Hashing of arbitrary data in String format works")
        void hashData_works()
        {
            // given
            var data = Instancio.create(String.class);

            var expectedHashedBytes = Instancio.create(byte[].class);

            var expectedResponse = new String(Hex.encode(expectedHashedBytes));

            given(digest256.digest(data.getBytes(StandardCharsets.UTF_8))).willReturn(expectedHashedBytes);

            // when
            var actualResponse = hashService.hashData(data);

            // then
            assertEquals(expectedResponse, actualResponse);
        }

        @Test
        @DisplayName("Illegal Argument Exception is thrown if null data is provided for hashing")
        void hashData_throws()
        {
            assertThrows(IllegalArgumentException.class, () -> hashService.hashData(null));
        }

    }

    @Nested
    @DisplayName("Tests for obtaining the parent hash of two inputs")
    class GetParentHash
    {
        @Test
        @DisplayName("Hashing of to inputs to receive the parent hash works")
        void getParentHash_works()
        {
            // given
            var leftHash = Instancio.create(String.class);

            var rightHash = Instancio.create(String.class);

            var expectedHashedBytes = Instancio.create(byte[].class);

            var expectedResponse = new String(Hex.encode(expectedHashedBytes));

            var data = leftHash + rightHash;

            given(digest256.digest(data.getBytes(StandardCharsets.UTF_8))).willReturn(expectedHashedBytes);

            // when
            var actualResponse = hashService.getParentHash(leftHash, rightHash);

            // then
            assertEquals(expectedResponse, actualResponse);
        }

        @ParameterizedTest
        @MethodSource("nullData")
        @DisplayName("Illegal Argument exception is thrown if any of the input arguments is null")
        void getParentHash_throws(String leftHash, String rightHash)
        {
            assertThrows(IllegalArgumentException.class, () -> hashService.getParentHash(leftHash, rightHash));
        }

        static Stream<Arguments> nullData()
        {
            return Stream.of(
                    Arguments.of(null, "String"),
                            Arguments.of("String", null),
                            Arguments.of(null, null)
            );
        }

    }




}