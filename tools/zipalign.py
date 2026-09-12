#!/usr/bin/env python3
"""Riscrive un APK allineando le voci non compresse.

Sostituisce zipalign: da Android 11 resources.arsc deve essere memorizzato non
compresso e a un offset multiplo di 4 byte; le librerie native vanno allineate a
4 KiB. Il padding viene inserito in un campo extra valido (id 0xD935).
"""
import binascii
import struct
import sys
import zipfile
import zlib

ALIGN = 4
SO_ALIGN = 4096
ALWAYS_STORED = ("resources.arsc",)
PAD_ID = 0xD935  # identificativo usato da zipalign di Android


def dos_datetime(dt):
    date = ((dt[0] - 1980) << 9) | (dt[1] << 5) | dt[2]
    time = (dt[3] << 11) | (dt[4] << 5) | (dt[5] // 2)
    return time, date


def padding_extra(size):
    """Campo extra di `size` byte (size == 0 oppure >= 4)."""
    if size == 0:
        return b""
    return struct.pack("<HH", PAD_ID, size - 4) + b"\x00" * (size - 4)


def align_apk(src, dst):
    zin = zipfile.ZipFile(src, "r")
    out = open(dst, "wb")
    records = []

    for info in zin.infolist():
        raw = zin.read(info.filename)
        crc = binascii.crc32(raw) & 0xFFFFFFFF
        name = info.filename.encode("utf-8")

        stored = info.compress_type == zipfile.ZIP_STORED or info.filename in ALWAYS_STORED
        if stored:
            payload, method = raw, zipfile.ZIP_STORED
        else:
            comp = zlib.compressobj(9, zlib.DEFLATED, -15)
            payload, method = comp.compress(raw) + comp.flush(), zipfile.ZIP_DEFLATED

        extra = b""
        if method == zipfile.ZIP_STORED:
            step = SO_ALIGN if info.filename.endswith(".so") else ALIGN
            header = 30 + len(name)
            pad = (-(out.tell() + header)) % step
            while 0 < pad < 4:
                pad += step
            extra = padding_extra(pad)

        offset = out.tell()
        time, date = dos_datetime(info.date_time)
        out.write(struct.pack("<IHHHHHIIIHH", 0x04034B50, 20, 0, method, time, date,
                              crc, len(payload), len(raw), len(name), len(extra)))
        out.write(name)
        out.write(extra)
        out.write(payload)
        records.append((info, name, method, crc, len(payload), len(raw), extra, offset))

    central = out.tell()
    for info, name, method, crc, csize, size, extra, offset in records:
        time, date = dos_datetime(info.date_time)
        out.write(struct.pack("<IHHHHHHIIIHHHHHII", 0x02014B50, 20, 20, 0, method, time, date,
                              crc, csize, size, len(name), len(extra), 0, 0, 0,
                              info.external_attr, offset))
        out.write(name)
        out.write(extra)
    end = out.tell()
    out.write(struct.pack("<IHHHHIIH", 0x06054B50, 0, 0, len(records), len(records),
                          end - central, central, 0))
    out.close()
    zin.close()


if __name__ == "__main__":
    align_apk(sys.argv[1], sys.argv[2])
