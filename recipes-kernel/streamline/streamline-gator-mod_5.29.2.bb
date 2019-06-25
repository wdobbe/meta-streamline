SUMMARY = "Driver for ARM Streamline profiler, part of ARM DS-5 development environment"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit module

SRC_URI = "file://streamline_gator_${PV}.tar.xz \
           file://gator_mod_makefile_for_yocto.patch \
"
           

S = "${WORKDIR}/gator/driver"


#The original gator module Makefile generates an include file that contains the md5sum of all source files.
#After modifying the makefile to make it work with the Yocto build system, this doesn't work anymore.
#Generate it in the bitbake recipe instead.
do_compile_prepend() {
    echo "hallo"
    GATOR_SRC_FILES=$(cd ${S} && ls *.c *.h mali/*.h | grep -Ev '^(gator_src_md5\.h|gator\.mod\.c)$$' | LC_ALL=C sort )
    GATOR_SRC_FILES_MD5=$(echo ${GATOR_SRC_FILES} | xargs cat | md5sum | cut -b 1-32 )
    #echo "GATOR_SRC_FILES: ${GATOR_SRC_FILES}"
    #echo "GATOR_SRC_FILES_MD5: ${GATOR_SRC_FILES_MD5}"
    echo "static char *gator_src_md5 = \"${GATOR_SRC_FILES_MD5}\";" > ${S}/generated_gator_src_md5.h
}




