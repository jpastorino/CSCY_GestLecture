# Implementing Secure Client/Server Applications using Java

- Materials from the guest lecture I offered to CU Denver, CSE for the CSCY program
- November 17, 2022.

# Description

Modern messaging systems like iMessage®, WhatsApp®, and Facebook Messenger® rely on the widely used Client/Server architectural design pattern. These types of systems can be easily implemented using Java programming language. However, additional implementation details need to be considered to assure a certain privacy level when sending messages over the network. In this lecture, we will explain the Java language's capabilities to prevent a third party from reading the messages sent over these types of applications.

# Contents

- **slides** directory contains the handout of the slides presented. 
- **prototypes** contains four InteliJ Idea projects, corresponding to the Messaging Server and Messaging Client applications both without SSL/TLS (_Unsecured_ folder) and using SSL/TLS (_Secured_ folder).

# References
- [Oracle Security Developer's Guide – Chapter 8](https://docs.oracle.com/en/java/javase/17/security/java-secure-socket-extension-jsse-reference-guide.html#GUID-93DEEE16-0B70-40E5-BBE7-55C3FD432345)

- [Java Development Kit API Reference](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/javax/net/ssl/SSLServerSocket.html#getEnabledProtocols)

- [History of SSL/TLS](https://www.globalsign.com/en/blog/ssl-vs-tls-difference)

- [TLS 1.2 Handshake image](https://www.ssl2buy.com/wiki/wp-content/uploads/2018/08/ssl2buy-tls12-13.jpg)

- [CS 161 Computer Securyty by D. Wagner](https://textbook.cs161.org)