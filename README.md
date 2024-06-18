# App Recipe Sharing
A recipe sharing app that allows users to discover and save recipes, and create user favorite recipe lists. It include the use of NLP algorithms, async threads, Firebase and so much more.

# Installation
1. download Java 11 (or higher) if not downloaded already
2. download Android Studio Giraffe:
   https://drive.google.com/file/d/1r1F3zt9LD8d40QW2ATxNabAsWQLlKQut/view - the installer
3. open the Android Studio project, go into Device Manager, select Create Device, and choose any device that has the Play Store logo.
   it is essiential that you download a device with the Play Store logo in order for all the features to work.
   here is the images to guide you installing the Device:
   (https://github.com/nahoom567/recipeSharing/assets/128990420/fd750ac9-e2e7-4d29-9274-88f6715ca554) - press Device Manager
   (https://github.com/nahoom567/recipeSharing/assets/128990420/af973eaa-d2e3-45c8-846d-f17f20358ff7) - press Create Device
   (https://github.com/nahoom567/recipeSharing/assets/128990420/eb24c333-9de1-4240-871c-d45fe09720bd) - choose any device with the Play Store logo

# Features
- [x] Realtime Database and Authentication in Firebase
- [x] Registration and Login to users
- [x] Adding and Changing recipes
- [x] Searching for other people's recipes
- [x] Each user has his/her own inbox
- [x] Every user can like any recipes they want
- [x] Option to remind the user that they liked a recipe, after a certain amount of time has passed
- [x] Reminders for the recipes happen automatically even if the user is not connected to the application at the time
- [x] Several users can be connected to the application at the same time, in a way that the data displayed to the user will change at real time without the need to refresh
- [x] Complex transition Animation between Activities
- [x] Using autocorrect to search for the recipes, so that the searches will be corrected automatically assuming that there are recipes with similar names (using NLP algorithms and threshold of normalized values returned from the algorithms)
