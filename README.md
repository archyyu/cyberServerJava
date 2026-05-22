this is the project used in the backside of internet cafe.

it uses spring-boot, mysql as the persistence, and stateless. 

this project is convert from the another project which is wroten by C sharp. 
I change it a little bit, for example, didnot store the sessions in the memory, but use db as the main and only source of truth.
1: merge the online and billing table a little bit, so the billing is only for finance purpose, the online equals to the session
2: split the online_record and online, if the user finishs the session, move the info from the online to online_record, by doing so, 
we could separate the hot data and cold data.


also, I spent years at small companies in China, so when coding, we would only try to ship as many features as possible regardless
of the teah debit. so in this small project, I would follow three principles.
1: obbey the principles from the book clean code
2: utilize modern java features, like record, optional, virtual threads, stream.
3: cover the code by unit tests and functional tests.
