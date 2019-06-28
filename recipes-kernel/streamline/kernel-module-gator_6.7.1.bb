SUMMARY = "Driver for ARM Streamline profiler, part of ARM DS-5 development environment"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
"

inherit module

SRC_URI = "git://github.com/ARM-software/gator.git;protocol=https \
           file://streamline_gatormod_670to671.patch \
           file://gator_mod_makefile_for_yocto.patch \
           file://streamline-gator.conf \
"
SRCREV = "6.7"           

S = "${WORKDIR}/git/driver"


#The original gator module Makefile generates an include file that contains the md5sum of all source files.
#After modifying the makefile to make it work with the Yocto build system, this doesn't work anymore.
#Generate it in the bitbake recipe instead.
do_compile_prepend() {
    GATOR_SRC_FILES=$(cd ${S} && ls *.c *.h mali/*.h | grep -Ev '^(gator_src_md5\.h|gator\.mod\.c)$$' | LC_ALL=C sort )
    GATOR_SRC_FILES_MD5=$(echo ${GATOR_SRC_FILES} | xargs cat | md5sum | cut -b 1-32 )
    #echo "GATOR_SRC_FILES: ${GATOR_SRC_FILES}"
    #echo "GATOR_SRC_FILES_MD5: ${GATOR_SRC_FILES_MD5}"
    echo "static char *gator_src_md5 = \"${GATOR_SRC_FILES_MD5}\";" > ${S}/generated_gator_src_md5.h
}


do_install_append() {
    install -d ${D}${sysconfdir}/modprobe.d
    install -m 644 ${WORKDIR}/streamline-gator.conf ${D}${sysconfdir}/modprobe.d
}

FILES_${PN} += "${sysconfdir}/modprobe.d/streamline-gator.conf"

EXTRA_OEMAKE_append_task-install = " -C ${STAGING_KERNEL_DIR} M=${S}"
