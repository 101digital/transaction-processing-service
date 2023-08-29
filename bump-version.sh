echo bumping version to $1
mvn versions:set -DnewVersion=$1 -DprocessAllModules -DgenerateBackupPoms=false
echo bumped to $1