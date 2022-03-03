cmake_minimum_required(VERSION 3.4.1)

# ding yi cpp yuan wen jian mulu bian liang
set(SRC_DIR ${PROJECT_SOURCE_DIR}/src/main/cpp)

set(SRC_CPPCOMMON ${SRC_DIR}/cppcommon)
set(SRC_CPP ${SRC_DIR}/cppsrc)
set(OPENSLES_DIR ${SRC_DIR}/opensles)
set(FFMPEG_INC ${SRC_DIR}/ffmpeg)


set(libs "${CMAKE_SOURCE_DIR}/src/main/jniLibs")

# she zhi cpp yuan ma mu lu
aux_source_directory(${SRC_DIR} src_cpp)
aux_source_directory(${SRC_CPPCOMMON} ffmpeg_common)
aux_source_directory(${SRC_CPP} ffmpeg_demo)
aux_source_directory(${OPENSLES_DIR} opensles_cpp)

# she zhi .h tou wen jian mu lu
include_directories(${OPENSLES_DIR})
include_directories(${FFMPEG_INC})
include_directories(${SRC_CPP})
include_directories(${SRC_CPPCOMMON})

# yin ru android log ku
find_library( # Sets the name of the path variable.
        log-lib
        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

include_directories(${SRC_DIR}/include)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11 -fexceptions -frtti")
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/jniLibs/${CMAKE_ANDROID_ARCH_ABI})
# import ffmpeg library
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${CMAKE_SOURCE_DIR}/src/main/jniLibs/${CMAKE_ANDROID_ARCH_ABI} -lm -lz")
# ding yi jiang gai ku da bao jin app de lei xing
add_library(viomiMedia SHARED
        ${src_cpp}
        ${ffmpeg_demo}
        ${ffmpeg_common}
        ${com_cpp}
        ${opensles_cpp}
        )
target_link_libraries(viomiMedia android log
        OpenSLES
        mediandk
        # for dynamic
        avformat avcodec avfilter avutil swresample swscale
        # for static
        #        libavformat.a libavcodec.a libavfilter.a  libavutil.a libswresample.a libswscale.a libass.a libfontconfig.a libexpat.a
        #        libfreetype.a libfribidi.a libmp3lame.a libx264.a
        ${log-lib}
        )