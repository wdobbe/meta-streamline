Build ARM Streamline target software, with added support for NXP i.MX6 SOC.


Dependencies
============

This layer depends on:

  URI: git://git.openembedded.org/bitbake
  branch: master

  URI: git://git.openembedded.org/openembedded-core
  layers: meta
  branch: sumo

  URI: git://git.yoctoproject.org/poky
  branch: sumo
  


Patches
=======

Please submit any patches against the meta-streamline layer to 

Maintainer: W. Dobbe,  winfried <underscore> mb2 <at> xmsnet <dot> nl 




I. Adding the meta-streamline layer to your build
=================================================

In order to use this layer, you need to make the build system aware of
it.

Assuming the meta-streamline layer exists at the top-level of your
yocto build tree, you can add it to the build system by adding the
location of the meta-streamline layer to bblayers.conf, along with any
other layers needed. e.g.:

  BBLAYERS ?= " \
    /path/to/yocto/meta \
    /path/to/yocto/meta-poky \
    /path/to/yocto/meta-yocto-bsp \
    /path/to/yocto/meta-streamline \
    "

Then add the following recipes to IMAGE_INSTALL in your image file:
* kernel-module-imx6-pmu-count
* kernel-module-gator
* streamline-gatord

II. Starting and stopping the Streamline gator daemon
=====================================================
To load the required kernel modules and start the gatord daemon run command:
`systemctl start streamline-gatord.service`

The port that is used for communication with the streamline GUI can be changed in file /etc/streamline-gator.conf .
The default is set to port 4880 because port 8080 was already in use on our targets.

To stop the gatord daemon and unload the related kernel modules run command:
`systemctl stop streamline-gatord.service`

III. Misc
========
This layer contains recipes for the ARM DS-5 streamline gator kernel module and daemon.
It builds version 6.7.1 of the gator kernel module and daemon, as included with Arm DS-5 version 5.29.2 (the last DS-5 version ?).

The makefile for the (optional) gator kernel module had to be changed to let it build with Yocto.
To remove complexity in the makefile I have moved the generation of the include file gator_src_md5.h
(that contains a md5sum of all concatenated source files) to the bitbake recipe.

Tested with Yocto Krogoth and Sumo branch and Congatec QMX6 (NXP i.MX6quad SOM).

Note that Arm discontinued DS-5 and released its replacement 'Arm Development Studio'. Arm Development Studio contains a newer
version of Streamline. However the license price and conditions changed to the worse so my company decided to not upgrade to
Arm Development Studio at the moment. This means I cannot test the newer Streamline versions and this layer will stay at
gator 6.7.1.

IV. i.MX6 support and limitations
==================================

The performance counters of the NXP i.MX6 SOC are turned off by default. I included a kernel module
that will turn on these counters when the module is loaded, and will turn the counters off when
the module is unloaded. This module should only be used with NXP i.MX6 SOCs. If your target has a different SOC, edit file
/etc/streamline-gator.conf and set parameter enable_imx6_counters to 'n'.

Unfortunately Freescale decided to connect the IRQ lines of the 4 Cortex A9 PMUs together. The result is that the performance
counters may be inaccurate when running all cores, and you may get a kernel oops due to superfluous interrupts.
To avoid this boot your target with nr_cpus=1 or shutdown cores 2, 3 and 4 with command:
echo 0 > /sys/devices/system/cpu/cpu3/online   (repeat for cpu2 and cpu1)
