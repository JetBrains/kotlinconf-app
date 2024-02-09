xcodebuild archive \
    -allowProvisioningUpdates \
    -workspace ./iosApp/KotlinConf.xcworkspace \
    -scheme iosApp \
    -archivePath \
    ./kotlinconf.xcarchive \
    CODE_SIGN_IDENTITY='' \
    CODE_SIGNING_REQUIRED='NO' \
    CODE_SIGNING_ALLOWED='NO'
