; -- INSTALLATION DE PROJET BBQ PROFESSEUR -- 

[Setup]
; ID de l'application
AppId=ProjetBBQProf
; Nom de l'application :
AppName=Projet BBQ Professeur
; Version de l'application :
AppVersion=0.1
; Dossier d'installation par d�faut :
DefaultDirName={pf}\Projet BBQ Professeur
; Dossier dans le menu d�marrer par d�faut :
DefaultGroupName=Projet BBQ Professeur
; Ic�ne du d�sinstallateur (dans Ajouter/Supprimer des programmes) :
UninstallDisplayIcon={app}\ProjetBBQProf.exe
; Nom du d�sinstallateur (dans Ajouter/Supprimer des programmes) :
UninstallDisplayName=Projet BBQ Professeur v0.1
; Ic�ne de l'installateur :
SetupIconFile=res\Icon.ico
; Grande ic�ne sur la gauche :
WizardImageFile=res\WizardImageFile.bmp
; Petite ic�ne en haut � droite :
WizardSmallImageFile=res\WizardSmallImageFile.bmp
; Licence :
LicenseFile=res\LICENSE.txt 
; Autres :
OutputBaseFilename=Installer
DisableWelcomePage=no
UsePreviousAppDir=yes
AllowNoIcons=yes

; Informations sur l'application (dans Ajouter/Supprimer des programmes) :
AppPublisher=Groupe ISN
AppPublisherURL=https://github.com/Skyost/ProjetBBQ
AppContact=Aide, cr�dits et d�pendances : https://github.com/Skyost/ProjetBBQ/wiki.
AppSupportURL=https://github.com/Skyost/ProjetBBQ/wiki
AppUpdatesURL=https://github.com/Skyost/ProjetBBQ/releases

; Informations sur l'application (dans l'explorateur) :
VersionInfoCompany=Groupe ISN
VersionInfoCopyright=Voir liste des d�pendances ici : https://github.com/Skyost/ProjetBBQ/wiki. 
VersionInfoDescription=ProjetBBQ Professeur
VersionInfoProductName=ProjetBBQ Professeur
VersionInfoProductTextVersion=v0.1
VersionInfoProductVersion=0.1.0.0
VersionInfoTextVersion=v0.1
VersionInfoVersion=0.1.0.0

[Languages]
; La liste des langages disponibles :
Name: en; MessagesFile: "compiler:Default.isl"
Name: fr; MessagesFile: "compiler:Languages\French.isl"

[CustomMessages]
; Messages anglais :
en.additionals=Additional tasks :
en.addtodesktop=Add an icon on the desktop
en.uninstall=Uninstall
en.runapp=Run Projet BBQ Professeur (will show an error on the first launch)
en.openfolder=Open the installation directory (to edit the configuration and add rooms)
; Messages fran�ais :
fr.additionals=T�ches additionnelles :
fr.addtodesktop=Ajouter une ic�ne au bureau
fr.uninstall=D�sinstaller
fr.runapp=�xecuter Projet BBQ Professeur (affichera une erreur au premier lancement)
fr.openfolder=Ouvrir le dossier d'installation (pour �diter la configuration et ajouter des salles)

[Files]
; Le fichier de l'application � inclure dans l'installateur :
Source: "ProjetBBQProf.exe"; DestDir: "{app}"
Source: "Lisez-moi !.txt"; DestDir: "{app}"

[Icons]
; Ic�ne de l'application (menu d�marrer) :
Name: "{group}\Projet BBQ Professeur"; Filename: "{app}\ProjetBBQProf.exe"
; Ic�ne de l'application (bureau) :
Name: "{userdesktop}\Projet BBQ Professeur"; Filename: "{app}\ProjetBBQProf.exe"; Tasks: TaskDesktop
; Ic�ne du d�sinstallateur (menu d�marrer) :
Name: "{group}\{cm:uninstall}"; Filename: "{uninstallexe}"

[Tasks]
; La t�che suppl�mentaire qui permet de d'ajouter une ic�ne sur le bureau :
Name: TaskDesktop; Description: {cm:addtodesktop}; GroupDescription: {cm:additionals}

[Run]
; Permet d'�xecuter le logiciel apr�s l'installation :
Filename: "{app}\ProjetBBQEleve.exe"; Description: {cm:runapp}; Flags: postinstall nowait skipifsilent
; Permet d'ouvrir le dossier d'installation :
Filename: "{app}"; Description: {cm:openfolder}; Flags: postinstall nowait skipifsilent shellexec