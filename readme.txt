1. How to import / Build / Run the project
2. Approach
3. Completions
4. Pending Items
5. Assumptions


1. How to import / Build / Run the project

To run the jar file, please follow the following steps

Mvn clean install 
Java -jar TradeAllocate-0.0.1-SNAPSHOT.jar 

The output file would be in the /tmp/ directory and output file name is: TargetAllocation.csv

2. Approach

Approach has been to understand the problem first. Since this was a banking / trading related project, the understanding of fundamentals of trades, the jargons was important before solving it. Once the problem statement and requirement was clear, next step was understanding what data was provided, how they are related and how they can to be used. Based on the data, bottom to top approach was used where idea of what data needs to be retrieved and stored in what entities (Holdings, Trades etc..) and then utilized to create and feed the final output table (Allocation). Once that part was clear, implementation was done. Implementation was started with brute force method but usage of packages and frameworks like Spring boot, Jackson DataFormat (to import/export CSV) and Lambok from Maven were utilized to lay the foundation quickly. Brute Force method was used to initially implement the code but some optimization were added later on as time allowed. 

3. Completions

All the allocation calculation has been completed. The output will show the account details with allocation calculated for both accounts. 

4. Pending Items
Due to timing constraints, below items were not completed
1. Test Cases (started off but couldn't complete all cases)
2. Full optimization of code
3. Error / Exception checking
4) Rules 

5. Assumptions

1. Rounding off of some values like "Suggested Final Position" were not done since the table shown still had the values in decimal, assumption was made that it would be OK. 

