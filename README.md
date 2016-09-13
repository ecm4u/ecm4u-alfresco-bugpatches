[![Travis build status](https://api.travis-ci.org/ecm4u/ecm4u-alfresco-bugpatches.svg?branch=master)](https://travis-ci.org/ecm4u/ecm4u-alfresco-bugpatches)

# ecm4u Alfresco Bug Patches

This module contains a collection of bug fixes for Alfresco, both the
repository and Share.

The module is build for Alfresco 5.1e. If it is necessary to apply different
patches for other Alfresco versions, we will do this in branches. The master
branch is meant to contain patches for the current release of Alfresco CE.

## Build

To build the module, use Maven.

```
$ mvn package
```

## Deploy

Copy `ecm4u-bugpatches-repo-3.0-SNAPSHOT.amp` to the `amps` folder of your
Alfresco installation, copy `ecm4u-bugpatches-share-3.0-SNAPSHOT.amp` to
`amps_share`.

## Included Patches

* Enable controls of transient fields to be rendered in Sets (patches https://issues.alfresco.com/jira/browse/ACE-5441)
* Make a QuickShare document available even if it is in a different tenant than the requesting user.
* Fix for [ACE-4154](https://issues.alfresco.com/jira/browse/ACE-4154) - DataListDownloadWebScript should allow custom type Datalists
* Fis for [ALF-21749](https://issues.alfresco.com/jira/browse/ALF-21749) - Manage Sites page broken when users without a surname are synchronized from LDAP

## License

GNU LESSER GENERAL PUBLIC LICENSE Version 3
