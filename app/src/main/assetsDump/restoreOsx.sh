rm ../assets/database.db
rm ../assets/italiano-cei2008.db
rm ../assets/latino.db
rm ../assets/inglese-cpdv.db
./sqlite3Osx ../assets/database.db ".read database.txt"
./sqlite3Osx ../assets/italiano-cei2008.db ".read italiano-cei2008.txt"
./sqlite3Osx ../assets/latino.db ".read latino.txt"
./sqlite3Osx ../assets/inglese-cpdv.db ".read inglese-cpdv.txt"