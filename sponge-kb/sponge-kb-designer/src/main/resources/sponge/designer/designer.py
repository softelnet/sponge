"""
Sponge Knowledge Base
Action designer

WARNING: THIS IS A WORK IN PROGRESS!
"""

from org.openksavi.sponge.core.action import BaseAction as Action
import re 

CLASS_REGEX = '^[A-Z][A-Za-z_][A-Za-z0-9_]*'
VARIABLE_REGEX = '^[A-Za-z_][A-Za-z0-9_]*'

def fullmatch(regex, string, flags=0):
    m = re.match(regex, string, flags=flags)
    if m and m.span()[1] == len(string):
        return m

def createActionRecordType(name):
    return RecordType(name).withFields([
        StringType("label").withLabel("Label").withNullable(),
        StringType("description").withLabel("Description").withNullable(),
        ListType("args").withLabel("Arguments").withElement(TypeType("arg").withAnnotated()).withFeatures({
            "createAction":SubAction("AddArgument").withArg("actionName", "/name"),
            }),
        # args - list
        # result
        # features manually
        BooleanType("callable").withLabel("Callable"),
        BooleanType("activatable").withLabel("Activatable")
])

class ActionProjects(Action):
    def onConfigure(self):
        self.withLabel("Action projects").withDescription("Action designer.")
        self.withArgs([
            ListType("actions").withLabel("Actions").withElement(StringType("action").withAnnotated()).withFeatures({
                    "createAction":SubAction("AddAction"),
                    #"readAction":SubAction("ViewAction").withArg("action", "@this"),
                    "updateAction":SubAction("UpdateAction").withArg("name", "@this"),
                    #"deleteAction":SubAction("DeleteAction").withArg("action", "@this"),
                    #"contextActions":[], Up/Down
                    "refreshable":True,
                # Provided with overwrite to allow GUI refresh.
                }).withProvided(ProvidedMeta().withValue().withOverwrite())
        ]).withNonCallable().withFeature("icon", "library")

    def onProvideArgs(self, context):
        global STORAGE
        if "actions" in context.provide:
            context.provided["actions"] = ProvidedValue().withValue(
                # Context actions are provided dynamically in an annotated value.
                map(lambda action: AnnotatedValue(action.name).withValueLabel(action.label if action.label else action.name).withFeatures({
#                     "contextActions":[
#                     ],
                    }),
                    sorted(STORAGE.actions, key = lambda action: action.label.lower() if action.label else action.name.lower()))
                    )

class AddAction(Action):
    def onConfigure(self):
        self.withLabel("Add a action")
        self.withArgs([
            StringType("name").withLabel("Name (e.g. UpperCase)"),
            StringType("label").withLabel("Label").withNullable(),
            StringType("description").withLabel("Description").withNullable(),
        ]).withNoResult()
        self.withFeatures({"visible":False, "callLabel":"Save", "cancelLabel":"Cancel", "icon":"plus-box"})

    def onCall(self, name, label, description):
        global STORAGE, CLASS_REGEX

        if fullmatch(CLASS_REGEX, name):
            builder = BaseActionBuilder(name)
            if label:
                builder.withLabel(label)
            if description:
                builder.withDescription(description)
            STORAGE.addAction(builder.getMeta())
        else:
            raise Exception("Invalid action name: " + name)

class UpdateAction(Action):
    def onConfigure(self):
        self.withLabel("Modify the action")
        self.withArgs([
            # Must set withOverwrite to replace with the current value.
            StringType("name").withAnnotated().withReadOnly(),#.withFeature("visible", False),
            createActionRecordType("action").withProvided(
                    ProvidedMeta().withValue().withOverwrite().withDependency("name")).withFields([
            ])
        ]).withNoResult()
        self.withFeatures({"visible":False, "callLabel":"Save", "cancelLabel":"Cancel", "icon":"square-edit-outline"})
    def onCall(self, name, action):
        global STORAGE
        STORAGE.updateAction(name.value, BaseActionBuilder(name.value).withLabel(action["label"]).withDescription(action["description"])
                             .withCallable(action["callable"]).withActivatable(action["activatable"]).withArgs(action["args"]).getMeta())
    def onProvideArgs(self, context):
        global STORAGE
        if "action" in context.provide:
            context.provided["action"] = ProvidedValue().withValue(self.toMap(STORAGE.getAction(context.current["name"].value)))

    def toMap(self, actionMeta):
        return {"label":actionMeta.label, "description":actionMeta.description, "callable":actionMeta.callable, "activatable":actionMeta.activatable,
                "args":list(map(lambda arg: AnnotatedValue(arg).withValueLabel(arg.name), actionMeta.args)) if actionMeta.args else []}

class AddArgument(Action):
    def onConfigure(self):
        self.withLabel("Add an argument")
        self.withArgs([
            StringType("actionName").withLabel("Action name").withAnnotated().withFeature("visible", False),
            TypeType("arg")
        ]).withNoResult()
        self.withFeatures({"visible":False, "callLabel":"Save", "cancelLabel":"Cancel", "icon":"plus-box"})

    def onCall(self, actionName, arg):
        global STORAGE, VARIABLE_REGEX

        if fullmatch(VARIABLE_REGEX, arg.name):
            STORAGE.getAction(actionName.value).args.append(arg)
        else:
            raise Exception("Invalid argument name: " + arg.name)
