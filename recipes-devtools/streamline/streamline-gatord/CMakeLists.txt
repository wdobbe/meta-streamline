project(streamline_gatord)

cmake_minimum_required(VERSION 3.2)

#Pretty colors
SET(CMAKE_COLOR_MAKEFILE ON)


get_property(LIB64 GLOBAL PROPERTY FIND_LIBRARY_USE_LIB64_PATHS)
if ("${LIB64}" STREQUAL "TRUE")
    set(LIBSUFFIX 64)
else()
    set(LIBSUFFIX "")
endif()


#Gatord requires a c++11 compiler
set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)
if (CMAKE_COMPILER_IS_GNUCC AND CMAKE_CXX_COMPILER_VERSION VERSION_LESS 4.8)
    message(FATAL_ERROR "Compilation of this software requires gcc >= 4.8 (or another compiler supporing C++11).")
endif() 

if (CMAKE_HOST_UNIX OR MINGW)
    set( CMAKE_THREAD_PREFER_PTHREAD TRUE )
    find_package(Threads REQUIRED )
    set( GCC_EXTRA_COMPILE_OPTIONS "${GCC_EXTRA_COMPILE_OPTIONS} -pthread" )
endif()

#Compile escape program with native compiler to generate .h files from xml files
if (NOT EXISTS ${CMAKE_BINARY_DIR}/escape)
    execute_process(COMMAND gcc -o ${CMAKE_BINARY_DIR}/escape ${CMAKE_SOURCE_DIR}/escape/escape.c RESULT_VARIABLE ESCAPE_RESULT)
    if (NOT ESCAPE_RESULT EQUAL 0)
        message(FATAL_ERROR "Could not compile escape binary")
    endif()
endif()

#Generate SrcMd5.cpp that contains md5 sum of all (non-generated) source files
set(MD5_SRC_FILE SrcMd5)
file(GLOB ALL_GATOR_SRCS LIST_DIRECTORIES false RELATIVE ${CMAKE_SOURCE_DIR} *.cpp *.h mxml/*.c mxml/*.h libsensors/*.c libsensors/*.h)
list(SORT ALL_GATOR_SRCS)
foreach(NEXT_FILE ${ALL_GATOR_SRCS})
    if( NOT NEXT_FILE MATCHES ".*_xml\.h|${MD5_SRC_FILE}.cpp" )
        message("${NEXT_FILE}")
        list(APPEND SRCS_FOR_MD5 ${NEXT_FILE})
    endif()
endforeach()
list(SORT SRCS_FOR_MD5)
foreach(NEXT_FILE ${SRCS_FOR_MD5})
    file(READ ${NEXT_FILE} NEXT_CONTENT)
    set(ALL_CONTENT "${ALL_CONTENT}${NEXT_CONTENT}")
endforeach()
string(LENGTH "${ALL_CONTENT}" CONTENT_LENGTH)
string(MD5 CONTENT_MD5 "${ALL_CONTENT}")
message("Calc. md5 over ${CONTENT_LENGTH} chars: ${CONTENT_MD5}")
set(MD5_VAR_LINE "extern const char *const gSrcMd5 = \"${CONTENT_MD5}\";")
file(WRITE ${CMAKE_BINARY_DIR}/${MD5_SRC_FILE}.cpp "${MD5_VAR_LINE}")


#Geenerate .h files from event xml files
file(GLOB EVENT_XML_FILES LIST_DIRECTORIES false RELATIVE ${CMAKE_SOURCE_DIR} events-*.xml)
file(READ ${CMAKE_SOURCE_DIR}/events_header.xml ALL_CONTENT)
foreach(NEXT_FILE ${EVENT_XML_FILES})
    file(READ ${NEXT_FILE} NEXT_CONTENT)
    set(ALL_CONTENT "${ALL_CONTENT}${NEXT_CONTENT}")
endforeach()
file(READ ${CMAKE_SOURCE_DIR}/events_footer.xml NEXT_CONTENT)
set(ALL_CONTENT "${ALL_CONTENT}${NEXT_CONTENT}")
file(WRITE ${CMAKE_BINARY_DIR}/events.xml ${ALL_CONTENT})

file(GLOB EVENT_XML_FILES LIST_DIRECTORIES false RELATIVE ${CMAKE_SOURCE_DIR} *.xml)
foreach(NEXT_XML_FILE ${EVENT_XML_FILES})
    #message("gen. ${NEXT_XML_FILE}")
    string(REGEX REPLACE "\.xml$" "_xml" GENERATED_FILE_BASENAME ${NEXT_XML_FILE})
    
    set(EVENT_HEADER_FILES ${EVENT_HEADER_FILES} ${CMAKE_BINARY_DIR}/${GENERATED_FILE_BASENAME}.h)
    add_custom_command(OUTPUT ${CMAKE_BINARY_DIR}/${GENERATED_FILE_BASENAME}.h
                       COMMAND ${CMAKE_BINARY_DIR}/escape ${GENERATED_FILE_BASENAME} ${NEXT_XML_FILE} ${CMAKE_BINARY_DIR}/${GENERATED_FILE_BASENAME}.h
                       DEPENDS ${CMAKE_SOURCE_DIR}/${NEXT_XML_FILE}
                       WORKING_DIRECTORY ${CMAKE_SOURCE_DIR} )
endforeach()
set(EVENT_HEADER_FILES ${EVENT_HEADER_FILES} ${CMAKE_BINARY_DIR}/events_xml.h)
add_custom_command(OUTPUT ${CMAKE_BINARY_DIR}/events_xml.h
                   COMMAND ${CMAKE_BINARY_DIR}/escape events_xml events.xml events_xml.h
                   DEPENDS ${EVENT_XML_FILES} events_header.xml events_footer.xml
                   WORKING_DIRECTORY ${CMAKE_BINARY_DIR} )


include_directories(${CMAKE_BUILD_DIR} libsensors)


set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O0 -Wall -fno-exceptions -pthread -MD -DETCDIR=\\\"/etc\\\" -fno-rtti -Wextra -Wshadow -Wpointer-arith -Wundef")
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -O0 -Wall -fno-exceptions -pthread -MD -DETCDIR=\\\"/etc\\\"")

include_directories(${CMAKE_SOURCE_DIR} ${CMAKE_BINARY_DIR} libsensors)

file(GLOB LIBSENSOR_SRCS LIST_DIRECTORIES false ${CMAKE_SOURCE_DIR}/libsensors/*.c)
file(GLOB MXML_SRCS LIST_DIRECTORIES false ${CMAKE_SOURCE_DIR}/mxml/*.c)
file(GLOB_RECURSE GATORD_SRCS LIST_DIRECTORIES false ${CMAKE_SOURCE_DIR}/*.cpp ${CMAKE_SOURCE_DIR}/lib/*.cpp linux/*.cpp ${CMAKE_SOURCE_DIR}/linux/*/*.cpp ${CMAKE_SOURCE_DIR}/mali_userspace/*.cpp ${CMAKE_SOURCE_DIR}/non_root/*.cpp )

add_executable(gatord ${LIBSENSOR_SRCS} ${MXML_SRCS} ${GATORD_SRCS} ${CMAKE_BINARY_DIR}/${MD5_SRC_FILE}.cpp ${EVENT_HEADER_FILES} )
target_link_libraries(gatord rt m )


