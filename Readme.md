# Merkle Tree

## Introduction

A [Merkle Tree](http://www.ralphmerkle.com/papers/Protocols.pdf) is a binary hash graph where every "leaf" represents a
data element. The leaf nodes are hashed in pairs and the resulting hashes are combined in pairs and hashed again. This
process is recursively repeated until a single hash value is obtained at the root of the tree. This concept is named
after Ralph Merkle who patented it in 1979.

The key property of a Merkle Tree is that the root hash represents a compact and secure summary of all data contained
within the tree. Therefore, Merkle Trees became widely used in cryptography and computer science. Its two main
applications are verifiability and scalability. E.g. by comparing two root hashes - instead of the entire data sets -
one can efficiently verify if two data structures contain the same data (in the same order) or if any individual data
element within the tree has been tampered. It is not possible to derive from the root hash of a Merkle Tree its
contained elements due to the nature of hash functions. Thus, Merkle Trees can be utilized as well to preserve privacy.

Merkle Trees became gained popularity for their application in Blockchains and in particular in Bitcoin since their
mentioning in the [Bitcoin Whitepaper](https://bitcoin.org/bitcoin.pdf). Variants of Merkle Trees are also used
in [Ethereum](https://ethereum.org/en/),
[Git](https://git-scm.com/) and some distributed NoSQL databases.

## Challenge

This project presents a solution to a challenge that was posed, showcasing the implementation of Merkle Trees. Due to
the broad task, some assumptions and choices were made for the solution.

### Technology Stack

The only technical requirements to run the Merkle Tree application is Java 17. It was developed using the
[Eclipse Temurin JDK 17.0.7](https://adoptium.net/de/temurin/releases/).

The Merkle Tree is implemented as a web server
using [Spring Boot 3.1.0](https://spring.io/blog/2023/05/18/spring-boot-3-1-0-available-now)
with a [H2 in memory database](https://www.h2database.com/html/main.html)
and [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html). In addition other commonly used
libraries, such as [OpenAPI](https://www.openapis.org/), [Lombok](https://projectlombok.org/)
and [MapStruct](https://mapstruct.org/), were used for code auto-generation.

The application can be started using the command `./gradlew bootRun` or `./gradlew.bat bootRun` in dependence on your
operating system from a shell. The application starts on localhost on port 8080. The H2 database is available under the
URL http://localhost:8080/h2-console/ with the credentials:

* username: sa
* password: password

a client application like [Postman](https://www.postman.com/) can be used in order to interact with the application via
its API.

### Cryptography

[Keccak-256](https://keccak.team/index.html), which
is [used in many places in Ethereum](https://github.com/ethereumbook/ethereumbook/blob/develop/04keys-addresses.asciidoc#ethereums-cryptographic-hash-function-keccak-256)
, was chosen to generate the hashes for the Merkle Tree. It provides
a [security level of 128 bits](https://keccak.team/keccak_specs_summary.html). For this purpose the
library [Bouncycastle](https://www.bouncycastle.org/) was used.

### Key Features

The implementation is provided in 3 variants:

* Simple
* Regular
* Sparse

For consistency, for all variants a data model is assumed such that the Merkle Trees consist of _Leaf Nodes_ and _Nodes_
, which have the following properties:

|       | Leaf Nodes         | Nodes              |
| ------- | -------------------- | -------------------- |
| Index | :white_check_mark: | :white_check_mark: |
| Value | :white_check_mark: |                    |
| Hash  | :white_check_mark: | :white_check_mark: |

A detailed description of the API for the regular and sparse variants is provided in
the [API definition](src/main/resources/api.yaml).

#### Simple

`Simple` is a first implementation of the Merkle Tree allowing to create a pre-defined Merkle Tree with 8 leaf nodes,
retrieve the root hash of the Merkle Tree as well as update single leaf values.

The respective `POST`, `GET` and `PUT` endpoints are available under http://localhost:8080/simple-merkle-tree.

#### Regular

The regular variant allows:

* The creation of Merkle Trees with arbitrary size and data that can be provided. Thereby, the user can define the leaf
  nodes and subsequently the Merkle Tree is created.
* To retrieve the Merkle Tree, receiving a list of hashes with the respective node indices.
* Obtain a Proof of Membership for a specific leaf index and leaf value, receiving a list of necessary hashes to
  calculate (outside of the Merkle Tree application) the root hash and thereby verify the membership of this particular
  leaf value, without getting information on other data stored in the Merkle Tree.
* Update leaf values. Subsequently, the hash of the leaf node and of all necessary parent node is updated.

#### Sparse

The sparse variant is an extension of the regular variant. Most of the functionality can be recycled from the regular
variant, therefore the variants share a lot of the code. Separate API endpoints are only provided for the creation of
the Merkle Tree and the update of the leafes. The sparse variant allows - as the name indicates - to create Merkle Trees
that are not fully populated. Unpopulated leaf nodes receive the default value `"DUMMY"`. Leaf nodes with a dummy value
can be updated, but a proof of membership is not provided for dummy nodes.

### Misc

#### Testing and JavaDocs

Due to the prototype nature of the task, only exemplary automated testing was performed to demonstrate how possible
tests and their structuring could look like. The same applies to the JavaDocs.

#### Further extensions

The solution can be extended in various ways. For example, it would be reasonable to handle errors and map them to
meaningful error codes that align with the API and provide relevant information to the user. Additionally, it is
conceivable to develop additional functionalities, as well as create a frontend using technologies like React.
Containerization using Docker and orchestration with Kubernetes could also be considered for the solution.