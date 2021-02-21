"""
Sponge Knowledge Base
"""

from org.openksavi.sponge.examples.project.springboot.service import EmployeeService
from org.openksavi.sponge.examples.project.springboot.sponge import TypeUtils
from java.lang import Long

def getEmployeeService():
    return spring.context.getBean(EmployeeService)

class CreateEmployee(Action):
    def onConfigure(self):
        self.withLabel("Add a new employee")
        # Create an initial default instance of an employee and provide it to GUI.
        self.withArg(TypeUtils.createEmployeeRecordType("employee").withDefaultValue({})).withNoResult()
        self.withFeatures({"visible":False, "callLabel":"Save", "cancelLabel":"Cancel", "icon":"plus-box"})

    def onCall(self, employee):
        getEmployeeService().createNew(TypeUtils.createEmployeeFromMap(employee))

class UpdateEmployee(Action):
    def onConfigure(self):
        self.withLabel("Modify the employee")
        self.withArgs([
            IntegerType("employeeId").withAnnotated().withFeature("visible", False),
            # Must set withOverwrite to replace with the current value.
            TypeUtils.createEmployeeRecordType("employee").withProvided(
                    ProvidedMeta().withValue().withOverwrite().withDependency("employeeId"))
        ]).withNoResult()
        self.withFeatures({"visible":False, "callLabel":"Save", "cancelLabel":"Cancel", "icon":"square-edit-outline"})

    def onCall(self, employeeId, employee):
        getEmployeeService().update(employeeId.value, TypeUtils.createEmployeeFromMap(employee))

    def onProvideArgs(self, context):
        if "employee" in context.provide:
            employeeMap = TypeUtils.createEmployeeMap(getEmployeeService().getById(context.current["employeeId"].value))
            context.provided["employee"] = ProvidedValue().withValue(employeeMap)

class DeleteEmployee(Action):
    def onConfigure(self):
        self.withLabel("Delete the employee")
        self.withArg(IntegerType("employeeId").withAnnotated().withFeature("visible", False)).withNoResult()
        self.withFeatures({"visible":False, "callLabel":"Save", "cancelLabel":"Cancel", "icon":"delete", "confirmation":True})

    def onCall(self, employeeId):
        getEmployeeService().delete(employeeId.value)
