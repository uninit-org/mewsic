cmake_minimum_required(VERSION 3.4.1)
project(mewsic-engine-android)

add_library(mewsic-engine SHARED android.cpp)

if (${ANDROID_ABI} MATCHES armeabi-v7a)
    set(ANDROID_NATIVES_DIR androidNativeArm32)
elseif(${ANDROID_ABI} MATCHES arm64-v8a)
    set(ANDROID_NATIVES_DIR androidNativeArm64)
elseif(${ANDROID_ABI} MATCHES x86_64)
    set(ANDROID_NATIVES_DIR androidNativeX64)
elseif(${ANDROID_ABI} MATCHES x86)
    set(ANDROID_NATIVES_DIR androidNativeX86)
else()
    message(FATAL_ERROR "Unknown target: ${ANDROID_ABI}")
endif()

if (CMAKE_BUILD_TYPE MATCHES Debug)
    set(NATIVES_PATH ${NATIVES_BUILD_DIR}/${ANDROID_NATIVES_DIR}/debugStatic)
else()
    set(NATIVES_PATH ${NATIVES_BUILD_DIR}/${ANDROID_NATIVES_DIR}/releaseStatic)
endif()

find_library(log log)

target_include_directories(mewsic-engine PUBLIC ${NATIVES_PATH} include/)
target_link_libraries(mewsic-engine PUBLIC ${NATIVES_PATH}/libmewsic_engine.a log)
