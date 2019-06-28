/* 
 * Enable NXP i.MX6 performance counters
 *
 * Winfried Dobbe (winfried_mb2 at xmsnet dot nl) 2019
 * */

#include <linux/version.h>
#include <linux/kernel.h>
#include <linux/module.h>
#include <asm/uaccess.h>
#include <asm/io.h>

/* This module will enable the performance counters of the ARM performance Monitoring Unit (PMU).
 * Assembly instructions copied from https://community.nxp.com/thread/302685 and
 * https://community.arm.com/developer/tools-software/tools/f/armds-forum/6832/no-events-data-on-the-arm-ds-5-streamline-with-the-sabrelite-i-mx6-quad-core */



/****************************************************************************
* Entry and Exit
****************************************************************************/


static int __init peemuperf_init(void)
{
    u32 val = 0b11;
    asm volatile("mcr p15, 0, %0, c1, c1, 1" : : "r" (val));
	return 0;
}

static void __exit peemuperf_exit(void)
{
    u32 val = 0b00;
    asm volatile("mcr p15, 0, %0, c1, c1, 1" : : "r" (val));

}


module_init(peemuperf_init);
module_exit(peemuperf_exit);
MODULE_DESCRIPTION("PMU driver - insmod peemuperf.ko");
MODULE_AUTHOR("W. Dobbe (winfried_mb2 at xmsnet dot nl");
MODULE_LICENSE("GPL");

