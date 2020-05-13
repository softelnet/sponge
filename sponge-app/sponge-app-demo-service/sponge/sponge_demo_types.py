"""
Sponge Knowledge base
Demo - Data types
"""

from java.time import LocalDateTime, LocalDate, LocalTime, ZonedDateTime, Instant, ZoneId

class AnyTypeArgAction(Action):
    def onConfigure(self):
        self.withLabel("Type any arg action").withArgs([
            AnyType("arg").withLabel("Any").withNullable(),
        ]).withNonCallable()

class AnyTypeResultAction(Action):
    def onConfigure(self):
        self.withLabel("Type any result action").withNoArgs().withResult(AnyType("arg").withLabel("Any").withNullable())
    def onCall(self):
        return None

class BooleanTypeArgAction(Action):
    def onConfigure(self):
        self.withLabel("Type boolean arg action")
        self.withArgs([
            BooleanType("arg").withLabel("Boolean non nullable").withDefaultValue(False),
            BooleanType("argNullable").withLabel("Boolean nullable").withNullable(),
            BooleanType("argReadOnlyNullable").withLabel("Boolean read only nullable").withReadOnly().withNullable().withDefaultValue(False),
            BooleanType("argReadOnlyNonNullable").withLabel("Boolean read only non nullable").withReadOnly(),
        ]).withNonCallable()

class DateTimeTypeAction(Action):
    def onConfigure(self):
        self.withLabel("Type date-time action").withDescription("Shows date-time type as arguments.")
        self.withArgs([
            DateTimeType("arg").withLabel("Date-time non nullable"),
            DateTimeType("argNullable").withLabel("Date-time nullable").withNullable(),
            DateTimeType("argDefault").withLabel("Date-time with default").withDefaultValue(LocalDateTime.of(2020, 1, 1, 12, 0)),
            DateTimeType("argReadOnlyNullable").withLabel("Date-time read only nullable").withReadOnly().withNullable()
                .withDefaultValue(LocalDateTime.of(2020, 1, 1, 12, 0)),
            DateTimeType("argReadOnlyNonNullable").withLabel("Date-time read only non nullable").withReadOnly()
                .withDefaultValue(LocalDateTime.of(2020, 1, 1, 12, 0)),

            DateTimeType("argDate").withDate().withLabel("Date non nullable"),
            DateTimeType("argDateNullable").withDate().withLabel("Date nullable").withNullable(),
            DateTimeType("argDateDefault").withDate().withLabel("Date with default").withDefaultValue(LocalDate.of(2020, 1, 1)),
            DateTimeType("argDateReadOnlyNullable").withDate().withLabel("Date read only nullable").withReadOnly().withNullable()
                .withDefaultValue(LocalDate.of(2020, 1, 1)),
            DateTimeType("argDateReadOnlyNonNullable").withDate().withLabel("Date read only non nullable").withReadOnly()
                .withDefaultValue(LocalDate.of(2020, 1, 1)),

            DateTimeType("argTime").withTime().withFormat("HH:mm").withLabel("Time non nullable"),
            DateTimeType("argTimeNullable").withTime().withFormat("HH:mm").withLabel("Time nullable").withNullable(),
            DateTimeType("argTimeDefault").withTime().withFormat("HH:mm").withLabel("Time with default").withDefaultValue(LocalTime.of(12, 0)),
            DateTimeType("argTimeReadOnlyNullable").withTime().withFormat("HH:mm").withLabel("Time read only nullable").withReadOnly().withNullable()
                .withDefaultValue(LocalTime.of(12, 0)),
            DateTimeType("argTimeReadOnlyNonNullable").withTime().withFormat("HH:mm").withLabel("Time read only non nullable").withReadOnly()
                .withDefaultValue(LocalTime.of(12, 0)),

            DateTimeType("argDateTimeZoneReadOnlyNullable").withDateTimeZone().withLabel("Date-time-zone read only nullable").withReadOnly().withNullable()
                .withDefaultValue(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 12, 0), ZoneId.of("America/Detroit"))),
            DateTimeType("argDateTimeZoneReadOnlyNonNullable").withDateTimeZone().withLabel("Date-time-zone read only non nullable").withReadOnly()
                .withDefaultValue(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 12, 0), ZoneId.of("America/Detroit"))),

            DateTimeType("argInstantReadOnlyNullable").withInstant().withLabel("Instant read only nullable").withReadOnly().withNullable()
                .withDefaultValue(Instant.now()),
            DateTimeType("argInstantReadOnlyNonNullable").withInstant().withLabel("Instant read only non nullable").withReadOnly()
                .withDefaultValue(Instant.now()),

            # DATE_TIME_ZONE and INSTANT editing is not supported yet.
        ]).withNonCallable()
