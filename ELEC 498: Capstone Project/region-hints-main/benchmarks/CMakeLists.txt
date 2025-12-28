cmake_minimum_required(VERSION 3.17)
project(benchmark C)

set(CMAKE_C_STANDARD 11)

add_executable(benchmark benchmarktest.c benchmarktest.h)
