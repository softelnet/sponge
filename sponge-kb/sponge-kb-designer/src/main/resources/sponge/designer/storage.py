"""
Sponge Knowledge Base

WARNING: THIS IS A WORK IN PROGRESS!
"""

from org.openksavi.sponge.core.action import BaseActionMeta, BaseActionBuilder

from java.util.concurrent.atomic import AtomicInteger
from java.util.concurrent import CopyOnWriteArrayList
import re

def onInit():
    global STORAGE
    STORAGE = createStorage()

def createStorage():
    storage = Storage()

    storage.addAction(BaseActionBuilder("Echo").withLabel("Echo").withArgs([StringType("text").withLabel("Text")]).getMeta())

    return storage

class Storage:
    def __init__(self):
        self.actions = CopyOnWriteArrayList()
        self.currentId = AtomicInteger(0)

    def addAction(self, actionMeta):
        if list(filter(lambda action: action.name == actionMeta.name, self.actions)):
            raise Exception("The action {} has already been added".format(actionMeta.name))
        self.actions.add(actionMeta)

    def getAction(self, name):
        return filter(lambda action: action.name == name, self.actions)[0]

    def updateAction(self, name, actionMeta):
         action = self.getAction(name)
         action.name = actionMeta.name
         action.label = actionMeta.label
         action.description = actionMeta.description
         action.callable = actionMeta.callable
         action.activatable = actionMeta.activatable
