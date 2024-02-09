# Enunciat de les fites PJ02-DBAPI #

### Preparació de la base de dades ###
Podeu arrancar una instància de MySQL amb Docker
```bash
docker run --name mysql-dbapi -it -e MYSQL_ROOT_PASSWORD=pass -e MYSQL_DATABASE=dbapi -e MYSQL_USER=usuari -e MYSQL_PASSWORD=password -p 3306:3306 mysql
```

I aturar-la i destuir-la amb
```bash
docker stop mysql-dbapi

docker rm mysql-dbapi
```

En cas de no eliminar-se ho podem forçar amb
```bash
docker rm -f mysql-dbapi
```


### Compilació i funcionament ###

Cal el 'Maven' per compilar el projecte
```bash
mvn clean
mvn compile
mvn test
mvn clean compile test
```

Per executar el projecte a Windows cal
```bash
.\run.ps1 cat.iesesteveterradas.dbapi.AppMain
```

Per executar el projecte a Linux/macOS cal
```bash
./run.sh cat.iesesteveterradas.dbapi.AppMain
```
