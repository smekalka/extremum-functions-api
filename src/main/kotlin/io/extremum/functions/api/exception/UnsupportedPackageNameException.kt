package io.extremum.functions.api.exception

class UnsupportedPackageNameException(packageName: String, availablePackageName: String) :
    ArgumentValidationException("Unsupported package name: $packageName. Available: $availablePackageName.")