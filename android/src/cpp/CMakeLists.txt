cmake_minimum_required(VERSION 3.4.1)

file(GLOB bloom  "./bloom-filter-cpp/*.cpp")
file(GLOB hashset  "./hashset-cpp/*.cc")
file(GLOB client "./*.cc")

add_library( # Specifies the name of the library.
             native-lib

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             ${bloom} ${hashset} ${client} native-lib.cpp)

target_link_libraries( native-lib
        android
        log )