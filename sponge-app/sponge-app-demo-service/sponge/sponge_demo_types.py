"""
Sponge Knowledge base
Demo - Data types
"""

from java.time import LocalDateTime, LocalDate, LocalTime, ZonedDateTime, Instant, ZoneId

class AnyTypeArgAction(Action):
    def onConfigure(self):
        self.withLabel("Type any arg action").withArgs([
            AnyType("arg").withLabel("Any").withNullable(),
        ]).withResult(StringType())
    def onCall(self, arg):
        return None

class AnyTypeResultAction(Action):
    def onConfigure(self):
        self.withLabel("Type any result action").withNoArgs().withResult(AnyType().withLabel("Any").withNullable())
    def onCall(self):
        return None

# TODO BinaryType

class BooleanTypeArgAction(Action):
    def onConfigure(self):
        self.withLabel("Type boolean arg action")
        self.withArgs([
            BooleanType("arg").withLabel("Boolean non nullable").withDefaultValue(False),
            BooleanType("argNullable").withLabel("Boolean nullable").withNullable(),
            BooleanType("argReadOnlyNullable").withLabel("Boolean read only nullable").withReadOnly().withNullable(),
            BooleanType("argReadOnlyNonNullable").withLabel("Boolean read only non nullable").withReadOnly().withDefaultValue(False),
        ]).withResult(StringType())
    def onCall(self, arg, argNullable, argReadOnlyNullable, argReadOnlyNonNullable):
        return "/".join(map(lambda x: str(x), [arg, argNullable, argReadOnlyNullable, argReadOnlyNonNullable]))

class BooleanTypeResultAction(Action):
    def onConfigure(self):
        self.withLabel("Type boolean result action")
        self.withArg(StringType("type").withProvided(ProvidedMeta().withValueSet())).withResult(DynamicType())
    def onCall(self, type):
        if type == "nonNullable":
            return DynamicValue(False, BooleanType().withLabel("Boolean non nullable").withDefaultValue(False))
        elif type == "nullable":
            return DynamicValue(None, BooleanType().withLabel("Boolean nullable").withNullable())
        elif type == "readOnlyNullable":
            return DynamicValue(None, BooleanType().withLabel("Boolean read only nullable").withReadOnly().withNullable())
        elif type == "readOnlyNonNullable":
            return DynamicValue(False, BooleanType().withLabel("Boolean read only non nullable").withReadOnly())
        else:
            return None
    def onProvideArgs(self, context):
        if "type" in context.provide:
            context.provided["type"] = ProvidedValue().withValueSet(["nonNullable", "nullable", "readOnlyNullable", "readOnlyNonNullable"])

class DateTimeTypeAction(Action):
    def onConfigure(self):
        self.withLabel("Type date-time arg action")
        self.withArgs([
            DateTimeType("arg").withLabel("Date-time non nullable").withDefaultValue(LocalDateTime.of(2020, 1, 1, 12, 0)),
            DateTimeType("argNullable").withLabel("Date-time nullable").withNullable(),
            DateTimeType("argDefault").withLabel("Date-time with default").withDefaultValue(LocalDateTime.of(2020, 1, 1, 12, 0)),
            DateTimeType("argReadOnlyNullable").withLabel("Date-time read only nullable").withReadOnly().withNullable()
                .withDefaultValue(LocalDateTime.of(2020, 1, 1, 12, 0)),
            DateTimeType("argReadOnlyNonNullable").withLabel("Date-time read only non nullable").withReadOnly()
                .withDefaultValue(LocalDateTime.of(2020, 1, 1, 12, 0)),

            DateTimeType("argDate").withDate().withLabel("Date non nullable").withDefaultValue(LocalDate.of(2020, 1, 1)),
            DateTimeType("argDateNullable").withDate().withLabel("Date nullable").withNullable(),
            DateTimeType("argDateDefault").withDate().withLabel("Date with default").withDefaultValue(LocalDate.of(2020, 1, 1)),
            DateTimeType("argDateReadOnlyNullable").withDate().withLabel("Date read only nullable").withReadOnly().withNullable()
                .withDefaultValue(LocalDate.of(2020, 1, 1)),
            DateTimeType("argDateReadOnlyNonNullable").withDate().withLabel("Date read only non nullable").withReadOnly()
                .withDefaultValue(LocalDate.of(2020, 1, 1)),

            DateTimeType("argTime").withTime().withFormat("HH:mm").withLabel("Time non nullable").withDefaultValue(LocalTime.of(12, 0)),
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
        ]).withResult(StringType())
    def onCall(self, arg, argNullable, argDefault, argReadOnlyNullable, argReadOnlyNonNullable,
               argDate, argDateNullable, argDateDefault, argDateReadOnlyNullable, argDateReadOnlyNonNullable,
               argTime, argTimeNullable, argTimeDefault, argTimeReadOnlyNullable, argTimeReadOnlyNonNullable,
               argDateTimeZoneReadOnlyNullable, argDateTimeZoneReadOnlyNonNullable,
               argInstantReadOnlyNullable, argInstantReadOnlyNonNullable):
        return "/".join(map(lambda x: str(x), [arg, argNullable, argDefault, argReadOnlyNullable, argReadOnlyNonNullable,
               argDate, argDateNullable, argDateDefault, argDateReadOnlyNullable, argDateReadOnlyNonNullable,
               argTime, argTimeNullable, argTimeDefault, argTimeReadOnlyNullable, argTimeReadOnlyNonNullable,
               argDateTimeZoneReadOnlyNullable, argDateTimeZoneReadOnlyNonNullable,
               argInstantReadOnlyNullable, argInstantReadOnlyNonNullable]))

class DateTimeTypeResultAction(Action):
    def onConfigure(self):
        self.withLabel("Type date-time result action")
        self.withArg(StringType("type").withProvided(ProvidedMeta().withValueSet())).withResult(DynamicType())
    def onCall(self, type):
        if type == "nonNullable":
            return DynamicValue(LocalDateTime.of(2020, 1, 1, 12, 0), DateTimeType().withLabel("Date-time non nullable"))
        elif type == "nullable":
            return DynamicValue(None, DateTimeType().withLabel("Date-time nullable").withNullable())
        elif type == "readOnlyNullable":
            return DynamicValue(None, DateTimeType().withLabel("Date-time read only nullable").withReadOnly().withNullable())
        elif type == "readOnlyNonNullable":
            return DynamicValue(LocalDateTime.of(2020, 1, 1, 12, 0), DateTimeType().withLabel("Date-time read only non nullable").withReadOnly())
        if type == "dateNonNullable":
            return DynamicValue(LocalDate.of(2020, 1, 1), DateTimeType().withDate().withLabel("Date non nullable"))
        elif type == "dateNullable":
            return DynamicValue(None, DateTimeType().withDate().withLabel("Date nullable").withNullable())
        elif type == "dateReadOnlyNullable":
            return DynamicValue(None, DateTimeType().withDate().withLabel("Date read only nullable").withReadOnly().withNullable())
        elif type == "dateReadOnlyNonNullable":
            return DynamicValue(LocalDate.of(2020, 1, 1), DateTimeType().withLabel("Date-time read only non nullable").withReadOnly())
        if type == "timeNonNullable":
            return DynamicValue(LocalTime.of(12, 0), DateTimeType().withTime().withFormat("HH:mm").withLabel("Time non nullable"))
        elif type == "timeNullable":
            return DynamicValue(None, DateTimeType().withTime().withFormat("HH:mm").withLabel("Time nullable").withNullable())
        elif type == "timeReadOnlyNullable":
            return DynamicValue(None, DateTimeType().withTime().withFormat("HH:mm").withLabel("Time read only nullable").withReadOnly().withNullable())
        elif type == "timeReadOnlyNonNullable":
            return DynamicValue(LocalTime.of(12, 0), DateTimeType().withTime().withFormat("HH:mm").withLabel("Time read only non nullable").withReadOnly())
        if type == "dateTimeZoneReadOnlyNullable":
            return DynamicValue(None,
                                DateTimeType().withDateTimeZone().withLabel("Date-time-zone read only nullable").withReadOnly().withNullable())
        elif type == "dateTimeZoneReadOnlyNonNullable":
            return DynamicValue(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 12, 0), ZoneId.of("America/Detroit")),
                                DateTimeType().withDateTimeZone().withLabel("Date-time-zone read only non nullable").withReadOnly())
        if type == "instantReadOnlyNullable":
            return DynamicValue(None, DateTimeType().withInstant().withLabel("Instant read only nullable").withReadOnly().withNullable())
        elif type == "instantReadOnlyNonNullable":
            return DynamicValue(Instant.now(), DateTimeType().withInstant().withLabel("Instant read only non nullable").withReadOnly())
        else:
            return None
    def onProvideArgs(self, context):
        if "type" in context.provide:
            context.provided["type"] = ProvidedValue().withValueSet([
                "nonNullable", "nullable", "readOnlyNullable", "readOnlyNonNullable",
                "dateNonNullable", "dateNullable", "dateReadOnlyNullable", "dateReadOnlyNonNullable",
                "timeNonNullable", "timeNullable", "timeReadOnlyNullable", "timeReadOnlyNonNullable",
                "dateTimeZoneReadOnlyNullable", "dateTimeZoneReadOnlyNonNullable",
                "instantReadOnlyNullable", "instantReadOnlyNonNullable",
            ])

# TODO DynamicType

class IntegerTypeArgAction(Action):
    def onConfigure(self):
        self.withLabel("Type integer arg action")
        self.withArgs([
            IntegerType("arg").withLabel("Integer non nullable").withDefaultValue(5),
            IntegerType("argNullable").withLabel("Integer nullable").withNullable(),
            IntegerType("argReadOnlyNullable").withLabel("Integer read only nullable").withReadOnly().withNullable(),
            IntegerType("argReadOnlyNonNullable").withLabel("Integer read only non nullable").withReadOnly().withDefaultValue(5),
        ]).withResult(StringType())
    def onCall(self, arg, argNullable, argReadOnlyNullable, argReadOnlyNonNullable):
        return "/".join(map(lambda x: str(x), [arg, argNullable, argReadOnlyNullable, argReadOnlyNonNullable]))

class IntegerTypeResultAction(Action):
    def onConfigure(self):
        self.withLabel("Type integer result action")
        self.withArg(StringType("type").withProvided(ProvidedMeta().withValueSet())).withResult(DynamicType())
    def onCall(self, type):
        if type == "nonNullable":
            return DynamicValue(5, IntegerType().withLabel("Integer non nullable"))
        elif type == "nullable":
            return DynamicValue(None, IntegerType().withLabel("Integer nullable").withNullable())
        elif type == "readOnlyNullable":
            return DynamicValue(None, IntegerType().withLabel("Integer read only nullable").withReadOnly().withNullable())
        elif type == "readOnlyNonNullable":
            return DynamicValue(5, IntegerType("argReadOnlyNonNullable").withLabel("Integer read only non nullable").withReadOnly())
        else:
            return None
    def onProvideArgs(self, context):
        if "type" in context.provide:
            context.provided["type"] = ProvidedValue().withValueSet(["nonNullable", "nullable", "readOnlyNullable", "readOnlyNonNullable"])

# TODO ListType

# TODO MapType

class NumberTypeArgAction(Action):
    def onConfigure(self):
        self.withLabel("Type number arg action")
        self.withArgs([
            NumberType("arg").withLabel("Number non nullable").withDefaultValue(5.5),
            NumberType("argNullable").withLabel("Number nullable").withNullable(),
            NumberType("argReadOnlyNullable").withLabel("Number read only nullable").withReadOnly().withNullable(),
            NumberType("argReadOnlyNonNullable").withLabel("Number read only non nullable").withReadOnly().withDefaultValue(5.5),
        ]).withResult(StringType())
    def onCall(self, arg, argNullable, argReadOnlyNullable, argReadOnlyNonNullable):
        return "/".join(map(lambda x: str(x), [arg, argNullable, argReadOnlyNullable, argReadOnlyNonNullable]))

class NumberTypeResultAction(Action):
    def onConfigure(self):
        self.withLabel("Type number result action")
        self.withArg(StringType("type").withProvided(ProvidedMeta().withValueSet())).withResult(DynamicType())
    def onCall(self, type):
        if type == "nonNullable":
            return DynamicValue(5.5, NumberType().withLabel("Number non nullable"))
        elif type == "nullable":
            return DynamicValue(None, NumberType().withLabel("Number nullable").withNullable())
        elif type == "readOnlyNullable":
            return DynamicValue(None, NumberType().withLabel("Number read only nullable").withReadOnly().withNullable())
        elif type == "readOnlyNonNullable":
            return DynamicValue(5.5, NumberType("argReadOnlyNonNullable").withLabel("Number read only non nullable").withReadOnly())
        else:
            return None
    def onProvideArgs(self, context):
        if "type" in context.provide:
            context.provided["type"] = ProvidedValue().withValueSet(["nonNullable", "nullable", "readOnlyNullable", "readOnlyNonNullable"])

# TODO ObjectType

# TODO RecordType

# TODO StreamType

class StringTypeArgAction(Action):
    def onConfigure(self):
        self.withLabel("Type string arg action")
        self.withArgs([
            StringType("arg").withLabel("String non nullable").withDefaultValue("TEXT"),
            StringType("argNullable").withLabel("String nullable").withNullable(),
            StringType("argReadOnlyNullable").withLabel("String read only nullable").withReadOnly().withNullable(),
            StringType("argReadOnlyNonNullable").withLabel("String read only non nullable").withReadOnly().withDefaultValue("TEXT"),
        ]).withResult(StringType())
    def onCall(self, arg, argNullable, argReadOnlyNullable, argReadOnlyNonNullable):
        return "/".join(map(lambda x: str(x), [arg, argNullable, argReadOnlyNullable, argReadOnlyNonNullable]))

class StringTypeResultAction(Action):
    def onConfigure(self):
        self.withLabel("Type string result action")
        self.withArg(StringType("type").withProvided(ProvidedMeta().withValueSet())).withResult(DynamicType())
    def onCall(self, type):
        if type == "nonNullable":
            return DynamicValue("TEXT", StringType().withLabel("String non nullable"))
        elif type == "nullable":
            return DynamicValue(None, StringType().withLabel("String nullable").withNullable())
        elif type == "readOnlyNullable":
            return DynamicValue(None, StringType().withLabel("String read only nullable").withReadOnly().withNullable())
        elif type == "readOnlyNonNullable":
            return DynamicValue("TEXT", StringType("argReadOnlyNonNullable").withLabel("String read only non nullable").withReadOnly())
        else:
            return None
    def onProvideArgs(self, context):
        if "type" in context.provide:
            context.provided["type"] = ProvidedValue().withValueSet(["nonNullable", "nullable", "readOnlyNullable", "readOnlyNonNullable"])

# TODO VoidType
