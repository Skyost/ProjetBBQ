; -- INSTALLATION DE PROJET BBQ �L�VE -- 

[Setup]
; ID de l'application
AppId=ProjetBBQEleve
; Nom de l'application :
AppName=Projet BBQ �l�ve
; Version de l'application :
AppVersion=0.1.2
; Dossier d'installation par d�faut :
DefaultDirName={pf}\Projet BBQ �l�ve
; Dossier dans le menu d�marrer par d�faut :
DefaultGroupName=Projet BBQ �l�ve
; Ic�ne du d�sinstallateur (dans Ajouter/Supprimer des programmes) :
UninstallDisplayIcon={app}\ProjetBBQEleve.exe
; Nom du d�sinstallateur (dans Ajouter/Supprimer des programmes) :
UninstallDisplayName=Projet BBQ �l�ve v0.1.2
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
VersionInfoDescription=ProjetBBQ �l�ve
VersionInfoProductName=ProjetBBQ �l�ve
VersionInfoProductTextVersion=v0.1.2
VersionInfoProductVersion=0.1.2.0
VersionInfoTextVersion=v0.1.2  
VersionInfoVersion=0.1.2.0

[Languages]
; La liste des langages disponibles :
Name: en; MessagesFile: "compiler:Default.isl"
Name: fr; MessagesFile: "compiler:Languages\French.isl"

[CustomMessages]
; Messages anglais :
en.additionals=Additional tasks :
en.taskstart=Start with Windows
en.addtodesktop=Add an icon on the desktop
en.uninstall=Uninstall
en.runapp=Run Projet BBQ �l�ve (used only to generate the configuration at the first launch)
en.openfolder=Open the installation directory (to edit the configuration)
; Messages fran�ais :
fr.additionals=T�ches additionnelles :
fr.taskstart=D�marrer avec Windows
fr.addtodesktop=Ajouter une ic�ne au bureau
fr.uninstall=D�sinstaller
fr.runapp=�xecuter Projet BBQ �l�ve (ne permet que de g�n�rer la configuration au premier lancement)
fr.openfolder=Ouvrir le dossier d'installation (pour �diter la configuration)

[Files]
; Les fichiers de l'application � inclure dans l'installateur :
Source: "ProjetBBQEleve.exe"; DestDir: "{app}"
Source: "KillEleve.exe"; DestDir: "{app}"
Source: "Lisez-moi !.txt"; DestDir: "{app}"

[Icons]
; Ic�ne de l'application (menu d�marrer) :
Name: "{group}\Projet BBQ �l�ve"; Filename: "{app}\ProjetBBQEleve.exe"
; Ic�ne de l'application (bureau) :
Name: "{userdesktop}\Projet BBQ �l�ve"; Filename: "{app}\ProjetBBQEleve.exe"; Tasks: TaskDesktop
; Ic�ne du d�sinstallateur (menu d�marrer) :
Name: "{group}\{cm:uninstall}"; Filename: "{uninstallexe}"

[Tasks]
; La t�che suppl�mentaire qui permet de d�marrer l'application avec Windows :
Name: TaskStart; Description: {cm:taskstart}; GroupDescription: {cm:additionals}
; La t�che suppl�mentaire qui permet de d'ajouter une ic�ne sur le bureau :
Name: TaskDesktop; Description: {cm:addtodesktop}; GroupDescription: {cm:additionals}

[Registry]
; On souhaite d�marrer l'application au d�marrage de Windows si la t�che TaskStart est s�l�ctionn�e :
Root: HKLM; Subkey: "SOFTWARE\Microsoft\Windows\CurrentVersion\Run"; ValueType: string; ValueName: "ProjetBBQEleve"; ValueData: """{app}\ProjetBBQEleve.exe"""; Flags: uninsdeletevalue; Tasks: TaskStart

[Run]
; Permet d'�xecuter le logiciel apr�s l'installation :
Filename: "{app}\ProjetBBQEleve.exe"; Description: {cm:runapp}; Flags: postinstall nowait skipifsilent
; Permet d'ouvrir le dossier d'installation :
Filename: "{app}"; Description: {cm:openfolder}; Flags: postinstall nowait skipifsilent shellexec