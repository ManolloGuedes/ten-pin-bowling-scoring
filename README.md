### Installing dependencies
This software was built using the Maven tool as a project manager. To install the necessary dependencies to run the software, run the following command:

* mvn install

### Execution command
The software expects to receive as input the absolute path of a file containing the game logs.
Thus, the command for running the software is as follows:

* mvn spring-boot:run -q -Dspring-boot.run.arguments=/absolute/path/to/the/test/file
    * In order to run the software silently (without unnecessary logs) we should use the parameter "-q".

There are 5 test cases ready to use in the folder *src/test/resources/*.

These tests cover 5 different scenarios:
* 0-and-10.txt: 2 players, 1 has a perfect score, and the other has a zero score.
* fault-score.txt: 1 player, fault score.
* perfect-score.txt: 1 player, perfect score.
* zero-score.txt: 1 player, final score as zero.
* test.txt: a generic game extracted from the pdf challenge file.