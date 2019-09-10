# operator-cyclecounter

Takes value and timestamp readings and counts how often the value has been over a configurable threshold this day/month/year.
To count actual cycles, you can can configure a minimum duration before a new event is recognized.

Example use case: Count how many times you drank coffee by using a smart plug on your coffee machine.
Set the threshold to a value that your coffee machine exceeds when making a coffee (like 1000W during heating)
and the minimum duration to the time your machine needs to make it.

## Inputs

* value (float): Reading from a sensor
* timestamp (String): Timestamp of that reading

## Outputs

* DayTimestamp (string): String representation of the timestamp of the end of the current day
* DayCount (float): Number of cycles detected this day 
* MonthTimestamp (string): String representation of the timestamp of the end of the current month
* MonthCount (float): Number of cycles detected this month 
* YearTimestamp (string): String representation of the timestamp of the end of the current year
* YearCount (float): Number of cycles detected this year

## Configs

* Threshold (string): Minimum value to be recognized as an event, default: '-1'
* minDuration (string): Minimum duration in milliseconds before a new event can be recognized, default: '0'
* Timezone (string): Used to determine end of day/month/year. Can be anything able to be parsed by [ZoneId.of(String)](https://docs.oracle.com/javase/8/docs/api/java/time/ZoneId.html#of-java.lang.String-), default: '+02:00'
