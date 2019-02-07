"""
Sponge Knowledge base
Action metadata DateTime type
"""

class DateTimeAction(Action):
    def onConfigure(self):
        self.argsMeta = [
            ArgMeta("dateTime", DateTimeType().dateTime()),
            ArgMeta("dateTimeZone", DateTimeType().dateTimeZone()),
            ArgMeta("date", DateTimeType().date()),
            ArgMeta("time", DateTimeType().time()),
            ArgMeta("instant", DateTimeType().instant()),
        ]
        self.resultMeta = ResultMeta(ListType(AnyType()))
    def onCall(self, dateTime, dateTimeZone, date, time, instant):
        return [dateTime, dateTimeZone, date, time, instant]
