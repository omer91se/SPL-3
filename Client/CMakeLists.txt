cmake_minimum_required(VERSION 3.12)
project(client)

set(CMAKE_CXX_STANDARD 11)

find_package(Boost COMPONENTS system filesystem thread REQUIRED)
IF(Boost_FOUND)
    include_directories(${Boost_INCLUDE_DIR})
endif()

SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11 -pthread -lboost_thread")
include_directories(include)

add_executable(
        client
        include/EncoderDecoder.h 
        src/EncoderDecoder.cpp
        src/client.cpp
        include/Task.h 
        src/Task.cpp 
        include/ConnectionHandler.h 
        src/ConnectionHandler.cpp 
        
        makefile)

target_link_libraries(client LINK_PUBLIC ${Boost_LIBRARIES})
