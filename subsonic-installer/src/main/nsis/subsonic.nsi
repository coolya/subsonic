# subsonic.nsi

!include "WordFunc.nsh"
!include "MUI.nsh"

!insertmacro VersionCompare

# The name of the installer
Name "Subsonic"

# The default installation directory
InstallDir $PROGRAMFILES\Subsonic

# Registry key to check for directory (so if you install again, it will
# overwrite the old one automatically)
InstallDirRegKey HKLM "Software\Subsonic" "Install_Dir"

#--------------------------------
#Interface Configuration

!define MUI_HEADERIMAGE
!define MUI_HEADERIMAGE_BITMAP "${NSISDIR}\Contrib\Graphics\Header\orange.bmp"

#--------------------------------
# Pages

# This page checks for JRE
Page custom CheckInstalledJRE

!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_INSTFILES

!insertmacro MUI_UNPAGE_WELCOME
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES

# Languages
!insertmacro MUI_LANGUAGE "English"

Section "Subsonic"

  SectionIn RO

  # Stop and uninstall service if present.
  ExecWait '"$INSTDIR\subsonic.exe" -stop'
  ExecWait '"$INSTDIR\subsonic.exe" -uninstall'

  # Set output path to the installation directory.
  SetOutPath $INSTDIR

  # Write files.
  File ..\..\..\..\subsonic-booter\target\subsonic.exe
  File ..\..\..\..\subsonic-booter\target\subsonic.exe.vmoptions
  File ..\..\..\..\subsonic-booter\target\subsonic-booter-jar-with-dependencies.jar
  File ..\..\..\..\subsonic-main\LICENSE.TXT
  File ..\..\..\..\subsonic-main\README.TXT
  File ..\..\..\..\subsonic-main\target\subsonic.war
  File ..\..\..\..\subsonic-main\target\classes\version.txt
  File ..\..\..\..\subsonic-main\target\classes\build_number.txt

  # Write the installation path into the registry
  WriteRegStr HKLM SOFTWARE\Subsonic "Install_Dir" "$INSTDIR"

  # Write the uninstall keys for Windows
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Subsonic" "DisplayName" "Subsonic"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Subsonic" "UninstallString" '"$INSTDIR\uninstall.exe"'
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Subsonic" "NoModify" 1
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Subsonic" "NoRepair" 1
  WriteUninstaller "uninstall.exe"

  # Install and start service.
  ExecWait '"$INSTDIR\subsonic.exe" -install'
  ExecWait '"$INSTDIR\subsonic.exe" -start'

SectionEnd


Section "Start Menu Shortcuts"

  CreateDirectory "$SMPROGRAMS\Subsonic"
  CreateShortCut "$SMPROGRAMS\Subsonic\Start Subsonic.lnk"     "$INSTDIR\subsonic.exe"  "-start" "$INSTDIR\subsonic.exe"  0
  CreateShortCut "$SMPROGRAMS\Subsonic\Stop Subsonic.lnk"      "$INSTDIR\subsonic.exe"  "-stop"  "$INSTDIR\subsonic.exe"  0
  CreateShortCut "$SMPROGRAMS\Subsonic\Uninstall Subsonic.lnk" "$INSTDIR\uninstall.exe" ""       "$INSTDIR\uninstall.exe" 0

SectionEnd


# Uninstaller

Section "Uninstall"

  # Stop and uninstall service if present.
  ExecWait '"$INSTDIR\subsonic.exe" -stop'
  ExecWait '"$INSTDIR\subsonic.exe" -uninstall'

  # Remove registry keys
  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Subsonic"
  DeleteRegKey HKLM SOFTWARE\Subsonic

  # Remove files.
  Delete $INSTDIR\*.*

  # Remove shortcuts, if any
  Delete "$SMPROGRAMS\Subsonic\*.*"

  # Remove directories used
  RMDir "$SMPROGRAMS\Subsonic"
  RMDir "$INSTDIR"

SectionEnd


Function CheckInstalledJRE
    # Read the value from the registry into the $0 register
    ReadRegStr $0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" CurrentVersion

    # Check JRE version. At least 1.5 is required.
    #   $1=0  Versions are equal
    #   $1=1  Installed version is newer
    #   $1=2  Installed version is older (or non-existent)
    ${VersionCompare} $0 "1.5" $1
    IntCmp $1 2 InstallJRE 0 0
    Return

    InstallJRE:
      # Launch Java web installer.
      MessageBox MB_OK "Java 6 was not found and will now be installed."
      File /oname=$TEMP\jre-setup.exe jre-6u2-windows-i586-p-iftw.exe
      ExecWait '"$TEMP\jre-setup.exe"' $0
      Delete "$TEMP\jre-setup.exe"

FunctionEnd
