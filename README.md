# Keycloak extension for First Party Native App
This extension is implemented to provide authorization-challenge endpoint, which is defined in this draft: [OAuth2.0 for first-party native apps](https://www.ietf.org/archive/id/draft-parecki-oauth-first-party-native-apps-00.html)

## Requirements
Before landing to this extension, you must have knowledge about:
- OAuth2.0 frameworks
- OpenID Connect
- Keycloak
- JWT (JWS & JWE)


## Installation
Use the package manager [mvn] to install kc-extension.

```bash
npm install
cd ./kc-extension
mvn clean package
```

Unit test
```bash
npm run test
```


## License

[MIT](https://choosealicense.com/licenses/mit/)