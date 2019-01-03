#include <iostream>
#include <mutex>
#include <thread>

#include "../include/Task.h"
#include "../include/ConnectionHandler.h"
#include "../include/EncoderDecoder.h"

using namespace std;
int main(int argc, char **argv) {

    if(argc != 3){
        return 1;
    }
    std::string ip = argv[1];
    short port = std::stoi(argv[2]);

    EncoderDecoder encdec;
    ConnectionHandler handler(ip,port);
    if(!handler.connect()){
        cerr << "Cannot connect to " << ip << ": " << port << endl;
        return 1;
    }

    //start a thread to handle the user keyboard input:
    Task cin_handler(&handler);
    std::thread th1(&Task::run, cin_handler);

    //handle the server input:
    bool to_terminate = false;
    string to_print;
    string to_dec;

    while(!to_terminate){
        if(handler.getLine(to_dec)) {
            cout << "i got a line" << endl;
            to_print = encdec.decode(to_dec);
            if(to_print == "ACK 3"){
                to_terminate = true;
                cin_handler.terminate_task();
            }
            cout << to_print << endl;
        }
    }


    th1.join();
}
