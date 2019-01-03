//
// Created by labkinetic on 29/12/18.
//

#include "../include/Task.h"
#include "../include/EncoderDecoder.h"

#include <boost/algorithm/string.hpp>
#include <cstdlib>


Task::Task (ConnectionHandler *handler): _handler(handler), terminate(false){}

void Task::run() {
    while (!should_terminate()){
        const short BUFF_SIZE = 1024;
        char buf[BUFF_SIZE];
        cin.getline(buf, BUFF_SIZE);
        string line(buf);
        vector<string> split;
        boost::split(split, line, boost::is_any_of(" "));

        EncoderDecoder enc;
        vector<string> to_send = enc.encode(split);

        //Send the opcode
        string op_string = to_send.front();
        short op = atoi(op_string.c_str());
        char opcode[2];
        shortToBytes(op, opcode);
        _handler->sendBytes(opcode, 2);

        //Send other content if needed.
        if(to_send.size() > 1){
            _handler->sendLine(to_send.back());
        }

    }
}

bool Task::should_terminate(){
    return terminate;
}

void Task::terminate_task(){
    this->terminate = true;
}

void Task::shortToBytes(short num, char* bytesArr) {
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);
}