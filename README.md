As technology becomes an increasingly essential part of our lives, we also spend more and more of our time in the digital space: browsing websites, using software applications, and connecting to networks. At the same time, however, network cyber attacks have also become more frequent. It’s just as important to focus on advancing the capabilities of our networks as making sure they are secure and protected. 

This network monitor utility can parse network traffic (in real time or by reading in a PCP file) and detect a variety of anomalous network scenarios.
* DoS attack
* DDoS attack
* Foreign IP address connection

This program build an algorithm that models network behavior using the Shannon's entropy formula. First, the program goes through a learning phase, whose timeframe can be adjusted. It reads in an arbitrary amount of preliminary data and is able to calculate a baseline entropy which defines the "normal", assuming the data given as training is “normal” network traffic.
After the learning phase is completed, it transitions to an active detection phase, which
calculates mean value of entropy every 100ms (also adjustable). The program then compares the mean entropy values to the normal baseline value, and sends an alert message if the difference exceeds a certain threshold.

Calculation Process: 

Let a random variable p(i) represent the total number of packets of a particular connection i over a given time interval t. A connection is the network traffic between the same source, destination IP and protocol. The entropy for a particular interval is calculated as follows:

<img src="https://user-images.githubusercontent.com/74235189/202102588-8894d4d9-a79d-4d6a-9cc3-17b34a2bba49.png" width="300" height="80" />

Steps to run/apply the program: 
* Build and Install the program locally using git clone
* execute the “mvn clean install” terminal function 
* To detect real-time/live network traffic.  
Run: java -jar <The jar file built from step 1>
* To detect stored traffic data (PCAP file). 
Run: java -jar <The jar file built from step 1> -f <full path to PCAP file>
