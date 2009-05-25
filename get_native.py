#!/usr/bin/env python

import re
import shutil
import subprocess
import sys
import os
import urllib2

#
# Perhaps I should just ask if they'll provide the libraries directly...
#

DATA_DIRECTORY="installers"

WINDOWS_X86_URL="http://software.jessies.org/downloads/windows/terminator.msi"
LINUX_X86_URL="http://software.jessies.org/downloads/debian/org.jessies.terminator.i386.deb"
LINUX_AMD64_URL="http://software.jessies.org/downloads/debian/org.jessies.terminator.amd64.deb"
OSX_URL="http://software.jessies.org/downloads/mac/terminator.dmg"

WINDOWS_X86_OUT="native/windows/x86"
LINUX_X86_OUT="native/linux/x86"
LINUX_AMD64_OUT="native/linux/amd64"
OSX_OUT="native/osx/universal"

def url2file(url):
    u = urllib2.urlopen(url)
    f = open(os.path.join(DATA_DIRECTORY, os.path.split(url)[-1]), "w")
    shutil.copyfileobj(u, f)
    u.close()
    f.close()

def extract_osx(dmg, outdir):
    pass

def extract_linux(deb, outdir):
    data_file = os.path.join(DATA_DIRECTORY, "data.tar.gz")
    subprocess.Popen(["ar", "p", deb, "data.tar.gz"], stdout=open(data_file, "w")).communicate()
    entries = subprocess.Popen(["tar", "tzf", data_file], stdout=subprocess.PIPE).communicate()[0]
    entries = entries.split("\n")
    libraries = (x for x in entries if x.endswith(".so"))
    for library in libraries:
        filename = os.path.split(library)[-1]
        subprocess.Popen(["tar", "zxf", data_file, library, "-O"], stdout=open(os.path.join(outdir, filename), "w")).communicate()
    

MSI_FILE_RE=re.compile("(.*?)(file[0-9]+)(.*)")

def extract_windows(msi, outdir):
    # We don't really understand msi files but this will do for now...
    print "Grabbing msi string data"
    strings = subprocess.Popen(["7z", "e", "-i!*StringData", "-so",  msi], stdout=subprocess.PIPE).communicate()[0]
    print "Extracting cab from msi"
    subprocess.call(["7z", "e", "-i!project.cab", "-o" + DATA_DIRECTORY, "-y", msi])
    for string in strings.split("|"):
        match = MSI_FILE_RE.match(string)
        if match:
            external = match.group(1)
            internal = match.group(2)
            if os.path.splitext(external)[1] == '.dll':
                print "Extracting dll " + external
                subprocess.call(["7z", "e", "-i!" + internal, "-o" + WINDOWS_X86_OUT, os.path.join(DATA_DIRECTORY, "project.cab")])
                os.rename(os.path.join(WINDOWS_X86_OUT, internal), os.path.join(WINDOWS_X86_OUT, external))

if __name__ == "__main__":
    if not os.path.exists(DATA_DIRECTORY):
        os.mkdir(DATA_DIRECTORY)
    for url in [WINDOWS_X86_URL, LINUX_X86_URL, LINUX_AMD64_URL, OSX_URL]:
        pass #url2file(url)

    extract_windows(os.path.join(DATA_DIRECTORY, "terminator.msi"), WINDOWS_X86_OUT)
    extract_linux(os.path.join(DATA_DIRECTORY, "org.jessies.terminator.i386.deb"), LINUX_X86_OUT)
    extract_linux(os.path.join(DATA_DIRECTORY, "org.jessies.terminator.amd64.deb"), LINUX_AMD64_OUT)
    extract_osx(os.path.join(DATA_DIRECTORY, "terminator.dmg"), OSX_OUT)

