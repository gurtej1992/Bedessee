## README FILE

## Aps in Bedessee
Legacy app 
- (Bedesse-Canada icon) Bedesee Sales - com.bedessee.sales20

Stable channel
- (Bedesse-Canada icon) Sales 19 - com.bedessee.sales21

Beta Channel
- (Shopping Cart) Sales 19 B - com.bedessee.sales22

Bedesse-Canada icon -> R.drawable.ic_launcher_var_original
Shopping Cart icon -> R.drawable.ic_launcher_var

Attention: To let the stable app and beta app coexist in a phone it is not enough to change the package name.
The attribute android:authorities on the provider in the manifest has to be unique.
```
     <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="com.bedessee.sales22.provider.File"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_provider_paths" />
        </provider>
```

Release steps:
- Increase the version number, it is the current date to versionCode / versionName
- Push and run any primary job that notifies by email.