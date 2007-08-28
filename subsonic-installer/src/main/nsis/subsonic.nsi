; example1.nsi
;
; This script is perhaps one of the simplest NSIs you can make. All of the
; optional settings are left to their default settings. The installer simply
; prompts the user asking them where to install, and drops a copy of example1.nsi
; there.

;--------------------------------

; The name of the installer
Name "Subsonic"

; The default installation directory
InstallDir $PROGRAMFILES\Subsonic

;--------------------------------

; Pages

Page directory
Page instfiles

;--------------------------------

; The stuff to install
Section "" ;No components page, name is not important

  SetOutPath $INSTDIR
  File ..\..\..\..\subsonic-booter\target\subsonic-booter-3.2.beta1-jar-with-dependencies.jar

  SetOutPath $INSTDIR\jetty
  File ..\..\..\..\subsonic-main\target\subsonic.war

SectionEnd ; end the section
