SUMMARY = "Driver that turns on the performance counters of the NXP iMX6 PMU"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"


inherit module


SRC_URI = "file://Makefile \
           file://imx6-pmu-count.c \
          "

S = "${WORKDIR}"
          



# The inherit of module.bbclass will automatically name module packages with
# "kernel-module-" prefix as required by the oe-core build environment.
