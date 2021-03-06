###########################################################
## Script file to replace hosts file with a different file
## using sudo commands. Executed from sgtest cloud suites
## that modify hosts file on startup.
##
## Author: Barak Merimovich
###########################################################

export modifiedFile=$1
export hostsFile=$2 
export backupFile=$3

echo Copying $modifiedFile to $hostsFile, saving backup at $backupFile

echo Verifying passwordless sudo

sudo -n ls > /dev/null 
if [ $? -ne 0 ]; then
	echo "Current user is not a passwordless sudoer"
	exit 1
fi

if [ -f $backupFile ]; then
	echo Found existing hosts backup file - reverting hosts
	sudo cp $backupFile $hostsFile
	sudo rm $backupFile
fi

sudo cp $hostsFile $backupFile
sudo cp $modifiedFile $hostsFile
