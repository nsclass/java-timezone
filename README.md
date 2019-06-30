# java-timezone
Display timezone information in JDK

This application will expose the following API to query timezone information in current JDK.
It is using Springboot 2.0 Flux to implement HTTP REST API.

The following CURL command will display timezone information in current JDK.
```
curl http://localhost:8080/timezone
```

The following CURL will return all availabe Java timezone ID in current JDK
```
curl http://localhost:8080/timezoneIds
```

Output example
```
[
    {
        "daylightSavingRange": {
            "first": {
                "begin": false,
                "date": "Nov 3, 2019 1:00:00 AM"
            },
            "second": {
                "begin": true,
                "date": "Mar 8, 2020 3:00:00 AM"
            }
        },
        "dst": "GMT-7:00",
        "gmt": "GMT-8:00",
        "zoneId": "US/Pacific"
    },
    {
        "daylightSavingRange": {
            "first": {
                "begin": false,
                "date": "Oct 27, 2019 2:00:00 AM"
            },
            "second": {
                "begin": true,
                "date": "Mar 29, 2020 3:00:00 AM"
            }
        },
        "dst": "GMT+2:00",
        "gmt": "GMT+1:00",
        "zoneId": "Europe/Monaco"
    }
]
```

Timezone id example
```$xslt
[
    {
        "timezoneId": "US/Pacific"
    },
    {
        "timezoneId": "Europe/Monaco"
    }
]
```
