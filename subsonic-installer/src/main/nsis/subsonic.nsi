; subsonic.nsi

!include "MUI.nsh"

; The name of the installer
Name "Subsonic"

; The default installation directory
InstallDir $PROGRAMFILES\Subsonic

; Registry key to check for directory (so if you install again, it will
; overwrite the old one automatically)
InstallDirRegKey HKLM "Software\Subsonic" "Install_Dir"

;--------------------------------
;Interface Configuration

!define MUI_HEADERIMAGE
!define MUI_HEADERIMAGE_BITMAP "${NSISDIR}\Contrib\Graphics\Header\orange.bmp" ; optional

;--------------------------------
; Pages

!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_COMPONENTS
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_INSTFILES

!insertmacro MUI_UNPAGE_WELCOME
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES

;--------------------------------
; Languages
!insertmacro MUI_LANGUAGE "English"

;--------------------------------

; The stuff to install
Section "Subsonic Main"

  SectionIn RO

  ; Stop and uninstall service if present.
  ExecWait '"$INSTDIR\subsonic.exe" -stop'
  ExecWait '"$INSTDIR\subsonic.exe" -uninstall'

  ; Set output path to the installation directory.
  SetOutPath $INSTDIR

  ; Write files.
  File ..\..\..\..\subsonic-booter\target\subsonic.exe
  File ..\..\..\..\subsonic-booter\target\subsonic.exe.vmoptions
  File ..\..\..\..\subsonic-booter\target\subsonic-booter-jar-with-dependencies.jar
  File ..\..\..\..\subsonic-main\target\subsonic.war

  ; Write the installation path into the registry
  WriteRegStr HKLM SOFTWARE\Subsonic "Install_Dir" "$INSTDIR"

  ; Write the uninstall keys for Windows
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Subsonic" "DisplayName" "Subsonic"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Subsonic" "UninstallString" '"$INSTDIR\uninstall.exe"'
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Subsonic" "NoModify" 1
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Subsonic" "NoRepair" 1
  WriteUninstaller "uninstall.exe"

  ; Install and start service.
  ExecWait '"$INSTDIR\subsonic.exe" -install'
  ExecWait '"$INSTDIR\subsonic.exe" -start'

SectionEnd ; end the section

; Optional section (can be disabled by the user)
Section "Start Menu Shortcuts"

  CreateDirectory "$SMPROGRAMS\Subsonic"
  CreateShortCut "$SMPROGRAMS\Subsonic\Start Subsonic.lnk"     "$INSTDIR\subsonic.exe"  "-start" "$INSTDIR\subsonic.exe"  0
  CreateShortCut "$SMPROGRAMS\Subsonic\Stop Subsonic.lnk"      "$INSTDIR\subsonic.exe"  "-stop"  "$INSTDIR\subsonic.exe"  0
  CreateShortCut "$SMPROGRAMS\Subsonic\Uninstall Subsonic.lnk" "$INSTDIR\uninstall.exe" ""       "$INSTDIR\uninstall.exe" 0

SectionEnd

;--------------------------------

; Uninstaller

Section "Uninstall"

  ; Stop and uninstall service if present.
  ExecWait '"$INSTDIR\subsonic.exe" -stop'
  ExecWait '"$INSTDIR\subsonic.exe" -uninstall'

  ; Remove registry keys
  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Subsonic"
  DeleteRegKey HKLM SOFTWARE\Subsonic

  ; Remove files.
  Delete $INSTDIR\*.*

  ; Remove shortcuts, if any
  Delete "$SMPROGRAMS\Subsonic\*.*"

  ; Remove directories used
  RMDir "$SMPROGRAMS\Subsonic"
  RMDir "$INSTDIR"

SectionEnd
