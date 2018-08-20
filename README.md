# FirebaseChatAndroid

This Project uses firebase realtime database to create a chat application. Authentication is done with firebase Authentication. Attachments like image, video and pdf can be sent in this project. Attachments are first stored in firebase storage. The url thus formed is stored in realtime database as chat. Whenever there is attachment in the chat, it is downloaded from the url.

Permissions Required:
- Read External Storage
- Write External Storage
- Camera

Tool Used:
- Android Studio 3.0.0

Firebase Versions Used:
- firebase-auth:11.0.4
- firebase-database:11.0.4
- firebase-storage:11.0.4
- firebase-ui:2.3.0
- firebase-ui-database:2.3.0
