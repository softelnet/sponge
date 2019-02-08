"""
Sponge Knowledge base
Action metadata DateTime type
"""

class DateTimeAction(Action):
    def onConfigure(self):
        self.withArgs([
            ArgMeta("dateTime", DateTimeType().withDateTime()),
            ArgMeta("dateTimeZone", DateTimeType().withDateTimeZone()),
            ArgMeta("date", DateTimeType().withDate()),
            ArgMeta("time", DateTimeType().withTime()),
            ArgMeta("instant", DateTimeType().withInstant())
        ]).withResult(ResultMeta(ListType(AnyType())))
    def onCall(self, dateTime, dateTimeZone, date, time, instant):
        return [dateTime, dateTimeZone, date, time, instant]
