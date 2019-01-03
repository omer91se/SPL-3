//
// Created by labkinetic on 29/12/18.
//

#include <algorithm>
#include <cstring>
#include <iostream>
#include "../include/EncoderDecoder.h"


using namespace std;

EncoderDecoder::EncoderDecoder() {

}

vector<string> EncoderDecoder::encode(vector<string> msg) {

    vector<string> output;
    string op="";
    string content="";

    if(msg[0] == "REGISTER"){

        op = "1";
        content = msg[1]  +'\0' + msg[2];
        output.push_back(op);
        output.push_back(content);
    }

    if(msg[0] == "LOGIN") {

        op= "2";
        content = msg[1] + '\0' + msg[2] + '\0';
        output.push_back(op);
        output.push_back(content);
    }

    if(msg[0] == "LOGOUT") {
        op = "3";
        output.push_back(op);
        output.push_back("");
    }

    if(msg[0] == "FOLLOW") {

        op= "4";
        if(stoi(msg[2])>10)
            content = msg[1] + msg[2];
        else
            content = msg[1] + '0' + msg[2];

        for(int i = 3 ; i < msg.size(); ++i ){
            content = content + msg[i] +'\0';
        }
        output.push_back(op);
        output.push_back(content);
    }

    if(msg[0] == "POST") {
        op = "5";
        for(int i = 1 ; i < msg.size(); i++) {
            content = content + msg[i] + " ";
        }
        cout << content << endl;
        output.push_back(op);
        output.push_back(content);
    }

    if(msg[0] == "PM") {
        op = "6";
        content = msg[1] + '\0';
        for(int i = 2 ; i < msg.size(); i++) {
            content = content + msg[i] + " ";
        }
        output.push_back(op);
        output.push_back(content);
    }

    if(msg[0] == "USERLIST") {
        op = "7";
        output.push_back(op);
        output.push_back("");
    }

    if(msg[0] == "STAT") {
        op = "8";
        content = msg[1] + '\0';
        output.push_back(op);
        output.push_back(content);
    }
    return output;
}

std::string EncoderDecoder::decode(std::string msg){

    char opByte[2];
    opByte[0] = msg[0];
    opByte[1] = msg[1];
    short op = bytesToShort(opByte);

    string to_print = "";

    if(op == 9){
        to_print = "NOTIFICATION ";
        short pm_public = msg[2];
        if(pm_public == 0){
            to_print = to_print + "PM ";
        }
        else{
            to_print = to_print + "Public ";
        }



    }

    if(op == 10){
        to_print = "ACK ";
    }

    if(op == 11){
        to_print = "ERROR ";
    }
    return to_print;
}


short EncoderDecoder::bytesToShort(char* bytesArr) {
    short result = (short)((bytesArr[0] & 0xff) << 8);
    result += (short)(bytesArr[1] & 0xff);
    return result;
}

