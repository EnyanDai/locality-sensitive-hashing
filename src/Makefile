TWEET=/cw/bdap/assignment2/tweets


Runner.class:Runner.java
	javac -cp . $<
LSH.class:LSH.java
	javac -cp . $<
LSH.small:LSH.class
	@echo "Using brute search to get result"
	rm -f ouput
	time java -cp . LSH -threshold 0.9 -maxFiles 100 -inputPath $(TWEET) -outputPath ./output -shingleLength 3 -allcandidates
LSH:LSH.class
	@echo "Using LSH to get result"
	rm -f ouput
	time java -cp . LSH -threshold 0.9 -maxFiles 7416113 -inputPath $(TWEET) -outputPath ./output -shingleLength 3 -allcandidates
LSH.threshold:LSH.class
	@echo "Using lsh to get result"
	rm -f ouput
	time java -cp . LSH -threshold 0.9 -maxFiles 7416113 -inputPath $(TWEET) -outputPath ./output -shingleLength 3
Runner:Runner.class
	@echo "parameter analysis,it may cost a few minutes,please be paitent"
	time java -cp . Runner -maxFiles 5000 -inputPath $(TWEET) -outputPath ./output -shingleLength 3 -allcandidates
Runner.threshold:Runner.class
	@echo "parameter analysis,it may cost a few minutes,please be paitent"
	time java -cp . Runner -maxFiles 5000 -inputPath $(TWEET) -outputPath ./output -shingleLength 3