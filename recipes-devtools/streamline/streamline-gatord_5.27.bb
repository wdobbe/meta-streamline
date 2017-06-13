DESCRIPTION = "ARM DS-5 Streamline gator daemon, needed to use Streamline profiler"
AUTHOR = "ARM Ltd."
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
RDEPENDS_${pn} = "streamline-gator-mod"

inherit systemd

#Use tarball that is already included with gator kernel module recipe
FILESEXTRAPATHS_prepend := "${THISDIR}/../../recipes-kernel/streamline/streamline-gator-mod:"

SRC_URI = "file://streamline_gator_${PV}.tar.xz \
           file://${PN}.service \
           file://gatord-yocto-build.patch \
"

S = "${WORKDIR}/gator/daemon"


    
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

