# Hashsnail-Server
Hashsnail is extremely slow (because it uses only CPU) and barebones clone of the most popular password-cracking utility - Hashcat, but with one key difference - it uses distributed computing with client-server model to crack passwords. And this repository is the server side of the whole system. The application was written as a University DSA course final project, client side was written by me and server side by my colleague.
# How it works
The main idea is to divide amount of work among clients based on their relative performance. For example, range of work for mask-based attack can look something like “%D#5%L%F”, it means client with this work range will be bruteforcing all the 4 letter permutations from “05aA” to “95zZ” (How mask syntax works, see further in the paragraph “How to use”). We wrote an algorithm that divides amount of work for any number of clients in this manner. This is done with a performance benchmark that starts before any work does, then clients send benchmark results back to the server, so it can use the algorithm to determine amount of work for every client. Server sends file with hashes and information like "what hashing algorithm to use" and "what attack mode to run" to every client, after that all the clients start attacking simultaneously. All clients stop working roughly at the same time and send their results back to the server.
#### External libraries
- Apache Commons CLI
- log4j-slf4j-impl
#### Implemented attack modes
- Mask based bruteforce
- Dictionary attack
#### Implemented hashing algorithms
- SHA-1
- MD5
# Screenshots
#### Server side
![image](https://user-images.githubusercontent.com/95579070/189551256-6df465d8-ec56-4b7f-a5d2-f12ec6673f2a.png)
![image](https://user-images.githubusercontent.com/95579070/189551289-b1e93b8a-969b-4bc6-9320-dc38388baa73.png)
#### Client side
![image](https://user-images.githubusercontent.com/95579070/189554349-9226582b-7117-4cf8-97fa-ff5087a6b919.png)
# How to use
0. To run the server side part of application required a pre-installed Java Virtual Machine 18.0.1.1 or more recent version.
1. Download or recursive clone this repository.
2. Go to "target" directory and run your cli client
3. Use "java -jar Hashsnail.jar" + your CLI arguments. At startup, the program uses command-line arguments to determine the operating mode, hashing algorithm, and so on.
4. Now chosen port is listening. Connect all your clients, write "/start" and wait while each does received work

#### CLI arguments guide
- `-p --port <port>` if used, default port 8000 will be changed to the specified
- `-a --algorithm <algorithm name>` now supports MD5 and SHA1 (MD5 by default)
- `-d --dictionary <path to dictionary file>` dictionary attack mode, takes path to file with a set of potential passwords (one of attack modes must be specified)
- `-m --mask <mask>` mask attack mode, takes a special symbol set that determines range of potential passwords (one of attack modes must be specified)
- `-s --single <hash>` if used, password will be searched for only one specified hash, instead of using file with hashes
- `-c --hash-collection <path to hash file>` if used, allows to set hash file location ("hash.txt" by default)

####  Mask syntax guide
- `%` is used with one of the following characters to select the necessary alphabet:
    - `D` "0123456789"
    - `L` "abcdefghijklmnopqrstuvwxyz"
    - `U` "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    - `F` "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
- `#` is used with any symbol to explicitly specify an already known character in potential password

# Client
[Client side application repository made by my colleague](https://github.com/rebmanop/Hashsnail-Client)

