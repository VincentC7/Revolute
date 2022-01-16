#Prerequis
______
- Consul de lancé sur le port 8500
- JAVA 11

# Installation
______
Pour lancer la compilation et la création de tous les jars, ```mvn -DskipTests clean package``` à la racine.  

Il y a des tests dans le service banque et le service conversion. **Il fonctionnent uniquement dans la branche without-sercurity**


# Lancement
______
## Keycloak
Un fichier realm-export.json est placé à la racine du projet. Il faudra certainement regénérer une clé et la changer dans le application.properties du service banque.
Il faut créer un utilisateur avec un attribut "secret" qui est égale au "secret" enregistré dans le compte bancaire de l'usager. (le secret de l'exemple donnée ci-dessous est **7a53028a-765a-11ec-90d6-0242ac120003**)
## Docker
Ce projet utilise un docker-compose contenant la base de donnée du service banque, keycloack et la base de données de keycloak.
Pour le lancer ``docker up --build`` à la racine du projet.  

Pour accéder à la console admin : http://localhost:8180/auth/

##Banque service
``cd Banque-service``  
``java -jar -Dserver.port=8085 .\target\Banque-service-1.0.jar``  
``java -jar -Dserver.port=8086 .\target\Banque-service-1.0.jar`` si voulez en avoir un 2ème

##Conversion service
``cd Conversion-service``  
``java -jar -Dserver.port=8380 .\target\Conversion-service-1.0.jar``  
``java -jar -Dserver.port=8381 .\target\Conversion-service-1.0.jar`` si voulez en avoir un 2ème

## Marchand service
``cd Merchant-service``  
``java -jar -Dserver.port=9000 .\target\Merchant-service-1.0.jar``  
``java -jar -Dserver.port=9001 .\target\Merchant-service-1.0.jar`` si voulez en avoir un 2ème

## Gateway-banque
``cd Gateway-bank-service``
``java -jar -Dserver.port=8100 .\target\Gateway-bank-service-1.0.jar``

## Gateway-conversion
``cd Gateway-conversion-service``
``java -jar -Dserver.port=8101 .\target\Gateway-conversion-service-1.0.jar``


# Requêtes HTTP utiles
get http://localhost:8180/auth/realms/BanqueService/protocol/openid-connect/token : Permet de récupérer le token d'accès

## Banque-service
POST http://localhost:8085/accounts pour créer un compte
````json
{
    "name" : "Choquert",
    "surname" : "Vincent",
    "country" : "France",
    "birthday" : "27-07-1999",
    "passport" : "123456789",
    "tel" : "+0033638590012",
    "secret" : "7a53028a-765a-11ec-90d6-0242ac120003"
}
````

POST http://localhost:8085/accounts/{{accountId}}/cards pour créer une carte
````json
{  
  "code" : "5641",  
  "ceiling" : 1000.0,  
  "blocked" : false,  
  "contact" : true,  
  "virtual" : false,  
  "longitude" : 0.0,  
  "latitude" : 0.0  
}
````

## Marchant-service
POST http://localhost:9000/pay pour payer en euro
````json
{
  "cardNumber" : "à récupérer sur la requette de création d'une carte",
  "code" : "5641",
  "amount" : 100,
  "country" : "France",
  "crypto" : "à récupérer sur la requette de création d'une carte",
  "currency" : "EUR",
  "iban": "FR8712313668306362208622O17" 
}
````

POST http://localhost:9000/pay pour payer en dollars
````json
{
  "cardNumber" : "à récupérer sur la requette de création d'une carte",
  "code" : "5641",
  "amount" : 100,
  "country" : "France",
  "crypto" : "à récupérer sur la requette de création d'une carte",
  "currency" : "USD",
  "iban": "FR8712313668306362208622O17" 
}
````


#Documentation
http://localhost:8085/swagger-ui/index.html