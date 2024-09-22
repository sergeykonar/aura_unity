# aura_unity
Technical task

App's functionality:
On Android API 34 user must provide permission for scheduling alarms
On Android API 31 and higher user must provider notification permission

Default settings:
Maximum Dismissals allowed: 5
Interval between dismissals (minutes): 1
Interval between notifications (minutes): 15

If the user dismissed the notification more than Maximum Dismissals allowed then the next notification will be scheduled in 15 mintes. Oherwise, when user dismiss notification, it will be rescheduled according to Interval between dismissals

The app uses AlarmManager to schedule activity at exect time

TODO:
Test on different devices from different manufacturers
Improve UI


