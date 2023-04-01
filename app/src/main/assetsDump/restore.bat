del ..\assets\database.db
del ..\assets\italiano-cei2008.db
del ..\assets\latino.db
del ..\assets\inglese-cpdv.db
sqlite3.exe ..\assets\database.db ".read database.txt"
sqlite3.exe ..\assets\italiano-cei2008.db ".read italiano-cei2008.txt"
sqlite3.exe ..\assets\latino.db ".read latino.txt"
sqlite3.exe ..\assets\inglese-cpdv.db ".read inglese-cpdv.txt"