"""
Sponge Knowledge Base
Remote service configuration
"""

def onInit():
    sponge.addCategories(
        CategoryMeta("service").withLabel("My Service")
    )

def onLoad():
    sponge.selectCategory("service", lambda processor: processor.kb.name.startswith("service"))
