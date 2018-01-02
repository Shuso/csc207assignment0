# csc207assignment0
Write a Java program on Collaborative Filtering 

Sites such as Amazon and Ebay, track-purchasing history of its users and shoppers. When the user logs in, these sites use the information to suggest products that may be of interest to you. Amazon for instance can recommend movies you might like, even if you have purchased books or music from it before.
The data by these sites can be mined in very different ways. Data can be (I like / I dislike), (yes/ no) vote, or as ratings (1 to 5) etc. On the other hand, dating websites try to match up people based on how similar they are. If Jack and Jill have similar interests, Jack may get a recommendation on his pro(ile page ?Hi Jack, Jill has similar interests as you, do you like to send her a message OR add her as friend??

Your job is to (ind a similarity score between pair of users that tells how similar they are i.e. Are user1 and user2 more similar based on their rating of movies OR are user3 and user1 more similar? 
To calculate the Euclidean Distance between pairs of user . we use the Euclidean Distance formula.

This formula actually calculates the distance and we know that for users that are very similar (or very close on the chart) the distance will be less compared to users that are very dissimilar (or very far on the chart).In order to get higher values (similarity score) for users who are similar we take theinverse of the distance and add 1 to the denominator to avoid division by 0.

a) The userUserMatrix that your program calculates (where columns and rows are User1, User2, User3 and User4?

b) Most similar pair of users (both users in the pair MUST be different. If there are more than one pair of different users with the same score, you must list all of them but do not repeat them for example the pair (User1 and User3) is identical to (User3 and User1)): From the userUserMatrix, the most similar pair of users are: User1 and User3.
c) Most dissimilar pair of users (both users MUST be different. If there are more than one pair of different users with the same score, you must list all of them but do not repeat them for example the pair (User3 and User4) is identical to (User4 and User3)):From the userUserMatrix, the most dissimilar pair of users are:User3 and User4.