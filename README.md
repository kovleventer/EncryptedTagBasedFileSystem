# Encrypted Tag Based FileSystem

This project is kind of a proof-of-concept for a file manager/file system, which would be based around tags, and would provide a relatively secure encryption. A bit like a mix between VeraCrypt and a database file system.

So, the main requirements were:

* Files should be identified by their tags. Each file can have multiple tags, and a tag might belong to multiple files, in other words, there is an n-to-n relation between files and tags.
* The whole file and tag structure should be encrypted, not only the contents of the files.
* No mounting by default. Other malicious programs should not be able to access this file system. The operating system and privileged programs are obviously an exception, since there is no way to prevent them from accessing memory.

While this project works, it's far from complete. Almost every aspect of this program could be improved or should be rewritten from scratch, preferably in an other language. The project was mainly created to illustrate those requirements, and provide a how-should-it-look-like demo. The GUI is hideous, the encryption is insecure, and the database system does not scale very well. But it might be a good starting point.
