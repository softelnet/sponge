"""
Sponge Knowledge base
Action metadata DateTime type
"""

class DateTimeAction(Action):
    def onConfigure(self):
        self.withArgs([
            DateTimeType("dateTime").withDateTime(),
            DateTimeType("dateTimeZone").withDateTimeZone(),
            DateTimeType("date").withDate(),
            DateTimeType("time").withTime(),
            DateTimeType("instant").withInstant()
        ]).withResult(ListType())
    def onCall(self, dateTime, dateTimeZone, date, time, instant):
        return [dateTime, dateTimeZone, date, time, instant]
