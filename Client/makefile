# All Targets
all: rest

# Tool invocations
rest: bin/client.o bin/ConnectionHandler.o bin/EncoderDecoder.o bin/Task.o 
	@echo 'Building target: rest'
	@echo 'Invoking: C++ Linker'
	g++ -o bin/client bin/ConnectionHandler.o bin/EncoderDecoder.o bin/Task.o
	@echo 'Finished building target: hello'
	@echo ' '


bin/client.o: src/client.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/client.o src/client.cpp

bin/ConnectionHandler.o: src/ConnectionHandler.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/ConnectionHandler.o src/ConnectionHandler.cpp

bin/EncoderDecoder.o: src/EncoderDecoder.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/EncoderDecoder.o src/EncoderDecoder.cpp

bin/Task.o: src/Task.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/Task.o src/Task.cpp


#Clean the build directory
clean:
	rm -f bin/*
