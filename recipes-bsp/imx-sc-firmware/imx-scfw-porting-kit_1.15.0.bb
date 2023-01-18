# Copyright (C) 2016 Freescale Semiconductor
# Copyright 2017-2019, 2023 NXP
DESCRIPTION = "i.MX System Controller Firmware Porting Kit"
SECTION = "BSP"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://COPYING;md5=5a0bf11f745e68024f37b4724a5364fe"

PROVIDES += "imx-sc-firmware"

SRC_URI:append = " https://developer.arm.com/-/media/Files/downloads/gnu-rm/9-2019q4/gcc-arm-none-eabi-9-2019-q4-major-x86_64-linux.tar.bz2;name=toolchain"

SRC_URI[md5sum] = "3396390ce6d771330aa10ac9ae2f0e3b"
SRC_URI[sha256sum] = "183313205b183d9d05d3ce5ff5e074e1d1704cbbe0c5559ff41515529b5a899d"
SRC_URI[toolchain.md5sum] = "fe0029de4f4ec43cf7008944e34ff8cc"
SRC_URI[toolchain.sha256sum] = "bcd840f839d5bf49279638e9f67890b2ef3a7c9c7a9b25271e83ec4ff41d177a"

inherit fsl-eula2-unpack2 deploy

CONFIG = "${SOC_FAMILY}${TARGET}_${@d.getVar('REV').lower()}"

SOC_FAMILY = "SOC_FAMILY_UNDEFINED"
SOC_FAMILY:mx8-generic-bsp = "mx8"

TARGET                = ""
REV                   = ""

TARGET:mx8qm-nxp-bsp  = "qm"
REV:mx8qm-nxp-bsp     = "B0"

TARGET:mx8qxp-nxp-bsp = "qx"
REV:mx8qxp-nxp-bsp    = "B0"

TARGET:mx8dxl-nxp-bsp = "dxl"
REV:mx8dxl-nxp-bsp    = "A0"

BOOT_TOOLS = "imx-boot-tools"

# Unpack the CONFIG-specific archive
do_unpack_scfw_porting_kit() {
    tar -xzvf ${S}/src/scfw_export_${CONFIG}.tar.gz --directory=${S}
}

python do_unpack:append() {
    bb.build.exec_func('do_unpack_scfw_porting_kit', d)
}

# Use the CONFIG-specific source as the build folder
B = "${S}/scfw_export_${CONFIG}"

do_compile() {
    export TOOLS="${WORKDIR}"
    oe_runmake ${TARGET} B=${BOARD_TYPE} R=${REV}
}

do_install() {
    install -d ${D}${nonarch_base_libdir}/firmware/nxp
    install -m 0644 ${B}/build_${CONFIG}/scfw_tcm.bin ${D}${nonarch_base_libdir}/firmware/nxp
}

do_deploy() {
    install -d ${DEPLOYDIR}/${BOOT_TOOLS}
    install -m 0644 ${B}/build_${CONFIG}/scfw_tcm.bin ${DEPLOYDIR}/${BOOT_TOOLS}
}
addtask deploy after do_compile

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

PACKAGE_ARCH = "${MACHINE_ARCH}"

COMPATIBLE_MACHINE = "(mx8qm-nxp-bsp|mx8qxp-nxp-bsp|mx8dxl-nxp-bsp)"
