# CryptoClaim

CryptoClaim is RESTful application that shall be used for sending messages to other users(of the app) in secure and protected way. The application provides authentication based on JSON Web Tokens(https://tools.ietf.org/html/rfc7519). Encryption of messages and keys is performed in several cases. For detailed view of the application see the architecture section. 

The application shall run in Cloud Foundry Environment. Its deployment configurations are set in the manifest.yml file. Zero-downtime deployment is considered and should be implemented soon.