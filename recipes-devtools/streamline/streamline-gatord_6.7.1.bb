DESCRIPTION = "ARM DS-5 Streamline gator daemon, needed to use Streamline profiler"
AUTHOR = "ARM Ltd."
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://libsensors/COPYING.LGPL;md5=4fbd65380cdd255951079008b364516c \
                    file://mxml/COPYING;md5=a6ba38606d63bb042c5d8cfee182e120 \
"
                    
RDEPENDS_${pn} = "streamline-gator-mod"

inherit systemd

#Use tarball that is already included with gator kernel module recipe
FILESEXTRAPATHS_prepend := "${THISDIR}/../../recipes-kernel/streamline/streamline-gator-mod:"

SRC_URI = "git://github.com/ARM-software/gator.git;protocol=https \
           file://${PN}.service \
           file://streamline_gatord_670to671.patch \
           file://gatord-yocto-build.patch \
"
SRCREV = "6.7"

S = "${WORKDIR}/git/daemon"


    
do_compile() {
    oe_runmake CC='${CC}' CXX='${CXX}'
}


do_install () {
    install -d -D -m 0755 ${D}${sbindir}
    install -m 0755 ${S}/gatord ${D}${sbindir}
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/${PN}.service ${D}${systemd_unitdir}/system
}

FILES_${PN} += "${sbindir} \
                ${sbindir}/gatord \
                ${systemd_unitdir}/system/${PN}.service \
"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "streamline-gatord.service"
SYSTEMD_AUTO_ENABLE = "disable"

