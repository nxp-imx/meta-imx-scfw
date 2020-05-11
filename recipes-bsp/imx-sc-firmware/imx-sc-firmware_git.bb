# Copyright (C) 2016 Freescale Semiconductor
# Copyright 2017-2019 NXP

DESCRIPTION = "i.MX System Controller Firmware"
LICENSE = "Proprietary"
SECTION = "BSP"
FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

inherit fsl-eula2-unpack2 pkgconfig deploy autotools

#Current firmware porting kit sources
SRC_URI = "file://scfw_export_${CONFIG}.tar.gz \
           file://scfw.patch"
#NOTE: Plase set SILICON_REVISION accordingly. Variants: A0, B0
#NOTE: Plase set BOARD_TYPE accordingly. Variants: mek, val
SILICON_REVISION = "B0"
BOARD_TYPE = "mek"

CONFIG = "mx8${PLATFORM_EXTENSION}_${@d.getVar('SILICON_REVISION').lower()}"
PLATFORM_EXTENSION = "${@d.getVar('MACHINE')[4:6]}"

S = "${WORKDIR}/scfw_export_${CONFIG}"

SC_FIRMWARE_NAME = "scfw_tcm.bin"
BOOT_TOOLS = "imx-boot-tools"

do_compile() {
	cd ${S}
	#NOTE: Set the correct path to the toolchain.
	export TOOLS="/opt/imx-scfw-toolchain/tools/"
	oe_runmake ${PLATFORM_EXTENSION} B=${BOARD_TYPE} R=${SILICON_REVISION}
}

do_install () {
    install -d ${STAGING_DIR}/boot
    install -m 0755 ${S}/build_${CONFIG}/${SC_FIRMWARE_NAME} ${STAGING_DIR}/boot/${SC_FIRMWARE_NAME}
}

do_deploy () {
    install -Dm 0644 ${S}/build_${CONFIG}/${SC_FIRMWARE_NAME} ${DEPLOYDIR}/${BOOT_TOOLS}/${SC_FIRMWARE_NAME}
    ln -sf ${SC_FIRMWARE_NAME} ${DEPLOYDIR}/${BOOT_TOOLS}/${symlink_name}

}

addtask deploy after do_install

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
PACKAGE_ARCH = "${MACHINE_ARCH}"

FILES_{PN} = "/boot"

COMPATIBLE_MACHINE = "(mx8qm|mx8qxp)"
